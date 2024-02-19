package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import models.cloud.orderService.products.ClickHouseCluster;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Table;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class ClickHouseClusterPage extends IProductPage {

    private final String userPasswordFullRight = "x7fc1GyjdMhUXXxgpGCube6jHWmn";
    private final String nameAD = "at_ad_user";
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_USER_LOCAL = "ТУЗ Локальные";
    private static final String HEADER_USER_AD = "ТУЗ AD";
    private static final String HEADER_GROUP_AD = "Группы пользователей AD";
    private static final String HEADER_GROUP_ADMIN = "Группы прикладных администраторов AD";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private final SelenideElement usernameInput = Selenide.$x("//input[@id='user']");
    private final SelenideElement passwordInput = Selenide.$x("//input[@id='password']");
    private final SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    private final SelenideElement btnUsers = $x("//button[.='Пользователи']");
    private final SelenideElement btnGroups = $x("//button[.='Группы']");
    private final SelenideElement inputWindow = Selenide.$x("//textarea[@id='query']");
    private final SelenideElement successfulСheck = Selenide.$x("//span[contains(text(), '✔')]");
    private final SelenideElement start = Selenide.$x("//button[@id='run']");

    public ClickHouseClusterPage(ClickHouseCluster product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ClickHouseClusterPage.VirtualMachineTable("Статус").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateInformationCert() {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Обновить информацию о сертификатах Clickhouse Cluster");
    }

    public void updateCertificate() {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Обновить сертификаты Clickhouse Cluster");
    }

    public void openPointConnect() {
        String url = new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url);
        inputWindow.setValue("show databases");
        signIn(getNameAD(), getUserPasswordFullRight());
        successfulСheck.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).setValue(user);
        passwordInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).setValue(password);
        start.click();
    }

    public void stopHard() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void removeDb(String name) {
        new ClickHouseClusterPage.VirtualMachineTable("Статус").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithoutParameters(name, "Удалить БД");
        btnGeneralInfo.click();
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
    }

    public void createLocalAccount(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_USER_LOCAL, "Создать локальную УЗ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Создать локальную УЗ");
            dlg.setInputValue("Имя пользователя", name);
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(new Table("", 2).isColumnValueContains("", name), "Ошибка создания УЗ");
    }

    public void resetPasswordLA(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }


    public void deleteLocalAccount(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithoutParameters(name, "Удалить локальную УЗ");
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(new Table("", 2).isColumnValueContains("", name), "Ошибка удаления УЗ");
    }

    public void addAccountAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_USER_AD, "Добавить ТУЗ AD", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить ТУЗ AD");
            dlg.setInputValue("Имя пользователя", name);
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(new Table("", 3).isColumnValueContains("", name), "Ошибка создания TУЗ АД");
    }

    public void resetPasswordAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteAccountAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Удалить ТУЗ AD", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить ТУЗ AD");
            dlg.setInputValue("Пользователь БД", name);
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(new Table("", 3).isColumnValueContains("", name), "Ошибка удаления TУЗ АД");
    }

    public void addGroupAD(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_GROUP_AD, "Добавить пользовательскую группу", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить пользовательскую группу");
            dlg.setSelectValue("Группы", nameGroup);
        });
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(new Table("").isColumnValueContains("", nameGroup), "Ошибка создания AD");
    }

    public void addGroupAdmin(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_GROUP_ADMIN, "Добавить группу администраторов", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить группу администраторов");
            dlg.setSelectValue("Группы", nameGroup);
        });
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(new Table("", 2).isColumnValueContains("", nameGroup), "Ошибка удаления AD");
    }

    public void deleteGroupAD(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithoutParameters(nameGroup, "Удалить пользовательскую группу");
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(new Table("").isColumnValueContains("", nameGroup), "Ошибка удаления AD");
    }

    public void deleteGroupAdmin(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithoutParameters(nameGroup, "Удалить админ группу");
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(new Table("", 2).isColumnValueContains("", nameGroup), "Ошибка удаления админ группы");
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
