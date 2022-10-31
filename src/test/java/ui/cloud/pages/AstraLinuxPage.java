package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.orderService.products.Astra;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.DropDown;
import ui.elements.Table;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class AstraLinuxPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_ROLE = "Роли";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_USERS_ROLE = "user";
    private static final String POWER = "Питание";
    private static final String HEADER_GROUP = "Группы";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement cpu = $x("(//h5)[1]");
    SelenideElement ram = $x("(//h5)[2]");

    public AstraLinuxPage(Astra product) {
        super(product);
    }

    @Override
    void checkPowerStatus(String expectedStatus) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
    }

    public void changeConfiguration() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
                DropDown.byLabel("Конфигурация Core/RAM").select(Product.getFlavor(maxFlavor)));
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }
    public void delete() {
        runActionWithParameters(BLOCK_VM, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Перезагрузить по питанию");
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
    }

  public void stopHard() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_OFF);
    }


    public void addGroup(String nameGroup) {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
            runActionWithParameters(BLOCK_ROLE, "Добавить группу доступа", "Подтвердить", () -> {
                Dialog dlg = new Dialog("Добавить группу доступа");
                dlg.setDropDownValue("Группы", nameGroup);
            });
    }

    public void changeGroup(String nameGroup) {
        runActionWithParameters(HEADER_USERS_ROLE, "Изменить состав группы", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Изменить состав группы");
            dlg.setDropDownValue("Группы", nameGroup);
        });
        btnGeneralInfo.scrollIntoView(scrollCenter).click();
        Assertions.assertTrue(new Table(HEADER_GROUP).isColumnValueContains(HEADER_GROUP, nameGroup), "Ошибка изменения состава группы");
    }
    public void deleteGroup() {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(HEADER_USERS_ROLE, "Удалить группу доступа");
        btnGeneralInfo.scrollIntoView(scrollCenter).click();
        Assertions.assertFalse(getBtnAction(HEADER_USERS_ROLE).exists(), "Ошиюка удаления группы");
    }

    public void issueClientCertificate(String nameCertificate) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithParameters(BLOCK_VM, "Выпустить клиентский сертификат", "Подтвердить", () -> {
            Dialog dlg = new Dialog("Выпустить клиентский сертификат");
            dlg.setInputValue("Клиентская часть имени сертификата", nameCertificate);
            generatePassButton.shouldBe(Condition.enabled).click();
        });
    }

    public void removeDb(String name) {
        new AstraLinuxPage.VirtualMachineTable(POWER).checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
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
        btnGeneralInfo.shouldBe(Condition.enabled).click();
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
}
