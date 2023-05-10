package ui.cloud.pages.orders;

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
    private static final String BLOCK_RULE = "Правила жизненного цикла";
    private static final String BLOCK_PARAM = "Параметры";
    private static final String BLOCK_VERSION = "Версионирование";
    private static final String BLOCK_USERS = "Список пользователей";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String HEADER_PREFIX = "Префикс";
    private static final String HEADER_COMPONENTS = "Компоненты";
    private static final String HEADER_NAME = "Имя";
    private static final String HEADER_NAME_RULE = "Имя правила";
    private static final String HEADER_METHOD = "methods";
    private static final String HEADER_MAX_AGE = "max_age_seconds";
    private static final String STATUS = "Статус";
    private static final String HEADER_NAME_USER = "Имя пользователя";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_LIST_POLICY = "Список политик";
    private static final String HEADER_RIGHTS = "Права";


    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement btnAccessPolicy = $x("//button[.='Политики доступа']");
    Button btnRule=Button.byText("Правила жизненного цикла");
    Button btnRuleCorse=Button.byText("Правила CORS");
    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");
    SelenideElement max_connections = $x("//div[.='max_connections']//following::p[1]");
    SelenideElement default_transaction_isolation = $x("//div[.='default_transaction_isolation']//following::p[1]");
    SelenideElement currentProduct = $x("(//span/preceding-sibling::a[text()='Интеграция приложений' or text()='Базовые вычисления' or text()='Контейнеры' or text()='Базы данных' or text()='Инструменты DevOps' or text()='Логирование' or text()='Объектное хранилище' or text()='Веб-приложения' or text()='Управление секретами' or text()='Сетевые службы']/parent::div/following-sibling::div/a)[1]");
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

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
                DropDown.byLabel("Конфигурация Core/RAM").select(NewOrderPage.getFlavor(maxFlavor)));
        btnGeneralInfo.click();
        Table table = new Table("Роли узла");
        table.getRowByIndex(0).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
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
        Assertions.assertTrue(getTableByHeader(HEADER_COMPONENTS).isColumnValueContains(HEADER_NAME,name), "Ошибка создания бакета");
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void deleteBucket() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        runActionWithoutParameters(BLOCK_PARAM, "Удалить бакет",ActionParameters.builder().node(getRoleNode()).build());
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        Assertions.assertFalse(new Table(HEADER_NAME).isColumnValueEquals(HEADER_NAME, "de-plux-bucket"), "Ошибка удаления");
    }

    public void changeSettingsBucket(String size) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        runActionWithParameters(BLOCK_PARAM, "Изменить настройки бакета", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить настройки бакета");
            dlgActions.setInputValue("Макс. объем, ГБ", size);
            CheckBox.byLabel("Версионирование").setChecked(true);
        },ActionParameters.builder().node(getRoleNode()).build());
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(WindowsPage.VirtualMachineTable.POWER_STATUS_ON);
        Assertions.assertTrue(new Table(HEADER_NAME).isColumnValueEquals(BLOCK_VERSION, "true"), "Ошибка изменения");
    }

    public void addRuLifeCycle(String name,String size) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRule.click();
        runActionWithParameters(getBtnAction("",3), "Добавить правило жизненного цикла", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить правило жизненного цикла");
            dlgActions.setInputValue("Название", name);
            dlgActions.setSelectValue("тип","Expiration");
            RadioGroup.byLabel("Условие срабатывания").select("Кол-во дней");
            dlgActions.setInputValue("Кол-во дней", size);
        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRule.click();
        Assertions.assertTrue(new Table(HEADER_NAME_RULE).isColumnValueEquals(HEADER_NAME_RULE, name), "Ошибка добавления правила ");
    }

    public void changeRuLifeCycle(String prefix,String size) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRule.click();
        runActionWithParameters(getBtnAction("",4), "Изменить правило жизненного цикла", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить правило жизненного цикла");
            dlgActions.setInputValue("Кол-во дней", size);

        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRule.click();
        Assertions.assertTrue(new Table(HEADER_PREFIX).isColumnValueEquals("Кол-во дней", size), "Ошибка изменения правила ");
    }
    public void addRuleCorse(String rule,String minute,String maxAge) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithParameters(getBtnAction("",3), "Добавить cors", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить cors");
            Input.byXpath("descendant::div/input").setValue("ruleCorse");
            CheckBox.byLabel("GET").setChecked(true);
            RadioGroup.byLabel("Access Control Max Age").select(minute);
        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isColumnValueEquals(HEADER_MAX_AGE, maxAge), "Ошибка изменения правила ");
    }

    public void changeRuleCorse(String sec,String maxAge) {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithParameters(getBtnAction("",4), "Изменить правило CORS", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить правило CORS");
            CheckBox.byLabel("PUT").setChecked(true);
            RadioGroup.byLabel("Access Control Max Age").select(sec);
        },ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isColumnValueEquals(HEADER_MAX_AGE, maxAge), "Ошибка изменения правила ");
    }
    public void deleteRuleCorse() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithoutParameters(getBtnAction("",4), "Удалить правило CORS",ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isEmpty(), "Ошибка изменения правила ");
    }

    public void deleteRuLifeCycle() {
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_ON);
        getRoleNode().click();
        btnRule.click();
        runActionWithoutParameters(getBtnAction("",4), "Удалить правило жизненного цикла",ActionParameters.builder().node(getRoleNode()).build());
        btnGeneralInfo.click();
        getRoleNode().click();
        btnRule.click();
        Assertions.assertTrue(new Table(HEADER_PREFIX).isEmpty(), "Ошибка удаления правила ");
    }

    public void addUser(String name) {
        btnUsers.click();
        runActionWithParameters(BLOCK_USERS, "Добавить пользователя", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить пользователя");
            dlgActions.setInputValue("Имя", name);
            generatePassButton1.shouldBe(Condition.enabled).click();
            generatePassButton2.shouldBe(Condition.enabled).click();
        });
        btnUsers.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER).isColumnValueEquals(HEADER_NAME_USER, name), "Ошибка создания");
    }

    public void addAccessPolicy(String name) {
        btnAccessPolicy.click();
        runActionWithParameters(HEADER_LIST_POLICY, "Добавить политику", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить пользователя");
            dlgActions.setSelectValue("Имя пользователя",name);
            dlgActions.setSelectValue("Права","Полные");
        });
        btnAccessPolicy.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER).isColumnValueEquals(HEADER_NAME_USER, name), "Ошибка создания");
    }

    public void changeAccessPolicy() {
        btnAccessPolicy.click();
        runActionWithParameters(getBtnAction("",3), "Изменить политику", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить политику");
            dlgActions.setSelectValue(HEADER_RIGHTS,"Настраиваемые");

        });
        btnAccessPolicy.click();
    }

    public void deleteUser() {
        btnUsers.click();
        runActionWithoutParameters(getBtnAction("",3), "Удалить пользователя");
        btnUsers.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER).isEmpty(), "Ошибка удаления правила ");
    }

    public void deleteTenant() {
        runActionWithParameters(BLOCK_INFO, "Удалить тенант", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new S3CephTenantPage.VirtualMachineTable(STATUS).checkPowerStatus(S3CephTenantPage.VirtualMachineTable.POWER_STATUS_DELETED);}


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
