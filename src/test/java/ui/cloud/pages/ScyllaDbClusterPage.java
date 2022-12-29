package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ScyllaDbCluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Table;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ScyllaDbClusterPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String BLOCK_ACCESS = "Доступ";
    private static final String HEADER_NAME_USER_DB = "Имя";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_DB_USERS_ROLE = "Роль";
    private static final String POWER = "Статус";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";



    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");


    public ScyllaDbClusterPage(ScyllaDbCluster product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }
    public void checkConfiguration(SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(node).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить кластер", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

  public void stopHard() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }


    public void createDb(String name) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isEmpty()) {
            runActionWithParameters(BLOCK_DB, "Добавить БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить БД");
                dlg.setInputValue("Имя хранилища ключей", name);
            });
            btnGeneralInfo.shouldBe(Condition.enabled).click();
            Assertions.assertTrue(new Table(HEADER_NAME_DB).isColumnValueContains(HEADER_NAME_DB, name), "БД не существует");
        }
    }

    public void addUserDb(String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_DB_USERS_ROLE).isEmpty()) {
            runActionWithParameters(BLOCK_DB_USERS, "Добавить пользователя", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить пользователя");
                dlg.setInputValue("Имя пользователя", nameUserDb);
                generatePassButton.shouldBe(Condition.enabled).click();
                Alert.green("Значение скопировано");
            });
        btnGeneralInfo.shouldBe(Condition.enabled).click();
            Assertions.assertTrue(new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb), "Пользователь не существует");
        }
    }

    public void addRightsUser(String nameDb,String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER_DB,2).isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb))) {
            runActionWithParameters(BLOCK_ACCESS, "Добавить права доступа пользователю БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить права доступа пользователю БД");
                DropDown.byLabel("Имя базы данных").select(nameDb);
                DropDown.byLabel("Имя пользователя").select(nameUserDb);
            });
            btnGeneralInfo.shouldBe(Condition.enabled).click();
            Assertions.assertTrue(
                    new Table(HEADER_NAME_USER_DB,2).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb), "Ошибка добавления прав доступа");
        }}

    public void deleteRightsUser(String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_USER_DB,2).isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb)) {
            runActionWithoutParameters(getBtnAction(nameUserDb, 2), "Удалить права доступа пользователю БД");
        Assertions.assertFalse(new Table(HEADER_NAME_USER_DB,2).isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb), "Ошибка удаления прав доступа");
        }
    }

    public void removeDb(String name) {
        new ScyllaDbClusterPage.VirtualMachineTable(POWER).checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            runActionWithoutParameters(name, "Удалить БД");
        Assertions.assertFalse(new Table("").isColumnValueEquals("", name), "БД существует");
        }
    }
    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        String secondSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", "/app/scylla/logs").getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        String value2 = (String.valueOf(Integer.parseInt(value) + Integer.parseInt(secondSizeDisk)));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value2));
    }

    public void resetPasswordUserDb(String nameUserDB) {
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteUserDb(String nameUser) {
        if (new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUser)) {
            runActionWithoutParameters(nameUser, "Удалить пользователя");
            Assertions.assertTrue(new Table(HEADER_DB_USERS_ROLE).isEmpty(), "Ошибка удаления пользователя БД");
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
