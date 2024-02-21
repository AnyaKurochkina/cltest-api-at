package ui.cloud.pages.orders;

import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.OpenMessagingAstra;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class OpenMessagingAstraPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String STATUS = "Статус";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    public OpenMessagingAstraPage(OpenMessagingAstra product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new OpenMessagingAstraPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void updateInstallation() {
        runActionWithParameters(BLOCK_APP, "Обновление установки", "Подтвердить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Обновление установки");
        });
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
    }
    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
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
