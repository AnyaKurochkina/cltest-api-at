package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Redis;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.cloud.pages.orders.RedisAstraOrderPage.userNameRedisSentinel;
import static ui.elements.TypifiedElement.scrollCenter;

public class RedisAstraPage extends AbstractAstraPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String STATUS = "Роли узла";
    private static final String HEADER_PATH = "Файловая система";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";


    private final SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    private final SelenideElement btnUsers = $x("//button[.='Пользователи']");
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    private final SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");
    private final SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");


    @Override
    public String getVirtualTableName() {
        return BLOCK_VM;
    }

    public RedisAstraPage(Redis product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        new Table("Роли узла").getRowByIndex(0).scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void resetPassword(String name, String nameAction) {
        runActionWithParameters(getActionsMenuButton(name, 2), "Сбросить пароль", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle(nameAction);
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteUser(String name) {
        runActionWithParameters(getActionsMenuButton(name, 2), "Удалить пользователя", "Подтвердить", () -> {
        });
        btnGeneralInfo.click();
        Assertions.assertFalse(getActionsMenuButton(name, 2).exists(), "Ошибка удаления пользователя БД");
    }

    public void createUser(String nameUser) {
        runActionWithParameters(BLOCK_DB_USERS, "Создать пользователя", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Создать пользователя");
            dlg.setInputValue("Пользователь", nameUser);
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(getActionsMenuButton(nameUser).exists(), "Пользователь не существует");
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new RedisAstraPage.VirtualMachineTable("Статус").checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void updateOs() {
        runActionWithoutParameters(BLOCK_APP, "Обновить ОС");
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void restart() {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void getActualConfiguration() {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Получить актуальную конфигурацию");
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeTransactionIsolation(String value) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Изменить default_transaction_isolation", "Подтвердить", () -> {
            Select.byLabel("default_transaction_isolation").set(value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value.toLowerCase(), default_transaction_isolation.getText(), "default_transaction_isolation " +
                "не соответствует установленному значению ");
    }

    public void changeMaxConnections(String value) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Изменить max_connections", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Изменить max_connections");
            dlg.setInputValue("max_connections", value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value, max_connections.getText(), "Максимальное количество подключений " +
                "не соответствует установленному значению ");
    }

    public void stopHard() {
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void changeConfiguration() {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () -> {
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void changeConfigurationSentinel() {
        new RedisAstraPage.VirtualMachineTable().checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () -> {
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
        });
        btnGeneralInfo.click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void changeParamNotify(String param) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () -> {
            Select.byLabel("Параметр notify-keyspace-events").set(param);
        });
    }

    public void issueClientCertificate(String nameCertificate) {
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
        });
    }


    public void createDb(String name) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_LIMIT_CONNECT).isColumnValueContains("", name))) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithParameters(BLOCK_DB, "Добавить БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить БД");
                dlg.setInputValue("Имя базы данных", name);
                generatePassButton.shouldBe(Condition.enabled).click();
                Alert.green("Значение скопировано");
            });
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertEquals(name, new Table(HEADER_NAME_DB).getRowByColumnValue(HEADER_NAME_DB, name).getValueByColumn(HEADER_NAME_DB));
        }
    }

    public void addUserDb(String nameDb, String nameUserDb, String comment) {
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new RedisAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(RedisAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name)) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithoutParameters(name, "Удалить БД");
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
        }
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void resetPasswordUserDb() {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(userNameRedisSentinel, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
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
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        }, ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
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
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа", ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
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
            getRoleNode().scrollIntoView(scrollCenter).click();
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
