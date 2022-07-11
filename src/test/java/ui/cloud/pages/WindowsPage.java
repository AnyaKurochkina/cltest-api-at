package ui.cloud.pages;

import com.codeborne.selenide.SelenideElement;
import models.orderService.products.Windows;
import models.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class WindowsPage extends IProductPage {
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_PATH = "Путь";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

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
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters("Виртуальная машина", "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

//    public void discActExpand() {
//        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
//        runActionWithoutParameters("Виртуальная машина", "Расширить диск");
//    }

    public void disableDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Отключить в ОС");
        Assertions.assertEquals("Отключен", new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name)
                .getValueByColumn(HEADER_CONNECT_STATUS));
    }

    public void enableDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Подключить в ОС");
        Assertions.assertEquals("Подключен", new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name)
                .getValueByColumn(HEADER_CONNECT_STATUS));
    }

    public void deleteDisk(String name) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getDiskMenuElement(name), "Удалить диск");
        Assertions.assertFalse(new Table(HEADER_CONNECT_STATUS).isColumnValueExist(HEADER_PATH, name), "Диск существует");
    }

    public void checkConfiguration() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Проверить конфигурацию");
    }

    public void stopSoft() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void stopHard() {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void addDisk(String name, String size) {
        new VirtualMachineTable().checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("Дополнительные диски", "Добавить диск", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Добавить диск");
            dlg.setInputValue("Дополнительный объем дискового пространства", size);
            DropDown.byLabel("Буква").selectByValue(name);
            DropDown.byLabel("Файловая система").selectByValue("refs");
        });
        Table diskTable = new Table(HEADER_CONNECT_STATUS);
        Assertions.assertTrue(diskTable.isColumnValueExist(HEADER_PATH, name), "Диск не существует");
        Assertions.assertAll("Проверка полей диска",
                ()-> Assertions.assertEquals(size, diskTable.getRowByColumnValue(HEADER_PATH, name).getValueByColumn("Размер, ГБ")
                        , "Неверный размер диска"),
                ()-> Assertions.assertEquals("Подключен", diskTable.getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_CONNECT_STATUS),
                        HEADER_CONNECT_STATUS)
        );
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
}
