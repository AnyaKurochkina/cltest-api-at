package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.Etcd;
import models.cloud.subModels.Flavor;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class EtcdPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");


    public EtcdPage(Etcd product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new EtcdPage.VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new EtcdPage.VirtualMachineTable("Статус").checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new EtcdPage.VirtualMachineTable("Роли узла").checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new EtcdPage.VirtualMachineTable("Роли узла").checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(EtcdPage.VirtualMachineTable.POWER_STATUS_OFF);
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
        String sizeComonAfterChange = String.valueOf(Integer.parseInt(value) + Integer.parseInt(getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", "/app/etcd/logs").getValueByColumn(HEADER_DISK_SIZE)));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                sizeComonAfterChange));
    }

    public void resetPassword(String name) {
        runActionWithParameters(getActionsMenuButton(name), "Сброс пароля", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сброс пароля");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void changeConfiguration() {
        btnGeneralInfo.click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Flavor maxFlavor = product.getMaxFlavor();
        runActionWithParameters(BLOCK_VM, "Изменить конфигурацию", "Подтвердить", () -> {
            CheckBox.byLabel("Я соглашаюсь с перезагрузкой и прерыванием сервиса").setChecked(true);
            Select.byLabel("Конфигурация Core/RAM").set(NewOrderPage.getFlavor(maxFlavor));
        });
        btnGeneralInfo.click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertEquals(String.valueOf(maxFlavor.getCpus()), cpu.getText(), "Размер CPU не изменился");
        Assertions.assertEquals(String.valueOf(maxFlavor.getMemory()), ram.getText(), "Размер RAM не изменился");
    }

    public void createCertificate(String name) {
        runActionWithoutParameters(getActionsMenuButton(name), "Создать сертификаты для пользователя etcd");
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
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
