package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ClickHouse;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

public class ClickHousePage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_DB_OWNER = "at_user";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, Гб";


    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");

    public ClickHousePage(ClickHouse product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(expectedStatus);
    }

        public void start() {
            checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_OFF);
            runActionWithoutParameters(BLOCK_APP, "Включить");
            checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void removeDb(String name) {
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithoutParameters(name, "Удалить БД");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
    }

    public void enlargeDisk(String name, String size,SelenideElement node) {
        checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        int value = Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(String.valueOf(value), getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                String.valueOf(value)));
    }

    public void resetPasswordDb() {
        new ClickHousePage.VirtualMachineTable("Питание").checkPowerStatus(ClickHousePage.VirtualMachineTable.POWER_STATUS_ON);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_DB_OWNER, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }

    public void resetPasswordUserDb() {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_DB_USERS, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }

    public class VirtualMachineTable extends VirtualMachine {
            public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Питание");
        }

    }
}
