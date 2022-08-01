package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.orderService.products.PostgreSQL;
import models.orderService.products.PostgreSQLAstra;
import models.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class PostgreSqlAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_DB = "Базы данных";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_PATH = "Файловая система";
    private static final String HEADER_DISK_SIZE = "";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");


    public PostgreSqlAstraPage(PostgreSQL product) {
        super(product);
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void expandDisk(String name, String size) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        firstVm.shouldBe(Condition.enabled).click();


        runActionWithParameters(getDiskMenuElement(name), "Расширить", "Подтвердить", () -> {
            Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size);
        });
        //curentProduct
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(size, new Table(HEADER_CONNECT_STATUS).getRowByColumnValue(HEADER_PATH, name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void changeConfiguration() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        firstVm.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void createDb() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_DB, "Добавить БД");

    }



    private SelenideElement getDiskMenuElement(String name) {
        return getTableByHeader(name).getRowElementByColumnValue(HEADER_PATH, "xfs").$("button");
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
