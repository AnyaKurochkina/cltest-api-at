package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.orderService.products.PostgreSQL;
import models.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import static core.helper.StringUtils.$x;

public class PostgreSqlAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB = "at_db";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_PATH = "Файловая система";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");


    public PostgreSqlAstraPage(PostgreSQL product) {
        super(product);
    }

    @Override
    void checkPowerStatus(String expectedStatus) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
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
        runActionWithParameters(BLOCK_DB, "Добавить БД", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Добавить БД");
            dlg.setInputValue("Имя базы данных", "at_db");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }
    public void createUserDb() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_DB_USERS, "Добавить пользователя", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Добавить пользователя");
            dlg.setDropDownValue("Имя базы данных", "at_db");
            dlg.setInputValue("Имя пользователя", "at_user");
            dlg.setInputValue("Комментарий", "Пользователь для тестов");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }

    public void removeDb(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getMenuElement(HEADER_LIMIT_CONNECT,name,HEADER_SORT), "Удалить БД");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
    }

    public void setLimitConnectDb() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_AT_DB, "Назначить предел подключений", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Назначить предел подключений");
            dlg.setInputValue("Предел подключений", "23");
        });
    }
    public void removeLimitConnectDb(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getMenuElement(HEADER_LIMIT_CONNECT,name,HEADER_SORT), "Убрать предел подключений");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
    }
    public void resetPasswordDb() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_AT_DB_ADMIN, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }
    public void resetPasswordUserDb() {
        runActionWithParameters(BLOCK_DB_AT_USER, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }
    public void deletePasswordUserDb() {
        runActionWithParameters(BLOCK_DB_AT_USER, "Удалить пользователя", "Подтвердить", () -> {
        });
    }


    private SelenideElement getDiskMenuElement(String name) {
        return getTableByHeader(name).getRowElementByColumnValue(HEADER_PATH, "xfs").$("button");
    }
    private SelenideElement getMenuElement(String header, String name, String value) {
        return new Table(header).getRowElementByColumnValue(value, name).$("button");
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
