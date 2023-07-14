package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Podman;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.cloud.tests.ActionParameters;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Select;
import ui.elements.Table;

import java.util.List;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class PodmanPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String POWER = "Статус";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public PodmanPage(Podman product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration(SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).node(node).build());
    }

    public void changeConfiguration() {
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
                Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor)));
        generalInfoTab.switchTo();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").setContains(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        }, ActionParameters.builder().node(node).build());
        generalInfoTab.switchTo();
        node.scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new PodmanPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new PodmanPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            DropDown groupsElement = DropDown.byLabel("Группы").clear();
            groups.forEach(groupsElement::select);
        }, ActionParameters.builder().node(node).build());
        generalInfoTab.switchTo();
        node.scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new PodmanPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new PodmanPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа", ActionParameters.builder().node(node).build());
        generalInfoTab.switchTo();
        node.scrollIntoView(scrollCenter).click();
        Assertions.assertThrows(NotFoundException.class, () -> new PodmanPage.RoleTable().getRoleRow(role));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    public void issueClientCertificate(String nameCertificate) {
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
        });
    }

    public void removeDb(String name) {
        new PodmanPage.VirtualMachineTable(POWER).checkPowerStatus(PodmanPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            runActionWithoutParameters(name, "Удалить БД");
            Assertions.assertFalse(new Table("").isColumnValueEquals("", name), "БД существует");
        }
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        generalInfoTab.switchTo();
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
