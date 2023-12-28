package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Artemis;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.net.MalformedURLException;
import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ArtemisPage extends IProductPage {
    private static final String BLOCK_CLUSTER = "Кластер";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_SERVICE = "Список сервисов";
    private static final String BLOCK_CLIENT = "Список клиентов";
    private static final String BLOCK_VIRTUAL_HOSTS = "Виртуальные хосты";
    private static final String BLOCK_PERMISSIONS = "Права доступа";
    private static final String BLOCK_GROUP_AD_WEB = "Группы доступа на WEB интерфейс";
    private static final String HEADER_NAME_SERVICE = "Имя сервиса";
    private static final String HEADER_NAME_CLIENT = "Имя клиента";
    private static final String HEADER_GROUP = "Группа";
    private static final String HEADER_GROUPS = "Группы";
    private static final String HEADER_ROLE = "Роль";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private static final String HEADER_NAME_USER_PERMISSIONS = "Имя пользователя";
    private static final String HEADER_GROUP_AD = "Группы пользователей AD";
    private static final String HEADER_GROUP_ADMIN = "Группы прикладных администраторов AD";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private final String nameService = "name_ser_vice";
    private final String nameClientWithOutService = "name_cli_ent_without_serv";
    private final String nameClientWithService = "name_cli_ent";
    private final String nameClientTemporary = "name_temporary";
    private final String nameCertService = "CN=cert_service";
    private final String nameCertClient = "CN=cert_client";
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    private final SelenideElement btnService = $x("//button[.='Сервисы']");
    private final SelenideElement btnClients = $x("//button[.='Клиенты']");
    private final SelenideElement btnUsers = $x("//button[.='Пользователи']");
    private final SelenideElement btnGroups = $x("//button[.='Группы']");
    private final SelenideElement usernameInput = Selenide.$x("//input[@name='username']");
    private final SelenideElement passwordInput = Selenide.$x("//input[@name='password']");

    public ArtemisPage(Artemis product) {
        super(product);
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_CLUSTER, "Включить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new ArtemisPage.VirtualMachineTable("Статус").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить принудительно");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Обновление информации о кластере")
    public void updateInfCluster() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Обновление информации о кластере");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновление сертификатов")
    public void updateCertificate() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление сертификатов", "Подтвердить", () -> {
