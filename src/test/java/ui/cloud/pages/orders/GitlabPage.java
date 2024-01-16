package ui.cloud.pages.orders;

import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.GitLab;
import ui.elements.Dialog;

import static core.helper.StringUtils.$x;

public class GitlabPage extends IProductPage {

    private static final String BLOCK_APP = "Общая информация";
    private static final String HEADER_CONNECT_STATUS = "Статус подключения";
    private static final String HEADER_PATH = "Путь";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");

    public GitlabPage(GitLab product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new GitlabPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить группу Gitlab", "Удалить", () ->
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
