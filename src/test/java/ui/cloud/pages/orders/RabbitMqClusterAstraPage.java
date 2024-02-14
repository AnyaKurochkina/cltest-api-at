package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import io.qameta.allure.Step;
import models.cloud.orderService.products.RabbitMQClusterAstra;
import models.cloud.subModels.Flavor;
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
    private static final String BLOCK_GROUP_AD_WEB = "Группы доступа на WEB интерфейс";
    private static final String HEADER_NAME_USER = "Имя";
    private static final String HEADER_GROUP = "Manager";
    private static final String HEADER_GROUPS = "Группы";
    private static final String HEADER_ROLE = "Роль";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private static final String HEADER_NAME_USER_PERMISSIONS = "Имя пользователя";
    private static final String HEADER_GROUP_AD = "Группы пользователей AD";
    private static final String HEADER_GROUP_ADMIN = "Группы прикладных администраторов AD";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    private final SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    private final SelenideElement btnUsers = $x("//button[.='Пользователи']");
    private final SelenideElement btnGroups = $x("//button[.='Группы']");
    private final SelenideElement usernameInput = Selenide.$x("//input[@name='username']");
    private final SelenideElement passwordInput = Selenide.$x("//input[@name='password']");

    public RabbitMqClusterAstraPage(RabbitMQClusterAstra product) {
        super(product);
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_CLUSTER, "Включить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).checkPreBilling(false).checkAlert(false).node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void openPointConnect() throws MalformedURLException, InterruptedException {
        String url = new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url);
        signIn(Configure.getAppProp("dev.user2"), Configure.getAppProp("dev.password"));
        Selenide.$x("//a[text()='Overview']").shouldBe(Condition.visible);
    }


    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new VirtualMachineTable("Статус").checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить");
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить принудительно");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Обновить сертификаты RabbitMQ")
    public void updateCertificate() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить сертификаты", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Балансировка очередей")
    public void reBalanceQueue() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Произвести балансировку очередей", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Синхронизировать данные кластера RabbitMQ")
    public void synchronizeData() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Синхронизировать данные кластера", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить операционную систему")
    public void updateOs() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновить операционную систему", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Вертикальное масштабирование")
    public void verticalScaling() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Вертикальное масштабирование", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Вертикальное масштабирование");
            dlg.setInputValue("Размер, Гб", "50");
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        btnGeneralInfo.click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void addUser(String nameUser, String numberApd, String numberRis) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, nameUser))) {
            runActionWithParameters(BLOCK_USERS, "Создать пользователя", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Создать пользователя");
                dlg.setInputValue("Введите номер системы APD", numberApd);
                dlg.setInputValue("Введите номер системы в RIS", numberRis);
                dlg.setInputValue("Уникальное имя клиента", nameUser);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, nameUser), "Пользователь не существует");
        }
    }

    public void checkUniquenessAddUser(String nameUser, String numberApd, String numberRis) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_USERS, "Создать пользователя", "Отмена", () -> {
            Dialog dlg = Dialog.byTitle("Создать пользователя");
            dlg.setInputValue("Введите номер системы APD", numberApd);
            dlg.setInputValue("Введите номер системы в RIS", numberRis);
            dlg.setInputValue("Уникальное имя клиента", nameUser);
            assertContains("Имя пользователя должно быть уникальным");
        }, ActionParameters.builder().checkAlert(false).build());
    }

    public void deleteUser() {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        String userName = new Table("Имя").getValueByColumnInFirstRow("Имя").getText();
        runActionWithoutParameters(getActionsMenuButton(userName, 1), "Удалить пользователя");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER, 1).isColumnValueContains(HEADER_NAME_USER, userName), "Ошибка удаления вируалного хоста");
    }

    public void createVirtualHosts(String nameHost) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost))) {
            runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Создать виртуальные хосты", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Создать виртуальные хосты");
                dlg.setInputValue("Введите имя виртуального хоста", nameHost);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка создания вируалного хоста");
        }
    }

    public void checkUniquenessСreateVirtualHosts(String nameHost) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Создать виртуальные хосты", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Создать виртуальные хосты");
            dlg.setInputValue("Введите имя виртуального хоста", nameHost);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка создания вируалного хоста");
    }

    public void deleteVirtualHosts(String nameHost) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VIRTUAL_HOSTS, "Удалить виртуальные хосты", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить виртуальные хосты");
            Select.byLabel("Выберите хосты для удаления").set(nameHost);
        });
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER, 2).isColumnValueContains(HEADER_NAME_USER, nameHost), "Ошибка удаления вируалного хоста");

    }

    public void addPermissions(String nameUser, String nameHost) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser))) {
            runActionWithParameters(BLOCK_PERMISSIONS, "Добавить права на виртуальные хосты", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить права на виртуальные хосты");
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
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_PERMISSIONS, "Редактировать права на виртуальные хосты", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Редактировать права на виртуальные хосты");
            String userName = new Table("Имя").getValueByColumnInFirstRow("Имя").getText();
            dlg.setSelectValue("Пользователь", nameUser);
            Select.byLabel("Виртуальный хост").set(nameHost);
            CheckBox.byLabel("Чтение").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка редактирования прав");
    }

    public void deletePermissions(String nameUser) {
        new VirtualMachineTable("Роли узла").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        String userName = new Table("Имя").getValueByColumnInFirstRow("Имя").getText();
        runActionWithoutParameters(getActionsMenuButton(userName, 2), "Удалить права на виртуальный хост");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка удаления прав");
    }

    public void removeDb(String name) {
        new VirtualMachineTable("Статус").checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
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

    @Step("Добавить новые группы на WEB интерфейс {group} с ролью {nameGroup}")
    public void addGroupWeb(String role, String nameGroup) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP_AD_WEB, "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            Select.byLabel(HEADER_GROUPS).set(nameGroup);
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_GROUP).isColumnValueContains(HEADER_GROUP, nameGroup), "Ошибка создания группы");
    }

    @Step("Изменить группу  доступа с ролью {role}")
    public void changeGroupWeb(String role, String group) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP_AD_WEB, "Редактировать группы доступа", "Подтвердить", () -> {
            Select.byLabel(role).set(group);
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_GROUP).isColumnValueContains(role, group), "Ошибка изменения группы");
    }

    @Step("Добавить роль {role}")
    public void addRole(String role, String group) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP_AD_WEB, "Добавить роль", "Подтвердить", () -> {
            Select.byLabel(HEADER_GROUPS).set(group);
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_GROUP).isColumnValueContains(HEADER_GROUP, group), "Ошибка добавления роли");
    }

    @Step("Удалить группы доступа {role}")
    public void deleteGroupWeb(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP_AD_WEB, "Редактировать группы доступа", "Подтвердить", () -> {
            Select.byLabel(role).clear();
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_GROUP).isColumnValueContains(HEADER_ROLE, role), "Ошибка удаления группы ");
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
