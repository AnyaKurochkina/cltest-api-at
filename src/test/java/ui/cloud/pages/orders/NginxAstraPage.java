package ui.cloud.pages.orders;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.Nginx;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.elements.CheckBox;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class NginxAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String STATUS = "Статус";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    public NginxAstraPage(Nginx product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new NginxAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void checkConfiguration() {
        checkPowerStatus(NginxAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию");
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        btnGeneralInfo.click(); // для задержки иначе не отрабатывает 305 строка
        checkPowerStatus(NginxAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
    }

    @Step("Обновить сертификат NginxAstra")
    public void updateCertificate() {
        new NginxAstraPage.VirtualMachineTable().checkPowerStatus(NginxAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Обновить сертификаты");
        new NginxAstraPage.VirtualMachineTable().checkPowerStatus(NginxAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
    }

    public void changeConfiguration() {
        checkPowerStatus(AstraLinuxPage.VirtualMachineTable.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        Flavor maxFlavor = product.getMaxFlavorLinuxVm();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () ->
        {
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
        });
        btnGeneralInfo.click();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        btnGeneralInfo.click(); // для задержки иначе не отрабатывает 305 строка
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("Устройство", "/dev/mapper/vg_02-lv_app").getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("Устройство", "/dev/mapper/vg_02-lv_app").getValueByColumn(HEADER_DISK_SIZE),
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


}
