package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Env;
import core.utils.EnviromentChecker;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Artemis;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ArtemisPage extends IProductPage {
    private static final String BLOCK_CLUSTER = "Кластер";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_SERVICE = "Список сервисов";
    private static final String BLOCK_CLIENT = "Список клиентов";
    private static final String HEADER_NAME_SERVICE = "Имя сервиса";
    private static final String HEADER_NAME_CLIENT = "Имя клиента";
    private static final String HEADER_NODE_ROLES = "Роли узла";
    private final String nameService = "name_ser_vice";
    private final SelenideElement fieldNameService = Selenide.$x("//li[text()='name_ser_vice']");
    private final String nameClientWithOutService = "name_cli_ent_without_serv";
    private final String nameClientWithService = "name_cli_ent";
    private final String nameClientTemporary = "name_temporary";
    private final String nameCertService = "CN=cert_service";
    private final String nameCertClient = "CN=cert_client";
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement inputNameService = Selenide.$x("//li[text()='name_ser_vice']");
    private final SelenideElement btnService = $x("//button[.='Сервисы']");
    private final SelenideElement btnClients = $x("//button[.='Клиенты']");
    private final SelenideElement usernameInput = Selenide.$x("//input[@name='username']");
    private final SelenideElement passwordInput = Selenide.$x("//input[@name='password']");
    private final SelenideElement reissueCertificate = Selenide.$x("//div[text()='Будет перевыпущен кластерный сертификат ВТБ-Артемис']");
    private final SelenideElement checkText = Selenide.$x("//span[text()='Обновление происходит без недоступности ВТБ-Артемис.']");
    private final SelenideElement updateInformationCluster = Selenide.$x("//span[text()='Будет произведена синхронизация данных кластера ВТБ-Артемис, операция производится без недоступности.']");
    private final SelenideElement sendMessageWarning = Selenide.$x("//span[text()='Будет произведен экспорт конфигурации и отправка на e-mail запустившего действие.']");
    private final SelenideElement resetCluster = Selenide.$x("//span[text()='Будет произведена перезагрузка ВМ в составе кластера ВТБ-Артемис, операция производится с недоступностью.']");
    private final SelenideElement onOfProtokol = Selenide.$x("//span[text()='Будет произведено переключение протоколов кластера. В процессе переключения протоколов будет выполнен перезапуск служб.']");
    private final SelenideElement checkTextUpdate = Selenide.$x("//span[text()='Обновление происходит с недоступностью ВТБ-Артемис.']");
    private final Button addButton = Button.byXpath("//button[contains(@class, 'array-item-add')]");
    private final String nameClient = "Имя клиента";
    private final String ownerCertificate = "subject(owner) сертификата клиента";
    private final String ownerCertificateService = "subject(owner) сертификата сервиса";
    private final String minExpiryDelay = "min_expiry_delay";
    private final String maxExpiryDelay = "max_expiry_delay";
    private final Select addressFullPolicySelect = Select.byLabel("address full policy");
    private final Select maxSizeMbtesSelect = Select.byLabel("max size Mbytes ");
    private final Select slowConsumerPolicySelect = Select.byLabel("slow_consumer_policy");
    private final String slowConsumerThreshold = "slow_consumer_threshold";
    private final Select typeQueueSelect = Select.byLabel("тип очереди");
    private final Select configureCoreRamSelect = Select.byLabel("Конфигурация Core/RAM");
    private final Select listClientSelect = Select.byLabel("Список клиентов");
    private final Select nameServiceSelect = Select.byLabel("Имя сервиса");
    private final Select nameClientSelect = Select.byLabel("Имя клиента");
    private final Input getElementClear = Input.byXpath("//button[@title='Clear']/ancestor::div/input");

    public ArtemisPage(Artemis product) {
        super(product);
    }

    @Step("Аутентифика́ция")
    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).val(user);
        passwordInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).val(password);
        passwordInput.submit();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(expectedStatus);
    }

    @Step("Включение")
    public void start() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_CLUSTER, "Включить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Выключение")
    public void stopSoft() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Проверка конфигурации")
    public void checkConfiguration() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).checkPreBilling(false).checkAlert(false).node(new Table(HEADER_NODE_ROLES).getRowByIndex(0)).build());
    }

    @Step("Удаление рекурсивно")
    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ArtemisPage.VirtualMachineTable("Статус").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    @Step("Перегрузка")
    public void restart() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить");
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Выключение принудительно")
    public void stopHard() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить принудительно");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Обновление информации о кластере")
    public void updateInfCluster() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление информации о кластере", "Подтвердить", () -> {
            updateInformationCluster.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
            if (EnviromentChecker.standEnvIs(Env.PROD)) {
                CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
            }
        });
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновление сертификатов")
    public void updateCertificate() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление сертификатов", "Подтвердить", () -> {
            reissueCertificate.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
            if (EnviromentChecker.standEnvIs(Env.PROD) && product.isProd()) {
                CheckBox.byLabel("Я прочитал предупреждение и понимаю все риски выполнения данного действия").setChecked(true);
                CheckBox.byLabel("У меня есть согласованное ЗНИ").setChecked(true);
            }
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Отправить конфигурацию кластера на email")
    public void sendConfiguration() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Отправить конфигурацию кластера на email", "Подтвердить", () -> {
            sendMessageWarning.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Перезапуск кластера")
    public void resetCluster() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Перезапуск кластера", "Подтвердить", () -> {
            resetCluster.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновление версии инсталляции")
    public void updateInstallVersion() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Обновление версии инсталляции");
    }

    @Step("Обновление операционной системы на ВМ кластера")
    public void updateOsVm() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление операционной системы на ВМ кластера", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
    }

    @Step("Включение\\отключение протоколов")
    public void onOffProtokol() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        // Необходимо 4 слеша  так ка метод stringutils.format не видит обратный слеш
        runActionWithParameters(BLOCK_CLUSTER, "Включение\\\\отключение протоколов", "Подтвердить", () -> {
            CheckBox.byLabel("AMQP").setChecked(true);
            onOfProtokol.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        });
    }

    @Step("Создание сервиса")
    public void createService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Создание сервиса", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание сервиса");
            dlgActions.setInputValue("Имя сервиса", nameService);
            dlgActions.setInputValue(ownerCertificateService, nameCertService);
            dlgActions.setInputValue(minExpiryDelay, "10001");
            dlgActions.setInputValue(maxExpiryDelay, "60001");
            addressFullPolicySelect.set("FAIL");
            maxSizeMbtesSelect.set("150Mb");
            dlgActions.setInputValue("slow_consumer_check_period", "11");
            slowConsumerPolicySelect.set("NOTIFY");
            dlgActions.setInputValue(slowConsumerThreshold, "2");
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        Assertions.assertTrue(new Table(HEADER_NAME_SERVICE).isColumnValueContains(HEADER_NAME_SERVICE, nameService), "Ошибка создания сервиса");
    }

    @Step("Создание клиента (own) без сервиса")
    public void createClientWithOutService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue(nameClient, nameClientWithOutService);
            dlgActions.setInputValue(ownerCertificate, nameCertClient);
            typeQueueSelect.set("own");
            dlgActions.setInputValue(minExpiryDelay, "10001");
            dlgActions.setInputValue(maxExpiryDelay, "60001");
            addressFullPolicySelect.set("FAIL");
            slowConsumerPolicySelect.set("NOTIFY");
            dlgActions.setInputValue(slowConsumerThreshold, "2");
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание клиента (own) с сервисом")
    public void createClientWithService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue(nameClient, nameClientWithService);
            dlgActions.setInputValue(ownerCertificate, nameCertClient);
            typeQueueSelect.set("own");
            addButton.click();
            getElementClear.setValue(nameService);
            inputNameService.click();
            dlgActions.setInputValue(minExpiryDelay, "10001");
            dlgActions.setInputValue(maxExpiryDelay, "60001");
            addressFullPolicySelect.set("FAIL");
            slowConsumerPolicySelect.set("NOTIFY");
            dlgActions.setInputValue(slowConsumerThreshold, "2");
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание клиента (temporary)")
    public void createClientTemporary() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание клиента", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание клиента");
            dlgActions.setInputValue(nameClient, nameClientTemporary);
            dlgActions.setInputValue(ownerCertificate, nameCertClient);
            Select.byLabel("тип очереди").set("temporary");
            getElementClear.setValue(nameService);
            inputNameService.click();
            dlgActions.setInputValue(minExpiryDelay, "10001");
            dlgActions.setInputValue(maxExpiryDelay, "60001");
            addressFullPolicySelect.set("FAIL");
            slowConsumerPolicySelect.set("NOTIFY");
            dlgActions.setInputValue(slowConsumerThreshold, "2");
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        Assertions.assertTrue(new Table(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка создания клиента");
    }

    @Step("Создание прав доступа клиента")
    public void createRightClient() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Создание прав доступа клиента", "Подтвердить", () -> {
            listClientSelect.set(nameClientWithOutService);
            getElementClear(2).setValue(nameService);
            fieldNameService.click();
        });
    }


    @Step("Удаление прав доступа клиента")
    public void deleteRightClient() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление прав доступа клиента", "Подтвердить", () -> {
            listClientSelect.set(nameClientWithOutService);
            getElementClear(2).setValue(nameService);
            fieldNameService.click();
        });
    }


    @Step("Удаление сервиса")
    public void deleteService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Удаление сервиса", "Подтвердить", () -> {
            nameServiceSelect.set(nameService);
        });
        btnService.click();
        Assertions.assertFalse(new Table(HEADER_NAME_SERVICE, 1).isColumnValueContains(HEADER_NAME_SERVICE, nameService), "Ошибка удаления сервиса");
    }

    @Step("Удаление клиента")
    public void deleteClient() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление клиента", "Подтвердить", () -> {
            nameClientSelect.set(nameClientWithOutService);
        });
        btnClients.click();
        Assertions.assertFalse(getTableByHeader(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка удаления сервиса");
    }

    @Step("Аварийное обновление сертификатов Artemis")
    public void emergencyUpdateCertificate() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        String actionName = EnviromentChecker.standEnvIs(Env.IFT)
                ? "Аварийное обновление сертификатов Artemis"
                : "Аварийное обновление сертификатов";
        runActionWithParameters(BLOCK_CLUSTER, actionName, "Подтвердить", () -> {
            checkTextUpdate.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
            if (EnviromentChecker.standEnvIs(Env.BLUE))
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
            if (EnviromentChecker.standEnvIs(Env.PROD) && product.isProd()) {
                CheckBox.byLabel("Я прочитал предупреждение и понимаю, что я делаю").setChecked(true);
                CheckBox.byLabel("У меня есть согласованное ЗНИ").setChecked(true);
            }
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Вертикальное масштабирование")
    public void verticalScaling() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_CLUSTER, "Вертикальное масштабирование", "Подтвердить", () -> {
            configureCoreRamSelect.set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я прочитал предупреждение ниже и понимаю, что я делаю").setChecked(true);
        }, ActionParameters.builder().timeout(Duration.ofHours(2)).build());
        btnGeneralInfo.click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Горизонтальное масштабирование")
    public void horizontalScaling() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Расширить точку монтирования на ВМ кластера")
    public void enlargeDisk() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public Input getElementClear(int index) {
        return Input.byXpath("(//button[@title='Clear']/ancestor::div/input)[" + index + "]");
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
