package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.PostgreSQL;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class PostgreSqlAstraPage extends AbstractAstraPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_ROLE = "Роли";
    private static final String HEADER_NAME = "Имя";
    private static final String HEADER_GROUPS = "Группы";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";
    private final Switch showDeleteSwitch = Switch.byText("Показывать удаленные");
    private final SelenideElement btnDb = $x("//button[.='Владельцы БД']");
    private final SelenideElement btnUsers = $x("//button[.='Пользователи']");
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    private final SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");


    @Override
    public String getVirtualTableName() {
        return BLOCK_VM;
    }

    public PostgreSqlAstraPage(PostgreSQL product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new PostgreSqlAstraPage.VirtualMachineTable("Статус").checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Получить актуальную конфигурацию")
    public void getActualConfiguration() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Получить актуальную конфигурацию");
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Изменить default_transaction_isolation")
    public void changeTransactionIsolation(String value) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Изменить default_transaction_isolation", "Подтвердить", () -> {
            Select.byLabel("default_transaction_isolation").set(value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value.toLowerCase(), default_transaction_isolation.getText(), "default_transaction_isolation " +
                "не соответствует установленному значению ");
    }

    @Step("Максимизировать max_connections")
    public void changeMaxConnections(String value) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Максимизировать max_connections");
        btnGeneralInfo.click();
        Assertions.assertEquals(value, max_connections.getText(), "Максимальное количество подключений " +
                "не соответствует установленному значению ");
    }

    @Step("Обновить минорную версию СУБД")
    public void updateMinorVersion() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Обновить минорную версию СУБД", "Подтвердить", () -> {
            CheckBox.byLabel("Я подтверждаю, что уведомлен, что в процессе выполнения действия может быть временная недоступность сервера").setChecked(true);
        });
    }

    public void stopHard() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Актуализировать версию СУБД")
    public void updateVersionDb() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Актуализировать версию СУБД");
    }

    @Step("Обновить ОС")
    public void updateOs() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Обновить ОС");
    }

    @Step("Добавить точку монтирования /pg_audit")
    public void addPgAudit() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Добавить точку монтирования /pg_audit");
    }

    @Step("Добавить точку монтирования /pg_walarchive")
    public void addPgWalarchive() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Добавить точку монтирования /pg_walarchive");
    }

    @Step("Добавить точку монтирования /pg_backup")
    public void adPgBackup() {
        checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Добавить точку монтирования /pg_backup");
    }

    @Step("Изменить конфигурацию")
    public void changeConfiguration() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () -> {
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
        });
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    @Step("Добавить БД")
    public void createDb(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME_DB).isColumnValueContains("", name))) {
            btnGeneralInfo.click();
            runActionWithParameters(BLOCK_APP, "Добавить БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить БД");
                dlg.setInputValue("Имя базы данных", name);
                generatePassButton.shouldBe(Condition.enabled).click();
                Alert.green("Значение скопировано");
            });
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(name, new Table(HEADER_NAME_DB).getRowByColumnValue(HEADER_NAME_DB, name).getValueByColumn(HEADER_NAME_DB));
        }
    }

    @Step("Добавить пользователя")
    public void addUserDb(String nameDb, String nameUserDb, String comment) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME_DB).isColumnValueContains("", nameDb + "_" + nameUserDb))) {
            btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithParameters(BLOCK_DB_USERS, "Добавить пользователя", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить пользователя");
                dlg.setSelectValue("Имя базы данных", nameDb);
                dlg.setInputValue("Имя пользователя", nameUserDb);
                dlg.setInputValue("Комментарий", comment);
                generatePassButton.shouldBe(Condition.enabled).click();
                Alert.green("Значение скопировано");
            });
            btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertTrue(new Table(HEADER_NAME_DB).isColumnValueContains("", nameDb + "_" + nameUserDb), "Пользователь не существует");
            Assertions.assertEquals(nameDb, new Table(HEADER_NAME_DB).getRowByColumnValue(HEADER_NAME_DB, nameDb).getValueByColumn(HEADER_NAME_DB), "БД не принадлежит пользователю");
        }
    }

    @Step("Актуализировать extensions")
    public void updateExtensions(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(getHeaderBlock(name), "Актуализировать extensions");
        }
    }

    @Step("Назначить предел подключений")
    public void setLimitConnection(String quantity) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, quantity)) {
            btnGeneralInfo.click();
            runActionWithParameters(quantity, "Назначить предел подключений", "Подтвердить", () -> Input.byLabel("Предел подключений").setValue(quantity));
        }
    }

    @Step("Убрать предел подключений")
    public void deleteLimitConnection(String quantity) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, quantity)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(quantity, "Убрать предел подключений");
        }
    }

    @Step("Изменить extensions")
    public void changeExtensions(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithParameters(getActionsMenuButton(name), "Изменить extensions", "Подтвердить", () -> DropDown.byXpath("//input[@spellcheck='false']/..").select("citext"));
        }
    }

    @Step("Удалить БД")
    public void removeDb(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(getHeaderBlock(name), "Удалить БД");
        }
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name), "БД существует");

    }

    @Step("Показать удаленные БД")
    public void showDeleteDB(String name) {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnGeneralInfo.click();
        showDeleteSwitch.setEnabled(true);
        Assertions.assertTrue(new Table(HEADER_NAME).isColumnValueEquals(HEADER_NAME, name), "БД не обнаружена");
    }

    @Step("Расширить точку монтирования /pg_data (standalone)")
    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_APP, "Расширить точку монтирования /pg_data (standalone)",
                "Подтвердить", () -> {
                    Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size);
                    CheckBox.byLabel("Я уверен, что хочу расширить точку монтирования /pg_data").setChecked(true);
                });
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    @Step("Сбросить пароль БД")
    public void resetPasswordDb() {
        new PostgreSqlAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_AT_DB_ADMIN, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    @Step("Сбросить пароль пользователя БД")
    public void resetPasswordUserDb(String nameUserDB) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    @Step("Удалить пользователя")
    public void deleteUserDb(String nameUser) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueContains("", nameUser)) {
            runActionWithParameters(BLOCK_DB_AT_USER, "Удалить пользователя", "Подтвердить", () -> {
            });
            btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_NAME_DB).isColumnValueContains("", BLOCK_DB_AT_USER), "Ошибка удаления пользователя БД");
        }
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        }, ActionParameters.builder().node(getVMElement()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        }, ActionParameters.builder().node(getVMElement()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа", ActionParameters.builder().node(getVMElement()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertThrows(NotFoundException.class, () -> new RoleTable().getRoleRow(role));
    }


    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
            getVMElement().scrollIntoView(scrollCenter).click();
        }

        private SelenideElement getRoleMenuElement(String name) {
            return getRoleRow(name).$("button");
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }

        private String getGroupsRole(String name) {
            open();
            return getRowByColumnValue("", name).getValueByColumn("Группы");
        }
    }


    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Роли узла");
        }

        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Статус");
        }

    }
}