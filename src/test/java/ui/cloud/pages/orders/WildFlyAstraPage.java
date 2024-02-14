package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import io.qameta.allure.Step;
import models.cloud.orderService.products.WildFly;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import java.net.MalformedURLException;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class WildFlyAstraPage extends AbstractAstraPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_CERTIFICATE = "Сертификат WildFly";
    private static final String BLOCK_VM = "Виртуальные машины";
    private static final String BLOCK_GROUP = "Список групп";
    private static final String HEADER_NAME_GROUP = "Имя группы";
    private static final String HEADER_CONSOLE = "Консоль управления";
    private static final String HEADER_LIST_GROUP = "Список групп";
    private static final String HEADER_GROUP = "Группы";
    private static final String POWER = "Питание";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String STATUS = "Статус";
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement link = $x("/html/body/div[1]/div/div/div/div[2]/div[2]/div/div/div[4]/div/div[3]/div/div[1]/div/div[2]/div[1]/div[2]/div/div/table/tbody/tr/td[3]/div/a");
    private final SelenideElement actionsButton = Button.byText("Действия", 2).getButton();
    public WildFlyAstraPage(WildFly product) {
        super(product);
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Override
    public String getVirtualTableName() {
        return BLOCK_VM;
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

    public void changeConfiguration() {
        checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(getActionsMenuButton("", 2), "Вертикальное масштабирование WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение ниже, и понимаю, что я делаю").setChecked(true);
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
        });
        btnGeneralInfo.click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void openAdminConsole() throws MalformedURLException, InterruptedException {
        String url = new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url + "management", "", Configure.getAppProp("dev.user"), Configure.getAppProp("dev.password"));
        Selenide.open(url);
        $x("(//a[text()='Deployments'])[2]").shouldBe(Condition.visible);
    }

    public void delete() {
        runActionWithParameters(actionsButton, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new WildFlyAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
        //new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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
        runActionWithParameters(actionsButton, "Обновить ОС сервера", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Заменить Java Wildfly")
    public void changeJavaWildFly(String versionWildFly, String versionJava) {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(actionsButton, "Заменить Java wildfly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
            Dialog dlg = new Dialog("Заменить Java wildfly");
            //dlg.setSelectValue("Текущая версия Wildfly", versionWildFly);
            dlg.setSelectValue("Версия java, на которую требуется заменить текущую java", versionJava);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить сертификат без изменений WildFly")
    public void updateCertificateWithoutChange() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CERTIFICATE, "Обновить сертификат WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить сертификат. Глобальный тип  WildFly")
    public void updateCertificateGlobal() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CERTIFICATE, "Обновить сертификат WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
            RadioGroup.byLabel("Выберите метод").select("Добавить/изменить альтернативные имена");
            Select.byLabel("Тип балансировщика").set("Глобальный");
            Input.byLabel("Имя глобальной записи").setValue("global");
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить сертификат. Локальный тип  WildFly")
    public void updateCertificateLocal() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CERTIFICATE, "Обновить сертификат WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
            RadioGroup.byLabel("Выберите метод").select("Добавить/изменить альтернативные имена");
            Select.byLabel("Тип балансировщика").set("Локальный");
            Input.byLabel("Имя глобальной записи").setValue("local");
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Обновить сертификат. F5 тип  WildFly")
    public void updateCertificateF5() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_CERTIFICATE, "Обновить сертификат WildFly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
            RadioGroup.byLabel("Выберите метод").select("Добавить/изменить альтернативные имена");
            Select.byLabel("Тип балансировщика").set("F5");
            Button.byXpath("//button[contains(@class, 'array-item-add')]").click();
            Input.byLabel("Доменное имя балансировщика F5").setValue("f5");
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Остановить сервис Wildfly")
    public void stopService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(actionsButton, "Остановить сервис Wildfly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Запустить сервис Wildfly")
    public void startService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(actionsButton, "Запустить сервис Wildfly");
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Перезапустить сервис Wildfly")
    public void resetService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(actionsButton, "Перезапустить сервис Wildfly", "Подтвердить", () -> {
            CheckBox.byLabel("Я прочитал предупреждение и согласен с последствиями").setChecked(true);
        });
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    @Step("Синхронизировать конфигурацию сервера WildFly")
    public void synchronizeService() {
        new WildFlyAstraPage.VirtualMachineTable().checkPowerStatus(WildFlyAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(actionsButton, "Синхронизировать конфигурацию сервера WildFly");
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
