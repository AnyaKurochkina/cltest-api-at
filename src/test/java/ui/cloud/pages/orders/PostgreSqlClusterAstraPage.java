package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.portalBack.AccessGroup;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class PostgreSqlClusterAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB = "at_db";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_ROLE = "Роли";
    private static final String HEADER_GROUP_COLUMN = "Роль";
    private static final String HEADER_GROUPS = "Группы";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";


    SelenideElement btnDb = $x("//button[.='Владельцы БД']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");
    SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");
    AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();

    public PostgreSqlClusterAstraPage(PostgresSQLCluster product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }


    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new PostgreSqlClusterAstraPage.VirtualMachineTable("Статус").checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void getActualConfiguration() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Получить актуальную конфигурацию");
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeTransactionIsolation(String value) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Изменить default_transaction_isolation", "Подтвердить", () -> {
            DropDown.byLabel("default_transaction_isolation").select(value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value.toLowerCase(), default_transaction_isolation.getText(), "default_transaction_isolation " +
                "не соответствует установленному значению ");
    }

    public void changeMaxConnections(String value) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Максимизировать max_connections");
        btnGeneralInfo.click();
        Assertions.assertEquals(value, max_connections.getText(), "Максимальное количество подключений " +
                "не соответствует установленному значению ");
    }

    public void updateOs() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Обновить ОС", "Подтвердить", () -> {
            CheckBox.byLabel("Я подтверждаю, что уведомлен, что в процессе выполнения действия может быть выполнена последовательная перезагрузка нод кластера").setChecked(true);
        });
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateMinorVersion() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Обновить минорную версию СУБД", "Подтвердить", () -> {
            CheckBox.byLabel("Я подтверждаю, что уведомлен, что в процессе выполнения действия может быть выполнена последовательная перезагрузка нод кластера").setChecked(true);
        });
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void changeConfiguration() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию нод СУБД", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(NewOrderPage.getFlavor(maxFlavor)));
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void createDb(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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

    public void updateExtensions(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(getHeaderBlock(name), "Актуализировать extensions");
        }
    }

    public void changeExtensions(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithParameters(name, "Изменить extensions", "Подтвердить", () -> DropDown.byXpath("//input[@spellcheck='false']/..").select("citext"));
        }
    }

    public void setLimitConnection(String quantity) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, quantity)) {
            btnGeneralInfo.click();
            runActionWithParameters(quantity, "Назначить предел подключений", "Подтвердить", () -> Input.byLabel("Предел подключений").setValue(quantity));
        }
    }

    public void deleteLimitConnection(String quantity) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, quantity)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(quantity, "Убрать предел подключений");
        }
    }

    public void addUserDb(String nameDb, String nameUserDb, String comment) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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

    public void removeDb(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            btnGeneralInfo.click();
            runActionWithoutParameters(getHeaderBlock(name), "Удалить БД");
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_NAME_DB).isColumnValueEquals("", name), "БД существует");
        }
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_APP, "Расширить точку монтирования /pg_data", "Подтвердить", () -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size));
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }


    public void resetPasswordDb() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_AT_DB_ADMIN, "Сбросить пароль", "Подтвердить", () -> {
            Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void resetPasswordUserDb(String nameUserDB) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteUserDb(String nameUser) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME_DB).isColumnValueContains("", nameUser)) {
            runActionWithParameters(BLOCK_DB_AT_USER, "Удалить пользователя", "Подтвердить", () -> {
            });
            btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_NAME_DB).isColumnValueContains("", BLOCK_DB_AT_USER), "Ошибка удаления пользователя БД");
        }
    }

    public SelenideElement getRoleNode() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").setContains(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        }, ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        btnGeneralInfo.click(); // для задержки иначе не отрабатывает 305 строка
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        }, ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа", ActionParameters.builder().waitChangeStatus(false).node(getRoleNode()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertFalse(getBtnAction(accessGroup.getPrefixName()).exists(), "Ошибка удаления админ группы");
    }
    public SelenideElement getHeaderBlock (String name)
    {
        return $x("//td[.='{}']/../descendant::button", name);
    }

    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
            getRoleNode().scrollIntoView(scrollCenter).click();
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
