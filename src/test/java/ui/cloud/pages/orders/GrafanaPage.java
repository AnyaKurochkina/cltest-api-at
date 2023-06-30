package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.Configure;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.Grafana;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.CheckBox;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

import java.net.MalformedURLException;
import java.util.List;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class GrafanaPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_SNAPSHOT = "Снапшоты";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String POWER = "Питание";
    private static final String HEADER_CONSOLE = "Точка подключения";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    SelenideElement usernameInput = Selenide.$x("//input[@name='user']");
    SelenideElement passwordInput = Selenide.$x("//input[@name='password']");

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public GrafanaPage(Grafana product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void changeConfiguration() {
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
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
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }
    public void reInventory() {
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GenericDatabasePage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Реинвентаризация ВМ (Linux)");
    }

    public void openPointConnect() throws MalformedURLException, InterruptedException {
        String url=new Table(HEADER_CONSOLE).getValueByColumnInFirstRow(HEADER_CONSOLE).$x(".//a").getAttribute("href");
        Selenide.open(url);
        signIn(Configure.getAppProp("dev.userGrafana"),Configure.getAppProp("dev.passwordGrafana"));
        Selenide.$x("//a[text()='Grafana']").shouldBe(Condition.visible);
    }
    private void signIn(String user, String password){
        usernameInput.shouldBe(Condition.visible).val(user);
        passwordInput.shouldBe(Condition.visible).val(password);
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
        Assertions.assertTrue(getTableByHeader("Снапшоты").isColumnValueContains("Тип","snapshot"));
    }

    public void deleteSnapshot() {
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new Table("Имя").getFirstValueByColumn("Имя"), "Удалить снапшот");
        btnGeneralInfo.click();
        Assertions.assertFalse(getTableByHeader("Снапшоты").isColumnValueContains("Тип", "snapshot"));
    }

    public void restart() {
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();

        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new GrafanaPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new GrafanaPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        });
        groups.forEach(group -> Assertions.assertTrue(new GrafanaPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new GrafanaPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertThrows(NotFoundException.class, () -> new GrafanaPage.RoleTable().getRoleRow(role));
    }

    public void issueClientCertificate(String nameCertificate) {
        new GrafanaPage.VirtualMachineTable(POWER).checkPowerStatus(GrafanaPage.VirtualMachineTable.POWER_STATUS_ON);
        new Table("Имя").getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
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
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(POWER);
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
