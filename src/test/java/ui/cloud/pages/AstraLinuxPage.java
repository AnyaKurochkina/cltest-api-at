package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Astra;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Table;

import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class AstraLinuxPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String POWER = "Питание";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public AstraLinuxPage(Astra product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void changeConfiguration() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void delete() {
        runActionWithParameters(BLOCK_VM, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            DropDown.byLabel("Роль").selectByValue(role);
            groups.forEach(group -> DropDown.byLabel("Группы").select(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new AstraLinuxPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new AstraLinuxPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            DropDown groupsElement = DropDown.byLabel("Группы").clear();
            groups.forEach(groupsElement::select);
        });
        groups.forEach(group -> Assertions.assertTrue(new AstraLinuxPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new AstraLinuxPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertThrows(NotFoundException.class, () -> new AstraLinuxPage.RoleTable().getRoleRow(role));
    }

    public void issueClientCertificate(String nameCertificate) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
        });
    }

    public void removeDb(String name) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            runActionWithoutParameters(name, "Удалить БД");
            Assertions.assertFalse(new Table("").isColumnValueEquals("", name), "БД существует");
        }
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(POWER);
        }

    }

    //Таблица ролей
    public class RoleTable extends Table {
        @Override
        protected void open() {
            btnGeneralInfo.click();
        }

        public RoleTable() {
            super("Группы");
        }

        private SelenideElement getRoleMenuElement(String name) {
            return getRoleRow(name).$("button");
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }

        private String getGroupsRole(String name) {
            return getRowByColumnValue("", name).getValueByColumn("Группы");
        }
    }
}
