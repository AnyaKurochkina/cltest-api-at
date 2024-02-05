package ui.t1.tests.engine.compute;

import core.helper.StringUtils;
import core.helper.TableChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import org.json.JSONObject;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Виртуальные машины. Действия")
@Epic("Cloud Compute")
public class VirtualMachineActionsTest extends AbstractComputeTest {
    String imageName;
    EntitySupplier<VmCreate> vmSup = lazy(() -> {
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setName(getRandomName())
                .setBootSize(4)
                .addSecurityGroups(securityGroup)
                .setSubnet(defaultSubNetwork)
                .setDescription("Описание")
                .setSshKey(sshKey);
        Button.byText("Скопировать данные формы").click();
        Alert.green("Данные успешно скопированы");
        imageName = new JSONObject(StringUtils.getClipBoardText()).getJSONObject("image").getString("name");
        vm.clickOrder();
        new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return vm;
    });

    @Test
    @TmsLink("1248448")
    @Order(1)
    @DisplayName("Cloud Compute. Виртуальные машины. Создание Образ Ubuntu")
    void orderVm() {
        VmCreate vm = vmSup.get();
        
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        final String osVersion = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getOsElement().nextItem().getText();
        Assertions.assertAll(
                () -> Assertions.assertEquals(imageName, osVersion),
                () -> Assertions.assertEquals(vm.getName(), vmPage.getNameElement().nextItem().getText()),
                () -> Assertions.assertEquals(vm.getAvailabilityZone(), vmPage.getAvailabilityZoneElement().nextItem().getText()),
                () -> Assertions.assertEquals(vm.getDescription(), vmPage.getDescriptionElement().nextItem().getText()));
    }

    @Test
    @TmsLink("982508")
    @Order(2)
    @DisplayName("Cloud Compute. Виртуальные машины (Таблица)")
    void vmList() {
        VmCreate vm = vmSup.get();
        new IndexPage().goToVirtualMachine();
        new TableChecker()
                .add("", String::isEmpty)
                .add("", String::isEmpty)
                .add(Column.NAME, e -> e.equals(vm.getName()))
                .add("Статус", e -> e.equals("Включено"))
                .add("Платформа", e -> e.length() > 5)
                .add("CPU", e -> StringUtils.isMatch("^\\d+$", e))
                .add("RAM", e -> StringUtils.isMatch("^\\d+ ГБ$", e))
                .add(Column.AVAILABILITY_ZONE, e -> e.equals(vm.getAvailabilityZone()))
                .add("Внутренний IP", e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add("Внешние IP-адреса", e -> e.equals("—"))
                .add(Column.CREATED_DATE, e -> e.length() > 5)
                .add("", String::isEmpty)
                .check(() -> new VmList.VmTable().getRowByColumnValue(Column.NAME, vm.getName()));
    }

    @Test
    @TmsLink("1248951")
    @Order(3)
    @DisplayName("Cloud Compute. Диски (Таблица)")
    void diskList() {
        VmCreate vm = vmSup.get();
        new IndexPage().goToDisks();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.NAME, e -> e.contains(vm.getName()))
                .add(Column.AVAILABILITY_ZONE, e -> e.equals(vm.getAvailabilityZone()))
                .add("Размер, ГБ", e -> e.equals(vm.getBootSize().toString()))
                .add("Виртуальная машина", e -> e.equals(vm.getName()))
                .add("Тип", e -> e.length() > 1)
                .add("Системный", e -> e.equals("Да"))
                .add(Column.CREATED_DATE, e -> e.length() > 5)
                .add("", String::isEmpty)
                .check(() -> new DiskList.DiskTable().getRowByColumnValueContains(Column.NAME, vm.getName()));
    }

    @Test
    @TmsLink("1249429")
    @Order(4)
    @DisplayName("Cloud Compute. Сетевые интерфейсы. (Таблица)")
    void networkInterfacesList() {
        VmCreate vm = vmSup.get();
        new IndexPage().goToNetworkInterfaces();
        new TableChecker()
                .add("", String::isEmpty)
                .add("", String::isEmpty)
                .add("IP адрес", e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add("MAC адрес", e -> StringUtils.isMatch("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", e))
                .add("Сеть", e -> e.length() > 4)
                .add("Подсеть", e -> e.length() > 4)
                .add("Регион", e -> e.equals(vm.getRegion()))
                .add("Группы безопасности", e -> e.equals(vm.getSecurityGroups().get(0)))
                .add(NetworkInterfaceList.NetworkInterfaceTable.COLUMN_VM, e -> e.equals(vm.getName()))
                .add(Column.CREATED_DATE, e -> e.length() > 5)
                .add("", String::isEmpty)
                .check(() -> new NetworkInterfaceList.NetworkInterfaceTable().getRowByColumnValueContains(NetworkInterfaceList.NetworkInterfaceTable.COLUMN_VM, vm.getName()));
    }

    @Test
    @Order(5)
    @TmsLink("1248684")
    @DisplayName("Cloud Compute. Виртуальные машины. Защита от удаления")
    void switchProtect() {
        VmCreate vm = vmSup.get();
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.switchProtectOrder(true);

        try {
            vmPage.runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
            {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refreshPage();
        } finally {
            vmPage.switchProtectOrder(false);
        }
    }

    @Test
    @Order(6)
    @TmsLinks({@TmsLink("1248845"), @TmsLink("1248846")})
    @DisplayName("Cloud Compute. Виртуальные машины. Остановить/Запустить")
    void stopAndStartVm() {
        VmCreate vm = vmSup.get();
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.runActionWithCheckCost(CompareType.LESS, vmPage::stop);
        vmPage.runActionWithCheckCost(CompareType.MORE, vmPage::start);
    }

    @Test
    @Order(7)
    @EnabledIfEnv("t1prod")
    @TmsLink("1248862")
    @DisplayName("Cloud Compute. Виртуальные машины. Консоль")
    void getConsoleLink() {
        VmCreate vm = vmSup.get();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).checkConsole();
    }

    @Test
    @Order(8)
    @TmsLink("1248781")
    @DisplayName("Cloud Compute. Виртуальные машины. Изменить конфигурацию")
    void resize() {
        VmCreate vm = vmSup.get();
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm.getName());
        vmPage.runActionWithCheckCost(CompareType.LESS, vmPage::stop);
        vmPage.runActionWithCheckCost(CompareType.EQUALS, () -> vmPage.resize("Intel"));
    }

    @Test
    @Order(100)
    @TmsLink("1248928")
    @DisplayName("Cloud Compute. Виртуальные машины. Удалить")
    void deleteVm() {
        VmCreate vm = vmSup.get();
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).delete();
    }
}
