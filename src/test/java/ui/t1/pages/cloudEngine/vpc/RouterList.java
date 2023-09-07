package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.IProductListT1Page;

import java.util.List;

import static ui.t1.pages.cloudEngine.vpc.VirtualIpList.IpTable.getMenuElement;

public class RouterList extends IProductListT1Page {
    public RouterCreate addRouter() {
        new RouterTable().clickAdd();
        return new RouterCreate();
    }

    public Router selectRouter(String name) {
        new RouterTable().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME).shouldBe(Condition.visible).click();
        return new Router();
    }

    public Menu getMenuRouter(String name) {
        return getMenuRouter(new RouterTable().getRowByColumnValue(Column.NAME, name).getElementLastColumn().$("button"));
    }

    public Menu getMenuRouter(SelenideElement element) {
        return new Menu(element);
    }

    public class Menu {
        private final SelenideElement btn;

        public Menu(SelenideElement btn) {
            this.btn = btn;
        }

        @Step("Подключить сеть {network}")
        public void attachNetwork(String network) {
            runActionWithParameters(btn, "Подключить сеть", "Подтвердить", () ->
                    Dialog.byTitle("Подключить сеть")
                            .setSelectValue("Добавленные сети", network));
        }
    }

    public static class RouterTable extends DataTable {

        public RouterTable() {
            super(Column.NAME);
        }

        public static SelenideElement getMenuElement(int index) {
            return new RouterTable().getRow(index).get().$("button");
        }
    }
}
