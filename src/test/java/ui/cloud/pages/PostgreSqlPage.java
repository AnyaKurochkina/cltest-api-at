package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.orderService.products.PostgreSQL;
import models.orderService.products.Windows;
import models.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class PostgreSqlPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_PATH = "";
    private static final String HEADER_DISK_SIZE = "";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public PostgreSqlPage(PostgreSQL product) {
        super(product);
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new PostgreSqlPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new PostgreSqlPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new PostgreSqlPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
    }

//    public void expandDisk(String name, String size) {
//        new PostgreSqlPage.VirtualMachineTable().checkPowerStatus(PostgreSqlPage.VirtualMachineTable.POWER_STATUS_ON);
//        firstVm.shouldBe(Condition.enabled).click();
//        runActionWithParameters(getBtnAction, "Расширить диск", "Подтвердить", () -> {
//            Input.byLabel("Итоговый объем дискового пространства, Гб").setValue(size);
//        });
//        btnGeneralInfo.shouldBe(Condition.enabled).click();
//        Assertions.assertEquals(size, new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_DISK_SIZE),
//                "Неверный размер диска");
//    }

    public void changeConfiguration() {
        new PostgreSqlPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_OFF);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }
    private SelenideElement getDiskMenuElement(String name) {
        return new Table(HEADER_CONNECT_STATUS).getRowElementByColumnValue(HEADER_PATH, name).$("button");
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Роли узла");
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Статус");
        }

    }
}
