package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import models.orderService.products.Windows;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Table;

public class WindowsPage extends IProductPage {

    public WindowsPage(Windows product) {
        super(product);
    }

    public void delete() {
        runActionWithParameters("Виртуальная машина", "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void start() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters("Виртуальная машина", "Включить");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void restart() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Перезагрузить по питанию");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeConfiguration() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithParameters("Виртуальная машина", "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(product.getMaxFlavor())));
    }

//    public void discActExpand() {
//        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
//        runActionWithoutParameters("Виртуальная машина", "Расширить диск");
//    }

    public void disableDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Отключить в ОС");
    }

    public void enableDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Подключить в ОС");
    }

    public void deleteDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Удалить диск");
    }

    public void checkConfiguration() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Проверить конфигурацию");
    }

    public void stopSoft() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить");
    }

    public void stopHard() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
    }

    public void addDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Дополнительные диски", "Добавить диск", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", "11");
            DropDown.byLabel("Буква").selectByValue(name);
            DropDown.byLabel("Файловая система").selectByValue("refs");
        });
    }

    private SelenideElement getDiskMenuElement(String name) {
        return new Table("Статус подключения").getRowByColumn("Путь", name).$x("descendant::button");
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
}
