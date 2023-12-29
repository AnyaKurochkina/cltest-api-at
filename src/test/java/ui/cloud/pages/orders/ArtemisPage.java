package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
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
    private final SelenideElement checkUpdateVersion = Selenide.$x("//div[text()='Будет произведено обновление версии инсталяции ВТБ-Артемис.']");
    private final SelenideElement checkTextUpdate = Selenide.$x("//span[text()='Обновление происходит с недоступностью ВТБ-Артемис.']");
    private final String lableNameClient = "Имя клиента";
    private final String lableOwnerCertificate = "subject(owner) сертификата клиента";
    private final String lableMinExpiryDelay = "min_expiry_delay";
    private final String lableMaxExpiryDelay = "max_expiry_delay";
    private final String lableAddressFullPolicy = "address full policy";
    private final String lableSlowConsumerPolicy = "slow_consumer_policy";
    private final String lableSlowConsumerThreshold = "slow_consumer_threshold";
    private final String lableTypeQueue = "тип очереди";

    public ArtemisPage(Artemis product) {
        super(product);
    }

    @Step("Аутентифика́ция")
    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
        passwordInput.submit();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(expectedStatus);
    }

    @Step("Включить")
    public void start() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_CLUSTER, "Включить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Выключить")
    public void stopSoft() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Выключить");
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Проверить конфигурацию")
    public void checkConfiguration() {
        checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).checkPreBilling(false).checkAlert(false).node(new Table(HEADER_NODE_ROLES).getRowByIndex(0)).build());
    }

    @Step("Удалить рекурсивно")
    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ArtemisPage.VirtualMachineTable("Статус").checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    @Step("Перезагрузить")
    public void restart() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить");
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Выключить принудительно")
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
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Обновление сертификатов", "Подтвердить", () -> {
            reissueCertificate.shouldBe(Condition.visible);
            checkText.shouldBe(Condition.visible);
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Отправить конфигурацию кластера на email")
    public void sendConfiguration() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Отправить конфигурацию кластера на email");
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Перезапуск кластера")
    public void resetCluster() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезапуск кластера");
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

    @Step("Включение отключение протоколов")
    public void onOffProtokol() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Включение\\отключение протоколов", "Подтвердить", () -> {
            CheckBox.byLabel("AMQP").setChecked(true);
        });
    }

    @Step("Создание сервиса")
    public void createService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Создание сервиса", "Подтвердить", () -> {
            Dialog dlgActions = Dialog.byTitle("Создание сервиса");
            dlgActions.setInputValue("Имя сервиса", nameService);
            dlgActions.setInputValue(lableOwnerCertificate, nameCertService);
            dlgActions.setInputValue(lableMinExpiryDelay, "10001");
            dlgActions.setInputValue(lableMaxExpiryDelay, "60001");
            Select.byLabel(lableAddressFullPolicy).set("FAIL");
            Select.byLabel("max size Mbytes ").set("150Mb");
            dlgActions.setInputValue("slow_consumer_check_period", "11");
            Select.byLabel(lableSlowConsumerPolicy).set("NOTIFY");
            dlgActions.setInputValue(lableSlowConsumerThreshold, "2");
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
            dlgActions.setInputValue(lableNameClient, nameClientWithOutService);
            dlgActions.setInputValue(lableOwnerCertificate, nameCertClient);
            Select.byLabel(lableTypeQueue).set("own");
            dlgActions.setInputValue(lableMinExpiryDelay, "10001");
            dlgActions.setInputValue(lableMaxExpiryDelay, "60001");
            Select.byLabel(lableAddressFullPolicy).set("FAIL");
            Select.byLabel(lableSlowConsumerPolicy).set("NOTIFY");
            dlgActions.setInputValue(lableSlowConsumerThreshold, "2");
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
            dlgActions.setInputValue(lableNameClient, nameClientWithService);
            dlgActions.setInputValue(lableOwnerCertificate, nameCertClient);
            Select.byLabel(lableTypeQueue).set("own");
            Button.byXpath("//button[contains(@class, 'array-item-add')]").click();
            Input.byXpath("//button[@title='Clear']/ancestor::div/input").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
            dlgActions.setInputValue(lableMinExpiryDelay, "10001");
            dlgActions.setInputValue(lableMaxExpiryDelay, "60001");
            Select.byLabel(lableAddressFullPolicy).set("FAIL");
            Select.byLabel(lableSlowConsumerPolicy).set("NOTIFY");
            dlgActions.setInputValue(lableSlowConsumerThreshold, "2");
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
            dlgActions.setInputValue(lableNameClient, nameClientTemporary);
            dlgActions.setInputValue(lableOwnerCertificate, nameCertClient);
            Select.byLabel("тип очереди").set("temporary");
            Input.byXpath("//button[@title='Clear']/ancestor::div/input").setValue("name_ser_vice");
            inputNameService.click();
            dlgActions.setInputValue(lableMinExpiryDelay, "10001");
            dlgActions.setInputValue(lableMaxExpiryDelay, "60001");
            Select.byLabel(lableAddressFullPolicy).set("FAIL");
            Select.byLabel(lableSlowConsumerPolicy).set("NOTIFY");
            dlgActions.setInputValue(lableSlowConsumerThreshold, "2");
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
            Select.byLabel("Список клиентов").set(nameClientWithOutService);
            Input.byXpath("(//button[@title='Clear']/ancestor::div/input)[2]").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
        });
    }


    @Step("Удаление прав доступа клиента")
    public void deleteRightClient() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление прав доступа клиента", "Подтвердить", () -> {
            Select.byLabel("Список клиентов").set(nameClientWithOutService);
            Input.byXpath("(//button[@title='Clear']/ancestor::div/input)[2]").setValue("name_ser_vice");
            Selenide.$x("//li[text()='name_ser_vice']").click();
        });
    }


    @Step("Удаление сервиса")
    public void deleteService() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnService.click();
        runActionWithParameters(BLOCK_SERVICE, "Удаление сервиса", "Подтвердить", () -> {
            Select.byLabel("Имя сервиса").set(nameService);
        });

        btnService.click();
        Assertions.assertFalse(new Table(HEADER_NAME_SERVICE, 1).isColumnValueContains(HEADER_NAME_SERVICE, nameService), "Ошибка удаления сервиса");
    }

    @Step("Удаление клиента")
    public void deleteClient() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        btnClients.click();
        runActionWithParameters(BLOCK_CLIENT, "Удаление клиента", "Подтвердить", () -> {
            Select.byLabel("Имя клиента").set(nameClientWithOutService);
        });
        btnClients.click();
        Assertions.assertFalse(getTableByHeader(HEADER_NAME_CLIENT).isColumnValueContains(HEADER_NAME_CLIENT, nameClientWithOutService), "Ошибка удаления сервиса");
    }

    @Step("Аварийное обновление сертификатов Artemis")
    public void emergencyUpdateCertificate() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CLUSTER, "Аварийное обновление сертификатов", "Подтвердить", () -> {
            checkUpdateVersion.shouldBe(Condition.visible);
            checkTextUpdate.shouldBe(Condition.visible);
        });
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Вертикальное масштабирование")
    public void verticalScaling() {
        new ArtemisPage.VirtualMachineTable(HEADER_NODE_ROLES).checkPowerStatus(ArtemisPage.VirtualMachineTable.POWER_STATUS_ON);
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
