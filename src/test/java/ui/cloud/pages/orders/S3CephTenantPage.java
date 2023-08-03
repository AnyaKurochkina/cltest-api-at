package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.S3Ceph;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import static core.helper.StringUtils.$x;

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

    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement btnAccessPolicy = $x("//button[.='Политики доступа']");
    Button btnRule = Button.byText("Правила жизненного цикла");
    Button btnRuleCorse = Button.byText("Правила CORS");
    SelenideElement generatePassButton1 = $x("//button[@aria-label='generate']");
    SelenideElement generatePassButton2 = $x("(//button[@aria-label='generate'])[2]");

    public S3CephTenantPage(S3Ceph product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {}

    public SelenideElement getRoleNode() {
        return new Table("Версионирование").getRow(0).get();
    }

    public void addBucket(String name, String size) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        runActionWithParameters(BLOCK_INFO, "Добавить бакет", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить бакет");
            dlgActions.setInputValue("Имя", name);
            dlgActions.setInputValue("Макс. объем, ГБ", size);
        });
        generalInfoTab.switchTo();
        Assertions.assertTrue(getTableByHeader(HEADER_COMPONENTS).isColumnValueContains(HEADER_NAME, name), "Ошибка создания бакета");
    }

    public void deleteBucket() {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        runActionWithoutParameters(BLOCK_PARAM, "Удалить бакет", ActionParameters.builder().node(getRoleNode()).build());
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getBtnGeneralInfo().click();
        Assertions.assertFalse(new Table(HEADER_NAME).isColumnValueEquals(HEADER_NAME, "de-plux-bucket"), "Ошибка удаления");
    }

    public void changeSettingsBucket(String size) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        runActionWithParameters(BLOCK_PARAM, "Изменить настройки бакета", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить настройки бакета");
            dlgActions.setInputValue("Макс. объем, ГБ", size);
            CheckBox.byLabel("Версионирование").setChecked(true);
        }, ActionParameters.builder().node(getRoleNode()).build());
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getBtnGeneralInfo().click();
        Assertions.assertTrue(new Table(HEADER_NAME).isColumnValueEquals(BLOCK_VERSION, "true"), "Ошибка изменения");
    }

    public void addRuLifeCycle(String name, String size) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRule.click();
        runActionWithParameters(getActionsMenuButton("", 3), "Добавить правило жизненного цикла", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить правило жизненного цикла");
            dlgActions.setInputValue("Название", name);
            dlgActions.setSelectValue("тип", "Expiration");
            RadioGroup.byLabel("Условие срабатывания").select("Кол-во дней");
            dlgActions.setInputValue("Кол-во дней", size);
        }, ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
        getRoleNode().click();
        btnRule.click();
        Assertions.assertTrue(new Table(HEADER_NAME_RULE).isColumnValueEquals(HEADER_NAME_RULE, name), "Ошибка добавления правила ");
    }

    public void changeRuLifeCycle(String prefix, String size) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRule.click();
        runActionWithParameters(getActionsMenuButton("", 4), "Изменить правило жизненного цикла", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить правило жизненного цикла");
            dlgActions.setInputValue("Кол-во дней", size);

        }, ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
        getRoleNode().click();
        btnRule.click();
        Assertions.assertTrue(new Table(HEADER_PREFIX).isColumnValueEquals("Кол-во дней", size), "Ошибка изменения правила ");
    }

    public void addCorsRule(String rule, String minute, String maxAge) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithParameters(getActionsMenuButton("", 3), "Добавить cors", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Добавить cors");
            Input.byXpath("descendant::div/input").setValue("ruleCorse");
            CheckBox.byLabel("GET").setChecked(true);
            RadioGroup.byLabel("Access Control Max Age").select(minute);
        }, ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isColumnValueEquals(HEADER_MAX_AGE, maxAge), "Ошибка изменения правила ");
    }

    public void changeCorsRule(String sec, String maxAge) {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithParameters(getActionsMenuButton("", 4), "Изменить правило CORS", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить правило CORS");
            CheckBox.byLabel("PUT").setChecked(true);
            RadioGroup.byLabel("Access Control Max Age").select(sec);
        }, ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isColumnValueEquals(HEADER_MAX_AGE, maxAge), "Ошибка изменения правила ");
    }

    public void deleteCorsRule() {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRuleCorse.click();
        runActionWithoutParameters(getActionsMenuButton("", 4), "Удалить правило CORS", ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
        getRoleNode().click();
        btnRuleCorse.click();
        Assertions.assertTrue(new Table(HEADER_METHOD).isEmpty(), "Ошибка изменения правила ");
    }

    public void deleteRuLifeCycle() {
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.SUCCESS.getStatus());
        getRoleNode().click();
        btnRule.click();
        runActionWithoutParameters(getActionsMenuButton("", 4), "Удалить правило жизненного цикла", ActionParameters.builder().node(getRoleNode()).build());
        generalInfoTab.switchTo();
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
            dlgActions.setSelectValue("Имя пользователя", name);
            dlgActions.setSelectValue("Права", "Полные");
        });
        btnAccessPolicy.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER).isColumnValueEquals(HEADER_NAME_USER, name), "Ошибка создания");
    }

    public void changeAccessPolicy() {
        btnAccessPolicy.click();
        runActionWithParameters(getActionsMenuButton("", 3), "Изменить политику", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Изменить политику");
            dlgActions.setSelectValue(HEADER_RIGHTS, "Настраиваемые");

        });
        btnAccessPolicy.click();
    }

    public void deleteUser() {
        btnUsers.click();
        runActionWithoutParameters(getActionsMenuButton("", 3), "Удалить пользователя");
        btnUsers.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER).isEmpty(), "Ошибка удаления правила ");
    }

    public void deleteTenant() {
        runActionWithParameters(BLOCK_INFO, "Удалить тенант", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new S3CephTenantPage.TopInfo().checkOrderStatus(OrderStatus.DEPROVISIONED.getStatus());
    }


    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            generalInfoTab.switchTo();
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
}
