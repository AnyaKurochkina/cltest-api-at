package ui.t1.tests.cloudCompute;

import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.WindowsPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static api.Tests.activeCnd;
import static ui.t1.pages.cloudCompute.IProductT1Page.BLOCK_PARAMETERS;

public class VirtualMachineTest extends AbstractComputeTest {

    @Test
    @TmsLink("982508")
    @DisplayName("Cloud Compute. Виртуальные машины")
    void vmList() {
        new IndexPage().goToVirtualMachine();
        Assertions.assertEquals(Arrays.asList("", "Имя", "Статус", "Операционная система", "Платформа", "CPU", "RAM", "Зона доступности", "Внутренний IP", "Дата создания", ""),
                new VmList.VmTable().getHeaders(), "Названия столбцов в таблице не совпадают");
    }

    @Test
    @TmsLink("1248261")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание")
    void vmCreatePage() {
        VmCreate vmPage = new IndexPage().goToVirtualMachine().addVm();
        vmPage.setName(new Generex("[a-zA-Z0-9-_]{3,10}").random())
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setBootSize(2)
                .setBootType(hddTypeOne)
                .setSubnet(Select.RANDOM_VALUE)
                .setSshKey(sshKey);
        SelenideElement button = Button.byText("Заказать").getButton();
        button.should(activeCnd);
        vmPage.setSwitchPublicIp(true);
        button.shouldNot(activeCnd);
        vmPage.setSwitchPublicIp(false)
                .setDescription(new Generex("[a-zA-Z0-9-_]{3,10}").random())
                .setDeleteOnTermination(false)
                .setFlavor(Select.RANDOM_VALUE)
                .setFlavorName(Select.RANDOM_VALUE)
                .addSecurityGroups(securityGroup);
    }

    @DisabledIfEnv("t1prod")
    @Test
    @TmsLink("1248392")
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Fedora")
    void createAltPlatform() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setImage(new SelectBox.Image("Fedora", "36"))
                .setName(getRandomName())
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        ((Vm) new VmList().selectCompute(vm.getName()).checkCreate()).delete();
    }


    @Test
    @DisplayName("Создание/Удаление ВМ c одним доп диском (auto_delete = on) boot_disk_auto_delete = off")
    void createVmWithoutBootDiskAutoDelete() {
        String name = getRandomName();
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(false)
                .setBootSize(5)
                .addDisk(name, 2, hddTypeOne, true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderId = vmPage.getOrderId();

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(project.getId());
        Assertions.assertEquals(3, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance")).findFirst().orElseThrow(
                        () -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 4 item's (nic & volume)");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);

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
    @TmsLink("1248684")
    @DisplayName("Cloud Compute. Виртуальные машины. Защита от удаления")
    void protectOrder() {
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        vmPage.switchProtectOrder(true);

        try {
            vmPage.runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
            {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refresh();
        } finally {
            vmPage.switchProtectOrder(false);
        }
        vmPage.delete();
    }

    @Test
    @TmsLinks({@TmsLink("1248845"), @TmsLink("1248846")})
    @DisplayName("Cloud Compute. Виртуальные машины. Остановить/Запустить")
    void stopAndStartVm() {
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        vmPage.runActionWithCheckCost(CompareType.LESS, vmPage::stop);
        vmPage.runActionWithCheckCost(CompareType.MORE, vmPage::start);
        vmPage.delete();
    }

    @Test
    @TmsLink("1248853")
    @DisplayName("Cloud Compute. Виртуальные машины. Подключить IP")
    void attachAndDetachIp() {
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();

        String ip = new IndexPage()
                .goToPublicIps()
                .addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        String orderIdIp = ipPage.getOrderId();

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.MORE, () -> vmPage.attachIp(ip));

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .count(), "Item ip не соответствует условиям или не найден");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage()
                .goToPublicIps()
                .selectIp(ip)
                .runActionWithCheckCost(CompareType.LESS, ipPage::delete);
    }

    @Test
    void name() {
        new IndexPage().goToVirtualMachine().getVmList().forEach(e -> {
            try {
                new IndexPage().goToVirtualMachine().selectCompute(e).delete();
            } catch (Throwable ignored) {
                TypifiedElement.refresh();
            }
        });
        new IndexPage().goToDisks().getDiskList().forEach(e -> {
            try {
                new IndexPage().goToDisks().selectDisk(e).delete();
            } catch (Throwable ignored) {
                TypifiedElement.refresh();
            }
        });
        new IndexPage().goToPublicIps().getIpList().forEach(e -> {
            try {
                new IndexPage().goToPublicIps().selectIp(e).delete();
            } catch (Throwable ignored) {
                TypifiedElement.refresh();
            }
        });
    }
}
