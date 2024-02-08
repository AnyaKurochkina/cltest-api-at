package ui.t1.tests.engine.compute;

import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.elements.Button;
import ui.elements.Select;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.List;
import java.util.Objects;

@Feature("Виртуальные машины")
@Epic("Cloud Compute")
public class VirtualMachineTest extends AbstractComputeTest {

    @Test
    @Tag("health_check")
    @Tag("smoke")
    @TmsLink("1248261")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание")
    void vmCreatePage() {
        VmCreate vmPage = new IndexPage().goToVirtualMachine().addVm();
        vmPage.setName(new Generex("[a-zA-Z0-9]{5,10}").random())
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setBootSize(2)
                .setRegion(region)
                .setBootType(hddTypeFirst)
                .addSecurityGroups(securityGroup)
                .setSubnet(Select.RANDOM_VALUE);
        SelenideElement button = Button.byText("Заказать").getButton();
        button.shouldNot(activeCnd);
        vmPage.setSshKey(sshKey);
        button.should(activeCnd);
        vmPage.setDescription(new Generex("[a-zA-Z0-9-_]{3,10}").random())
                .setFlavor(Select.RANDOM_VALUE)
                .setFlavorName(flavorName);
    }

    @Test
    @TmsLinks({@TmsLink("1249417"), @TmsLink("1248526")})
    @DisplayName("Создание/Удаление ВМ c одним доп диском (auto_delete = on) boot_disk_auto_delete = off")
    void createVmWithoutBootDiskAutoDelete() {
        String vmName = getRandomName();
        String extDiskName = getRandomName();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setBootSize(6)
                .addDisk(extDiskName, 2, hddTypeFirst)
                .setName(vmName)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(false),
                AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderId = vmPage.getOrderId();
        new IndexPage().goToDisks().selectDisk(extDiskName).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST);

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(getProjectId());
        Assertions.assertEquals(3, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().isEmpty())
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance"))
                        .filter(i -> i.getOrderId().equals(orderId))
                        .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 3 item's (nic & volume)");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, () -> vmPage.delete(false, extDiskName));

        final List<StateServiceSteps.ShortItem> items2 = StateServiceSteps.getItems(getProjectId());
        Assertions.assertTrue(items2.stream().noneMatch(e -> e.getOrderId().equals(orderId)), "Существуют item's с orderId=" + orderId);
        Assertions.assertEquals(1, items2.stream().filter(i -> Objects.nonNull(i.getName()))
                .filter(i -> i.getName().startsWith(vm.getName()))
                .filter(e -> {
                    if (!e.getOrderId().equals(e.getSrcOrderId()))
                        return false;
                    if (!Objects.equals(e.getSize(), vm.getBootSize()))
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).count(), "Должен быть один item с новым orderId, size и parent=null");
    }

    @Test
    @TmsLink("1248853")
    @DisplayName("Cloud Compute. Виртуальные машины. Подключить IP")
    void attachAndDetachIp() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdVm = vmPage.getOrderId();

        String ip = new IndexPage().goToPublicIps().addIp(region);
        PublicIp ipPage = new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdIp = ipPage.getOrderId();

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.MORE, () -> vmPage.attachIp(ip));

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .count(), "Item ip не соответствует условиям или не найден");
    }
}
