package ui.t1.tests.engine.compute;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.tests.engine.AbstractComputeTest;

import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@BlockTests
@ExtendWith(BeforeAllExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Виртуальные машины. Действия")
public class VirtualMachineActionsTest extends AbstractComputeTest {
    VmCreate vm;

    @Test
    @TmsLink("1248448")
    @Order(1)
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Ubuntu")
    void orderVm() {
        vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(vm.getName()).checkCreate();
    }

    @Test
    @Order(2)
    @TmsLink("1248684")
    @DisplayName("Cloud Compute. Виртуальные машины. Защита от удаления")
    void switchProtect() {
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
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
    }

    @Test
    @Order(3)
    @TmsLinks({@TmsLink("1248845"), @TmsLink("1248846")})
    @DisplayName("Cloud Compute. Виртуальные машины. Остановить/Запустить")
    void stopAndStartVm() {
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.runActionWithCheckCost(CompareType.LESS, vmPage::stop);
        vmPage.runActionWithCheckCost(CompareType.MORE, vmPage::start);
    }

    @Test
    @Order(4)
    @TmsLink("1248862")
    @DisplayName("Cloud Compute. Виртуальные машины. Получить ссылку на консоль")
    void getConsoleLink() {
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.runActionWithCheckCost(CompareType.EQUALS, vmPage::getLink);
    }

    @Test
    @Order(5)
    @TmsLink("1248781")
    @DisplayName("Cloud Compute. Виртуальные машины. Изменить конфигурацию")
    void resize() {
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.runActionWithCheckCost(CompareType.LESS, vmPage::stop);
        vmPage.runActionWithCheckCost(CompareType.EQUALS, () -> vmPage.resize("AMD Ryzen 7"));
    }

    @Test
    @Order(100)
    @TmsLink("1248928")
    @DisplayName("Cloud Compute. Виртуальные машины. Удалить")
    void deleteVm() {
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).delete();
    }
}