//            CheckBox.byLabel("Будет перевыпущен кластерный сертификат ВТБ-Артемис").setChecked(true);
//            CheckBox.byLabel("Обновление происходит без недоступности ВТБ-Артемис.").setChecked(true);
            Selenide.$x("//div[text()='Будет перевыпущен кластерный сертификат ВТБ-Артемис']").shouldBe(Condition.visible);
            Selenide.$x("//span[text()='Обновление происходит без недоступности ВТБ-Артемис.']").shouldBe(Condition.visible);
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Отправить конфигурацию кластера на email")
    public void sendConfiguration() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Отправить конфигурацию кластера на email");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Перезапуск кластера")
    public void resetCluster() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезапуск кластера");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновление версии инсталляции")
    public void updateInstallVersion() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Обновление версии инсталляции");
    }

    @Step("Обновление операционной системы на ВМ кластера")
    public void updateOsVm() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление операционной системы на ВМ кластера", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
    }

    @Step("Включение отключение протоколов")
    public void onOffProtokol() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Включение\\отключение протоколов", "Подтвердить", () -> {
            CheckBox.byLabel("AMQP").setChecked(true);
        });
    }

    @Step("Ре-балансировка очередей")
    public void reBalanceQueue() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Произвести балансировку очередей", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Создание сервиса")
    public void createService() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Создание сервиса", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание сервиса");
            dlgActions.setInputValue("Имя сервиса", nameService);
            dlgActions.setInputValue("subject(owner) сертификата сервиса", nameCertService);
            dlgActions.setInputValue("min_expiry_delay", "10001");
            dlgActions.setInputValue("max_expiry_delay", "60001");
            Select.byLabel("address full policy").set("FAIL");
            Select.byLabel("max size Mbytes ").set("150Mb");
            dlgActions.setInputValue("slow_consumer_check_period", "11");
            Select.byLabel("slow_consumer_policy").set("NOTIFY");
            dlgActions.setInputValue("slow_consumer_threshold", "2");
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        Assertions.assertTrue(new Table(HEADER_NAME_SERVICE).isColumnValueContains(HEADER_NAME_SERVICE, nameService), "Ошибка создания сервиса");
    }

    @Step("Создание клиента (own) без сервиса")
    public void createClientWithOutService() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue("Имя клиента", nameClientWithOutService);
            dlgActions.setInputValue("subject(owner) сертификата клиента", nameCertClient);
            Select.byLabel("тип очереди").set("own");
            dlgActions.setInputValue("min_expiry_delay", "10001");
            dlgActions.setInputValue("max_expiry_delay", "60001");
            Select.byLabel("address full policy").set("FAIL");
            Select.byLabel("slow_consumer_policy").set("NOTIFY");
            dlgActions.setInputValue("slow_consumer_threshold", "2");
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание клиента (own) с сервисом")
    public void createClientWithService() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue("Имя клиента", nameClientWithService);
            dlgActions.setInputValue("subject(owner) сертификата клиента", nameCertClient);
            Select.byLabel("тип очереди").set("own");
            Button.byXpath("//button[contains(@class, 'array-item-add')]").click();
            Input.byXpath("//button[@title='Clear']/ancestor::div/input").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
            dlgActions.setInputValue("min_expiry_delay", "10001");
            dlgActions.setInputValue("max_expiry_delay", "60001");
            Select.byLabel("address full policy").set("FAIL");
            Select.byLabel("slow_consumer_policy").set("NOTIFY");
            dlgActions.setInputValue("slow_consumer_threshold", "2");
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание клиента (temporary)")
    public void createClientTemporary() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue("Имя клиента", nameClientTemporary);
            dlgActions.setInputValue("subject(owner) сертификата клиента", nameCertClient);
            Select.byLabel("тип очереди").set("temporary");
            Input.byXpath("//button[@title='Clear']/ancestor::div/input").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
            dlgActions.setInputValue("min_expiry_delay", "10001");
            dlgActions.setInputValue("max_expiry_delay", "60001");
            Select.byLabel("address full policy").set("FAIL");
            Select.byLabel("slow_consumer_policy").set("NOTIFY");
            dlgActions.setInputValue("slow_consumer_threshold", "2");
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание прав доступа клиента")
    public void createRightClient() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание прав доступа клиента", "Подтвердить", () -> {
            Select.byLabel("Список клиентов").set(nameClientWithOutService);
            Input.byXpath("(//button[@title='Clear']/ancestor::div/input)[2]").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
        });
    }


    @Step("Удаление прав доступа клиента")
    public void deleteRightClient() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление прав доступа клиента", "Подтвердить", () -> {
            Select.byLabel("Список клиентов").set(nameClientWithOutService);
            Input.byXpath("(//button[@title='Clear']/ancestor::div/input)[2]").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
        });
    }


    @Step("Удаление сервиса")
    public void deleteService() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Удаление сервиса", "Подтвердить", () -> {
            Select.byLabel("Имя сервиса").set(nameService);
        });

        btnService.click();
        Assertions.assertFalse(new Table(HEADER_NAME_SERVICE, 1).isColumnValueContains(HEADER_NAME_SERVICE, nameService), "Ошибка удаления сервиса");
    }

    @Step("Удаление клиента")
    public void deleteClient() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление клиента", "Подтвердить", () -> {
            Select.byLabel("Имя клиента").set(nameClientWithOutService);
        });
        btnClients.click();
        Assertions.assertFalse(getTableByHeader(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка удаления сервиса");
    }

    @Step("Аварийное обновление сертификатов Artemis")
    public void emergencyUpdateCertificate() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Аварийное обновление сертификатов", "Подтвердить", () -> {
            Selenide.$x("//div[text()='Будет произведено обновление версии инсталяции ВТБ-Артемис.']").shouldBe(Condition.visible);
            Selenide.$x("//span[text()='Обновление происходит с недоступностью ВТБ-Артемис.']").shouldBe(Condition.visible);
        });
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Вертикальное масштабирование")
    public void verticalScaling() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Вертикальное масштабирование", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Вертикальное масштабирование");
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
        btnGeneralInfo.click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Горизонтальное масштабирование")
    public void horizontalScaling() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Горизонтальное масштабирование", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Горизонтальное масштабирование");
            dlg.setInputValue("Введите количество ВМ добавляемых в кластер", "50");
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
        btnGeneralInfo.click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Расширить точку монтирования на ВМ кластера")
    public void enlargeDisk() {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMinFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Расширить точку монтирования на ВМ кластера", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Расширить точку монтирования на ВМ кластера");
            dlg.setInputValue("Введите добавляемый объем (Гб) точки монтирования", "50");
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
        btnGeneralInfo.click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }


    public void addPermissions(String nameUser, String nameHost) {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_PERMISSIONS, "Редактировать права на виртуальные хосты", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Редактировать права на виртуальные хосты");
            dlg.setSelectValue("Пользователь", nameUser);
            Select.byLabel("Виртуальный хост").set(nameHost);
            CheckBox.byLabel("Чтение").setChecked(true);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка редактирования прав");

    }

    public void deletePermissions(String nameUser) {
        new ArtemisPage.VirtualMachineTable("Роли узла").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getActionsMenuButton(nameUser, 2), "Удалить права на виртуальный хост");
        btnGeneralInfo.click();
        Assertions.assertFalse(new Table(HEADER_NAME_USER_PERMISSIONS).isColumnValueContains(HEADER_NAME_USER_PERMISSIONS, nameUser), "Ошибка удаления прав");
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
        if (new Table(HEADER_GROUP).isColumnValueContains(HEADER_ROLE, role)) {
            runActionWithParameters(role, "Редактировать группы доступа", "Подтвердить", () -> {
                Select.byLabel(HEADER_GROUPS).set(group);
                CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
            });
            btnGeneralInfo.click();
            Assertions.assertTrue(new Table(HEADER_GROUP).isColumnValueContains(HEADER_ROLE, role), "Ошибка изменения группы");
        }
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
        runActionWithParameters(role, "Редактировать группы доступа", "Подтвердить", () -> {
            Select.byLabel(HEADER_GROUPS).clear();
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
