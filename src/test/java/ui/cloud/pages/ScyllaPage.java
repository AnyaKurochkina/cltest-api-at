package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ScyllaDb;
import org.junit.jupiter.api.Assertions;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ScyllaPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_NAME_USER_DB = "Имя";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_DB_USERS_ROLE = "Роль";
    private static final String POWER = "Питание";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, Гб";
    private static final String HEADER_COMMENTS = "Комментарий";



    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement noDataDb = $x("(//div[contains(@class,'BoxWrapper') and descendant::div[text()='Базы данных' and not(ancestor::*[@hidden])] and descendant::*[text()='Нет данных для отображения']])[last()]");
    SelenideElement noDataUser = $x("(//div[contains(@class,'BoxWrapper') and descendant::div[text()='Пользователи' and not(ancestor::*[@hidden])] and descendant::*[text()='Нет данных для отображения']])[last()]");


    public ScyllaPage(ScyllaDb product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

  public void stopHard() {
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }


    public void createDb(String name) {
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        if (noDataDb.exists() || !(new Table("").isColumnValueEquals(HEADER_NAME_DB, name))) {
            runActionWithParameters(BLOCK_DB, "Добавить БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить БД");
                dlg.setInputValue("Имя хранилища ключей", name);
            });
            btnGeneralInfo.shouldBe(Condition.enabled).click();
            Assertions.assertTrue(new Table(HEADER_NAME_DB).isColumnValueContains(HEADER_NAME_DB, name), "БД не существует");
        }
    }

    public void addUserDb(String nameUserDb) {
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER_DB).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb))) {
            runActionWithParameters(BLOCK_DB_USERS, "Добавить пользователя", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить пользователя");
                dlg.setInputValue("Имя пользователя", nameUserDb);
                generatePassButton.shouldBe(Condition.enabled).click();
                new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
            });
        btnGeneralInfo.shouldBe(Condition.enabled).click();
            Assertions.assertTrue(new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb), "Пользователь не существует");
        }
    }

    public void removeDb(String name) {
        new ScyllaPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaPage.VirtualMachineTable.POWER_STATUS_ON);
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
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
    }

    public void resetPasswordUserDb(String nameUserDB) {
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }

    public void deleteUserDb(String nameUser) {
        if (new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUser)) {
            runActionWithoutParameters(nameUser, "Удалить пользователя");
            Assertions.assertTrue(noDataUser.exists(), "Ошибка удаления пользователя БД");
        }
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
}
