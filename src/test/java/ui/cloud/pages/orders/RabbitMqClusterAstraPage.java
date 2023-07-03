package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import io.qameta.allure.Step;
import models.cloud.orderService.products.RabbitMQClusterAstra;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.net.MalformedURLException;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static core.utils.AssertUtils.assertContains;
import static ui.elements.TypifiedElement.scrollCenter;

public class RabbitMqClusterAstraPage extends IProductPage {
    private static final String BLOCK_CLUSTER = "Кластер";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_USERS = "Пользователи";
    private static final String BLOCK_VIRTUAL_HOSTS = "Виртуальные хосты";
    private static final String BLOCK_PERMISSIONS = "Права доступа";
    private static final String HEADER_NAME_USER = "Имя";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private static final String HEADER_NAME_USER_PERMISSIONS = "Имя пользователя";
    private static final String HEADER_GROUP_AD = "Группы пользователей AD";
    private static final String HEADER_GROUP_ADMIN = "Группы прикладных администраторов AD";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement btnGroups = $x("//button[.='Группы']");
    SelenideElement usernameInput = Selenide.$x("//input[@name='username']");
    SelenideElement passwordInput = Selenide.$x("//input[@name='password']");
    AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();

    public RabbitMqClusterAstraPage(RabbitMQClusterAstra product) {
        super(product);
    }
    private void signIn(String user, String password){
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_CLUSTER, "Включить");
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить");
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).checkPreBilling(false).checkAlert(false).node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void openPointConnect() throws MalformedURLException, InterruptedException {
        String url=new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url);
        signIn(Configure.getAppProp("dev.user2"),Configure.getAppProp("dev.password"));
        Selenide.$x("//a[text()='Overview']").shouldBe(Condition.visible);
    }


    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new RabbitMqClusterAstraPage.VirtualMachineTable("Статус").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить");
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить принудительно");
        checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Обновить сертификаты RabbitMQ")
    public void updateCertificate() {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить сертификаты RabbitMQ", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void addUser(String nameUser) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, nameUser))) {
            runActionWithParameters(BLOCK_USERS, "Создать пользователя RabbitMQ", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Создать пользователя RabbitMQ");
                dlg.setInputValue("Имя пользователя", nameUser);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, nameUser), "Пользователь не существует");
        }
    }

    public void checkUniquenessAddUser(String nameUser) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_USERS, "Создать пользователя RabbitMQ", "Отмена", () -> {
            Dialog dlg = Dialog.byTitle("Создать пользователя RabbitMQ");
            dlg.setInputValue("Имя пользователя", nameUser);
            assertContains("Имя пользователя должно быть уникальным");
        }, ActionParameters.builder().checkAlert(false).build());
    }

    public void deleteUser(String nameUser) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getBtnAction(nameUser, 1), "Удалить пользователя RabbitMQ");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, nameUser), "Ошибка удаления вируалного хоста");
    }

    public void createVirtualHosts(String nameHost) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost))) {
            runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Создать виртуальные хосты RabbitMQ", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Создать виртуальные хосты RabbitMQ");
                dlg.setInputValue("Имя виртуального хоста", nameHost);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка создания вируалного хоста");
        }
    }

    public void checkUniquenessСreateVirtualHosts(String nameHost) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Создать виртуальные хосты RabbitMQ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Создать виртуальные хосты RabbitMQ");
            dlg.setInputValue("Имя виртуального хоста", nameHost);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка создания вируалного хоста");
    }

    public void deleteVirtualHosts(String nameHost) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Удалить виртуальные хосты RabbitMQ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить виртуальные хосты RabbitMQ");
            Select.byXpath("descendant::div[label[starts-with(.,'Удаление виртуальных хостов RabbitMQ ')]]/div/input").set(nameHost);
        });
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка удаления вируалного хоста");

    }

    public void addPermissions(String nameUser, String nameHost) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser))) {
            runActionWithParameters(BLOCK_PERMISSIONS, "Добавить права на виртуальные хосты RabbitMQ", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить права на виртуальные хосты RabbitMQ");
                dlg.setSelectValue("Пользователь", nameUser);
                dlg.setSelectValue("Виртуальный хост", nameHost);
                CheckBox.byLabel("Чтение").setChecked(true);
                CheckBox.byLabel("Запись").setChecked(true);
                CheckBox.byLabel("Конфигурирование").setChecked(true);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка добавления прав");
        }
    }

    public void editPermissions(String nameUser, String nameHost) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_PERMISSIONS, "Редактировать права на виртуальные хосты RabbitMQ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Редактировать права на виртуальные хосты RabbitMQ");
            dlg.setSelectValue("Пользователь", nameUser);
            dlg.setSelectValue("Виртуальный хост", nameHost);
            CheckBox.byLabel("Чтение").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка редактирования прав");

    }

    public void deletePermissions(String nameUser) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Роли узла").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getBtnAction(nameUser, 2), "Удалить права на виртуальный хост RabbitMQ");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка удаления прав");
    }

    public void removeDb(String name) {
        new RabbitMqClusterAstraPage.VirtualMachineTable("Статус").checkPowerStatus(RabbitMqClusterAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new Table("Роли узла").getRow(0).get().scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
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

    public void openWebInterface() throws MalformedURLException, InterruptedException {
        Selenide.open("https://dlzorg-wfc001lk.corp.dev.vtb:9993/management", "", Configure.getAppProp("dev.user"),Configure.getAppProp("dev.password"));
        Selenide.open("https://dlzorg-wfc001lk.corp.dev.vtb:9993/");
        assertContains("Deployments");
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
