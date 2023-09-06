package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.TarantoolDataGrid;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.*;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class TarantoolDataGridAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_SNAPSHOT = "Снапшоты";
    private static final String HEADER_CERTIFICATE = "Сертификаты";
    private static final String HEADER_COPY = "Резервные копии";
    private static final String HEADER_CONF_CLUSTER = "Конфигурация кластера";
    private static final String STATUS = "Статус";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    protected Button btnCluster = Button.byElement(Selenide.$x("//button[.='Кластер']"));


    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public TarantoolDataGridAstraPage(TarantoolDataGrid product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void changeConfiguration() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
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
        runActionWithParameters(getActionsMenuButton("",2), "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void сreateSnapshot() {
        runActionWithParameters(BLOCK_VM, "Создать снапшот", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Создать снапшот");
            Select.byLabel("Срок хранения в днях").set("1");
        });
        btnGeneralInfo.click();
        Assertions.assertTrue(getTableByHeader("Снапшоты").isColumnValueContains("Тип","snapshot"));
    }

    public void deleteSnapshot() {
        new Table("Имя",2).getRow(0).get().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new Table("Имя").getFirstValueByColumn("Имя"), "Удалить снапшот");
        btnGeneralInfo.click();
        Assertions.assertFalse(getTableByHeader("Снапшоты").isColumnValueContains("Тип", "snapshot"));
    }

    private SelenideElement getSnapshotMenuElement(String name) {
        return new Table(BLOCK_SNAPSHOT).getValueByColumnInFirstRow("Тип").$("button");
    }

    public void restart() {
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }
    public void updateVersionApp() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(getActionsMenuButton("",2), "Обновить версию приложения Tarantool Data Grid");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }
    public void updateCertificate() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(HEADER_CERTIFICATE, "Обновить сертификаты Tarantool Data Grid");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }
    public void createReserveCopy() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(HEADER_COPY, "Создать резервную копию");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Остановка сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set("zorg-core-01");
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void startTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Запуск сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set("zorg-core-01");
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void resetTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Перезапуск сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set("zorg-core-01");
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        });
        groups.forEach(group -> Assertions.assertTrue(new TarantoolDataGridAstraPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithParameters(new TarantoolDataGridAstraPage.RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        });
        groups.forEach(group -> Assertions.assertTrue(new TarantoolDataGridAstraPage.RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        runActionWithoutParameters(new TarantoolDataGridAstraPage.RoleTable().getRoleMenuElement(role), "Удалить группу доступа");
        Assertions.assertThrows(NotFoundException.class, () -> new TarantoolDataGridAstraPage.RoleTable().getRoleRow(role));
    }

    public void issueClientCertificate(String nameCertificate) {
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
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
