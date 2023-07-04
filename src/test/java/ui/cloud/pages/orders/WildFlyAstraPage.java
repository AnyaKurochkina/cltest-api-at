package ui.cloud.pages.orders;
import com.codeborne.selenide.*;
import core.enums.Role;
import core.helper.Configure;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Step;
import models.cloud.orderService.products.WildFly;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Sleeper;
import ui.cloud.pages.CloudLoginPage;
import ui.elements.*;
import ui.t1.pages.productCatalog.image.MarketingInfoListPage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.function.Predicate;

import static api.Tests.clickableCnd;
import static com.codeborne.selenide.AuthenticationType.BASIC;
import static com.codeborne.selenide.AuthenticationType.BEARER;
import static com.codeborne.selenide.Selenide.*;
import static core.utils.AssertUtils.assertContains;
import static ui.elements.TypifiedElement.scrollCenter;

public class WildFlyAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_CERTIFICATE = "Сертификат WildFly";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_GROUP = "Список групп";
    private static final String HEADER_NAME_GROUP = "Имя группы";
    private static final String HEADER_CONSOLE = "Консоль управления";
    private static final String HEADER_LIST_GROUP = "Список групп";
    private static final String HEADER_GROUP = "Группы";
    private static final String POWER = "Питание";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String STATUS = "Статус";
    private final SelenideElement link = $x("/html/body/div[1]/div/div/div/div[2]/div[2]/div/div/div[4]/div/div[3]/div/div[1]/div/div[2]/div[1]/div[2]/div/div/table/tbody/tr/td[3]/div/a");

    public WildFlyAstraPage(WildFly product) {
        super(product);
    }

    public SelenideElement getRoleNode() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getRoleNode().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void openAdminConsole() throws MalformedURLException, InterruptedException {
        String url=new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url+"management", "", Configure.getAppProp("dev.user"),Configure.getAppProp("dev.password"));
        Selenide.open(url);
        $x("(//a[text()='Deployments'])[2]").shouldBe(Condition.visible);
    }

    public void delete() {
        runActionWithParameters(getBtnAction("", 2), "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void restart() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Обновить ОС сервера WildFly")
    public void updateServerOs() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(getBtnAction("", 2), "Обновить ОС сервера WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить сертификат WildFly")
    public void updateCertificate() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CERTIFICATE, "Обновить сертификат WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Остановить сервис Wildfly")
    public void stopService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(getBtnAction("", 2), "Остановить сервис Wildfly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Запустить сервис Wildfly")
    public void startService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getBtnAction("", 2), "Запустить сервис Wildfly");
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Перезапустить сервис Wildfly")
    public void resetService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getBtnAction("", 2), "Перезапустить сервис Wildfly");
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Синхронизировать конфигурацию сервера WildFly")
    public void synchronizeService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getBtnAction("", 2), "Синхронизировать конфигурацию сервера WildFly");
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Добавить новые группы WildFly {group} с ролью {nameGroup}")
    public void addGroupWildFlyAstra(String role, String nameGroup) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP, "Добавление группы WildFly", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            Select.byLabel("Группы").set(nameGroup);
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(getTableByHeader(HEADER_LIST_GROUP).isColumnValueContains(HEADER_NAME_GROUP, nameGroup), "Ошибка создания WildFly");
    }

    @Step("Удалить группу  доступа WildFly с ролью {role}")
    public void deleteGroupWildFlyAstra(String role, String nameGroup) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_GROUP, "Удаление группы WildFly", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            Select.byLabel("Имя группы").set(nameGroup);
        });
        btnGeneralInfo.click();
        Assertions.assertFalse(getTableByHeader(HEADER_LIST_GROUP).isColumnValueContains(HEADER_NAME_GROUP, nameGroup), "Ошибка удаления WildFly");
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("Устройство", "/dev/mapper/vg_02-lv_app_app").getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        String sizeComonAfterChange = String.valueOf(Integer.parseInt(value) + Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("Устройство", "/dev/mapper/vg_02-lv_app_app").getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                sizeComonAfterChange));
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
            return getPowerStatus(STATUS);
        }

    }

}
