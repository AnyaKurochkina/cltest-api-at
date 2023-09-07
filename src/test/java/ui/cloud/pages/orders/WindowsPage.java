package ui.cloud.pages.orders;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Windows;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.*;

import java.util.List;

import static core.helper.StringUtils.$x;

public class WindowsPage extends IProductPage {
    private static final String BLOCK_VM = "Виртуальная машина";

    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final SelenideElement acceptCheckBoxDeletionProtect = Selenide.$x("//*[text()='Подтвердить']");
    private static final String HEADER_PATH = "Путь";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public WindowsPage(Windows product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void delete() {
        runActionWithParameters(BLOCK_VM, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void start() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_VM, "Включить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void restart() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeConfiguration() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(NewOrderPage.getFlavor(maxFlavor)));
        btnGeneralInfo.click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    @Step("Расширить диск {name} до {size}ГБ")
    public void expandDisk(String name, String size) {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(getDiskMenuElement(name), "Расширить диск", "Подтвердить",
                () -> Input.byLabel("Итоговый объем дискового пространства, Гб").setValue(size));
        btnGeneralInfo.click();
        Assertions.assertEquals(size, new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void disableDisk(String name) {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Отключить в ОС");
        btnGeneralInfo.click();
        Assertions.assertEquals("Отключен", new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name)
                .getValueByColumn(HEADER_CONNECT_STATUS));
    }

    public void enableDisk(String name) {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Подключить в ОС");
        btnGeneralInfo.click();
        Assertions.assertEquals("Подключен", new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name)
                .getValueByColumn(HEADER_CONNECT_STATUS));
    }

    public void deleteDisk(String name) {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Удалить диск");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_CONNECT_STATUS).isColumnValueEquals(HEADER_PATH, name), "Диск существует");
    }

    public void checkConfiguration() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void stopSoft() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Выключить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void stopHard() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Выключить принудительно");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void addDisk(String name, String size) {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Дополнительные диски", "Добавить диск", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", size);
            DropDown.byLabel("Буква").selectByValue(name);
            DropDown.byLabel("Файловая система").selectByValue("refs");
        });
        btnGeneralInfo.click();
        Table diskTable = new Table(HEADER_CONNECT_STATUS);
        Assertions.assertTrue(diskTable.isColumnValueEquals(HEADER_PATH, name), "Диск не существует");
        Assertions.assertAll("Проверка полей диска",
                () -> Assertions.assertEquals(size, diskTable.getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_DISK_SIZE)
                        , "Неверный размер диска"),
                () -> Assertions.assertEquals("Подключен", diskTable.getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_CONNECT_STATUS),
                        HEADER_CONNECT_STATUS)
        );
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            DropDown groupsElement = DropDown.byLabel("Группы").clear();
            groups.forEach(groupsElement::select);
        });
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertThrows(NotFoundException.class, () -> new RoleTable().getRoleRow(role));
    }

    public void addKeyAstrom() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VM, "Добавить ключ Астром", "Подтвердить", () -> {
            CheckBox.byLabel("Подтвердить").setChecked(true);
        });
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void deleteKeyAstrom() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Удалить ключ Астром");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }
    public void updateOs() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Обновить ОС");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }


    private SelenideElement getDiskMenuElement(String name) {
        return new Table(HEADER_CONNECT_STATUS).getRowElementByColumnValue(HEADER_PATH, name).$("button");
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Имя хоста");
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Питание");
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
