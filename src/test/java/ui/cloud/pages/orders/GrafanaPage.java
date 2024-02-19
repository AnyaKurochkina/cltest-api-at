package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Grafana;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import java.net.MalformedURLException;
import java.util.List;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class GrafanaPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_SNAPSHOT = "Снапшоты";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String STATUS = "Статус";
    private static final String POWER = "Питание";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private final SelenideElement usernameInput = Selenide.$x("//input[@name='user']");
    private final SelenideElement passwordInput = Selenide.$x("//input[@name='password']");

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    public GrafanaPage(Grafana product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void start() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void updateOs() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Обновить ОС");
    }

    public void changeConfiguration() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
        {
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
        });
        generalInfoTab.switchTo();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void delete() {
        //new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new GrafanaPage.VirtualMachineTable(STATUS).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void reInventory() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Реинвентаризация ВМ (Linux)");
    }

    public void openPointConnect() throws MalformedURLException, InterruptedException {
        String url = new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url);
        signIn("admin", "admin");
        Selenide.$x("//span[text()='Skip']").shouldBe(Condition.visible.because("Должно отображаться сообщение")).click();
        Selenide.$x("//h1[text()='Welcome to Grafana']").shouldBe(Condition.visible.because("Должно отображаться сообщение"));
    }

    private void signIn(String user, String password) {
        usernameInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).val(user);
        passwordInput.shouldBe(Condition.visible.because("Должно отображаться сообщение")).val(password);
        passwordInput.submit();
    }

    public void сreateSnapshot() {
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Создать снапшот", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Создать снапшот");
            Select.byLabel("Срок хранения в днях").set("1");
        });
        btnGeneralInfo.click();
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        getTableByHeader(BLOCK_SNAPSHOT).asserts().checkColumnContainsValue("Тип", "snapshot");
    }

    public void deleteSnapshot() {
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        new Table("Дата удаления").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new Table("Дата удаления").getFirstValueByColumn("Имя"), "Удалить снапшот");
        btnGeneralInfo.click();
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        getTableByHeader(BLOCK_SNAPSHOT).asserts().checkColumnContainsValue("Тип", "snapshot");
    }

    public void restart() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();

        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        btnGeneralInfo.click();
        new Table("Тип").getRow(0).get().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new GrafanaPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new GrafanaPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        });
        btnGeneralInfo.click();
        new Table("Тип").getRow(0).get().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new GrafanaPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new GrafanaPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        btnGeneralInfo.click();
        new Table("Тип").getRow(0).get().scrollIntoView(scrollCenter).click();
        getTableByHeader("Роли").asserts().checkColumnContainsValue("", role);
    }

    public void resetPassword() {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(getActionsMenuButton("Пользователи", 1), "Сбросить пароль", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void issueClientCertificate(String nameCertificate) {
        new GrafanaPage.VirtualMachineTable().checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
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
        getTableByHeader("Дополнительные диски").asserts().checkColumnContainsValue(HEADER_DISK_SIZE, value);
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

    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
        }

        private SelenideElement getRoleMenuElement(String name) {
            return getRoleRow(name).$("button");
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }

        private String getGroupsRole(String name) {
            return getRowByColumnValue("", name).getValueByColumn("Группы");
        }
    }
}
