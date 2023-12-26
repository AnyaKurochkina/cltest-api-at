package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.GenericArangoDB;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.*;

import java.util.List;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class GenericArangoDBPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String POWER = "Питание";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public GenericArangoDBPage(GenericArangoDB product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }
    public void сreateSnapshot() {
        runActionWithParameters(BLOCK_VM, "Создать снапшот", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Создать снапшот");
            Select.byLabel("Срок хранения в днях").set("1");
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(getTableByHeader("Снапшоты").isColumnValueContains("Тип","snapshot"));
    }

    public void deleteSnapshot() {
        new Table("Имя",2).getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new Table("Имя").getFirstValueByColumn("Имя"), "Удалить снапшот");
        btnGeneralInfo.click();
        Assertions.assertFalse(getTableByHeader("Снапшоты").isColumnValueContains("Тип", "snapshot"));
    }

    public void changeConfiguration() {
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
        {
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
        });
        generalInfoTab.switchTo();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void delete() {
        runActionWithParameters(BLOCK_VM, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new GenericArangoDBPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new GenericArangoDBPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        });
        groups.forEach(group -> Assertions.assertTrue(new GenericArangoDBPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new GenericArangoDBPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertThrows(NotFoundException.class, () -> new GenericArangoDBPage.RoleTable().getRoleRow(role));
    }

    public void issueClientCertificate(String nameCertificate) {
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано").close();
        });
    }

    public void reInventory() {
        new GenericArangoDBPage.VirtualMachineTable(POWER).checkPowerStatus(GenericArangoDBPage.VirtualMachineTable.POWER_STATUS_ON);
            runActionWithoutParameters(BLOCK_VM, "Реинвентаризация ВМ (Linux)");
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
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
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
