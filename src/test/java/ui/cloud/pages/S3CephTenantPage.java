package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.S3Ceph;
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

public class S3CephTenantPage extends IProductPage {
    private static final String BLOCK_INFO = "Общая информация";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_NAME = "Имя";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String STATUS = "Статус";
    private static final String HEADER_PATH = "Файловая система";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";


    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");
    SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");


    public S3CephTenantPage(S3Ceph product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_INFO, "Включить");
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_INFO, "Выключить");
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        new Table("Роли узла").getRowByIndex(0).scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void resetPassword() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters("default", "Сбросить пароль","Подтвердить",  () ->
        {
            Dialog dlgActions = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void delete() {
        runActionWithParameters(BLOCK_INFO, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new S3CephTenantPage.VirtualMachineTable("Статус").checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_INFO, "Перезагрузить");
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void getActualConfiguration() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_INFO, "Получить актуальную конфигурацию");
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void changeTransactionIsolation(String value) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_INFO, "Изменить default_transaction_isolation", "Подтвердить", () -> {
            DropDown.byLabel("default_transaction_isolation").select(value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value.toLowerCase(), default_transaction_isolation.getText(), "default_transaction_isolation " +
                "не соответствует установленному значению ");
    }

    public void changeMaxConnections(String value) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_INFO, "Изменить max_connections", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Изменить max_connections");
            dlg.setInputValue("max_connections", value);
        });
        btnGeneralInfo.click();
        Assertions.assertEquals(value, max_connections.getText(), "Максимальное количество подключений " +
                "не соответствует установленному значению ");
    }

    public void stopHard() {
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_INFO, "Выключить принудительно");
        checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void changeConfiguration() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_INFO, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void createDb(String name) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
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
            Assertions.assertEquals(name, new Table(HEADER_NAME).getRowByColumnValue(HEADER_NAME, name).getValueByColumn(HEADER_NAME));
        }
    }

    public void addUserDb(String nameDb, String nameUserDb, String comment) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_NAME).isColumnValueContains("", nameDb + "_" + nameUserDb))) {
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
            Assertions.assertTrue(new Table(HEADER_NAME).isColumnValueContains("", nameDb + "_" + nameUserDb), "Пользователь не существует");
            Assertions.assertEquals(nameDb, new Table(HEADER_NAME).getRowByColumnValue(HEADER_NAME, nameDb).getValueByColumn(HEADER_NAME), "БД не принадлежит пользователю");
        }
    }

    public void removeDb(String name) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name)) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithoutParameters(name, "Удалить БД");
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
        }
    }
    public void enlargeDisk(String name, String size, SelenideElement node) {

        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
    }

    public void resetPasswordUserDb(String nameUserDB) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteUserDb(String nameUser) {
        btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_NAME).isColumnValueContains("", nameUser)) {
            runActionWithParameters(BLOCK_DB_AT_USER, "Удалить пользователя", "Подтвердить", () -> {
            });
            btnUsers.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_NAME).isColumnValueContains("", BLOCK_DB_AT_USER), "Ошибка удаления пользователя БД");
        }
    }

    public SelenideElement getRoleNode() {
        return new Table("Версионирование").getRow(0).get();
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            DropDown groupsElement = DropDown.byLabel("Группы").clear();
            groups.forEach(groupsElement::select);
        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа",ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertThrows(NotFoundException.class, () -> new RoleTable().getRoleRow(role));

    }

    public void addBucket(String name,String size) {
        runActionWithParameters(BLOCK_INFO, "Добавить бакет", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить бакет");
            dlgActions.setInputValue("Имя", name);
            dlgActions.setInputValue("Макс. объем, ГБ", size);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME).isColumnValueEquals(HEADER_NAME, "de-plux-bucket"), "Ошибка создания");
        new S3CephTenantPage.VirtualMachineTable("Статус").checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void deleteBucket() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        runActionWithoutParameters("Параметры", "Удалить бакет",ActionParameters.builder().node(getRoleNode()).build());
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        Assertions.assertFalse(new Table(HEADER_NAME).isColumnValueEquals(HEADER_NAME, "de-plux-bucket"), "Ошибка удаления");
    }


    //Таблица ролей
    public class RoleTable extends Table {
        @Override
        protected void open() {
            btnGeneralInfo.click();
            getRoleNode().scrollIntoView(scrollCenter).click();
        }

        public RoleTable() {
            super("Группы");
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

        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Статус");
        }

    }
}
