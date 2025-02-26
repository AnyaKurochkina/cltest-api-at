package ui.cloud.pages.orders;

import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.Moon;
import ui.elements.Dialog;

import static core.helper.StringUtils.$x;

public class MoonPage extends IProductPage {

    private static final String BLOCK_APP = "Приложение";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_PATH = "Путь";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    public MoonPage(Moon product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new MoonPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить приложение", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Имя хоста");
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus("Питание");
        }
    }
}
