package ui.t1.tests.engine.compute;

import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NotFoundException;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.elements.Button;
import ui.elements.Select;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.SelectBox;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.List;
import java.util.Objects;

import static core.utils.AssertUtils.assertHeaders;

@ExtendWith(BeforeAllExtension.class)
@Feature("Виртуальные машины")
@Epic("Cloud Compute")
public class VirtualMachineTest extends AbstractComputeTest {

    @Test
    @TmsLink("982508")
    @DisplayName("Cloud Compute. Виртуальные машины")
    void vmList() {
        new IndexPage().goToVirtualMachine();
        assertHeaders(new VmList.VmTable(), "", "Имя", "Статус", "Платформа", "CPU", "RAM", "Зона доступности", "Внутренний IP", "Внешние IP-адреса", "Дата создания", "");
    }

    @Test
    @Tag("health_check")
    @TmsLink("1248261")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание")
    void vmCreatePage() {
        VmCreate vmPage = new IndexPage().goToVirtualMachine().addVm();
        vmPage.setName(new Generex("[a-zA-Z0-9-_]{3,10}").random())
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setBootSize(2)
                .setBootType(hddTypeFirst)
                .addSecurityGroups(securityGroup)
                .setSubnet(Select.RANDOM_VALUE);
        SelenideElement button = Button.byText("Заказать").getButton();
        button.shouldNot(activeCnd);
        vmPage.setSshKey(sshKey);
        button.should(activeCnd);
        vmPage.setDescription(new Generex("[a-zA-Z0-9-_]{3,10}").random())
                .setDeleteOnTermination(false)
                .setFlavor(Select.RANDOM_VALUE)
                .setFlavorName(flavorName);
    }

    @DisabledIfEnv("t1prod")
    @Test
    @TmsLink("1248392")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Fedora")
    void createAltPlatform() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(new SelectBox.Image("Fedora", "36"))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).checkCreate().delete();
    }

    @Test
    @TmsLinks({@TmsLink("1249417"), @TmsLink("1248526")})
    @DisplayName("Создание/Удаление ВМ c одним доп диском (auto_delete = on) boot_disk_auto_delete = off")
    void createVmWithoutBootDiskAutoDelete() {
        String name = getRandomName();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(false)
                .setBootSize(5)
                .addDisk(name, 2, hddTypeFirst, true)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderId = vmPage.getOrderId();

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(project.getId());
        Assertions.assertEquals(3, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance"))
                        .filter(i -> i.getOrderId().equals(orderId))
                        .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 4 item's (nic & volume)");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);

        final List<StateServiceSteps.ShortItem> items2 = StateServiceSteps.getItems(project.getId());
        Assertions.assertTrue(items2.stream().noneMatch(e -> e.getOrderId().equals(orderId)), "Существуют item's с orderId=" + orderId);
        Assertions.assertEquals(1, items2.stream().filter(i -> Objects.nonNull(i.getName()))
                .filter(i -> i.getName().startsWith(vm.getName()))
                .filter(e -> {
                    if (!e.getOrderId().equals(e.getSrcOrderId()))
                        return false;
                    if (!Objects.equals(e.getSize(), vm.getBootSize()))
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).count(), "Должен быть один item с новим orderId, size и parent=null");

        new IndexPage().goToDisks().selectDisk(name).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @TmsLink("1248853")
    @DisplayName("Cloud Compute. Виртуальные машины. Подключить IP")
    void attachAndDetachIp() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();

        String ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        createdIpList.add(ip);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        String orderIdIp = ipPage.getOrderId();

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.MORE, () -> vmPage.attachIp(ip));

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .count(), "Item ip не соответствует условиям или не найден");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.LESS, ipPage::delete);
    }
}
