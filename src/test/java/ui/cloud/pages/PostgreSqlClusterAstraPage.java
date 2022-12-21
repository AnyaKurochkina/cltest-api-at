package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.PostgresSQLCluster;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
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
    private static final String HEADER_PATH = "Файловая система";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";


    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");
    SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");


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
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(value.toLowerCase(), default_transaction_isolation.getText(), "default_transaction_isolation " +
                "не соответствует установленному значению ");
    }

    public void changeMaxConnections(String value) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Максимизировать max_connections");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(value, max_connections.getText(), "Максимальное количество подключений " +
                "не соответствует установленному значению ");
    }

    public void stopHard() {
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void changeConfiguration() {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_APP, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void createDb(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (!(new Table(HEADER_LIMIT_CONNECT).isColumnValueContains("", name))) {
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
    public void updateExtensions(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name)) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithoutParameters(name, "Актуализировать extensions");
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }
    }
    public void changeExtensions(String name) {
        new PostgreSqlClusterAstraPage.VirtualMachineTable().checkPowerStatus(PostgreSqlAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name)) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithParameters(name, "Изменить extensions", "Подтвердить", () -> DropDown.byXpath("//input[@spellcheck='false']/..").select("citext"));
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
        if (new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name)) {
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            runActionWithoutParameters(name, "Удалить БД");
            btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
            Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
        }
    }
    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        runActionWithParameters(BLOCK_APP, "Расширить точку монтирования /pg_data","Подтвердить",() -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size));
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
//        Assertions.assertTrue(new Table(HEADER_CONNECT_STATUS).isColumnValueContains(HEADER_DISK_SIZE,
//                value));
    }
//    public void enlargeDisk(String name, String size, SelenideElement node) {
//        node.scrollIntoView(scrollCenter).click();
//        String firstSizeDisk = String.valueOf(Integer.parseInt(getTableByHeader("Дополнительные точки монтирования")
//                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE))+
//                Integer.parseInt(getTableByHeader("Дополнительные точки монтирования")
//                        .getRowByColumnValue("", "/app/etcd").getValueByColumn(HEADER_DISK_SIZE)));
//        String secondSizeDisk = String.valueOf(Integer.parseInt(getTableByHeader("Дополнительные точки монтирования")
//                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE)));
//        currentProduct.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
//        runActionWithParameters(BLOCK_APP, "Расширить точку монтирования /pg_data","Подтвердить",() -> Input.byLabel("Дополнительный объем дискового пространства, Гб").setValue(size));
//        btnGeneralInfo.shouldBe(Condition.enabled).click();
//        node.scrollIntoView(scrollCenter).click();
//        int value = Integer.parseInt(secondSizeDisk) +
//                Integer.parseInt(size);
//        int value2 = Integer.parseInt(firstSizeDisk) +
//                Integer.parseInt(size);
//        Assertions.assertEquals(String.valueOf(value), getTableByHeader("Дополнительные точки монтирования")
//                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
//                "Неверный размер диска");
//        Assertions.assertTrue(new Table(HEADER_CONNECT_STATUS).isColumnValueContains(HEADER_DISK_SIZE,
//                String.valueOf(value2)));
//    }

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
