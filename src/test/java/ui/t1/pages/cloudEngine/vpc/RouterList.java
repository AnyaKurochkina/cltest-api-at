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

    public VirtualIp selectRouter(String name) {
        new RouterTable().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.IP_ADDRESS).shouldBe(Condition.visible).click();
        return new VirtualIp();
    }

    public Menu getMenuRouter(SelenideElement btn) {
        return new Menu(btn);
    }

    public class Menu {
        private final SelenideElement btn;

        public Menu(SelenideElement btn) {
            this.btn = btn;
        }

        @Step("Подключить к сетевому интерфейсу {ip}")
        public void attachComputeIp(String ip) {
            runActionWithParameters(btn, "Подключить к сетевому интерфейсу", "Подтвердить", () ->
                    Dialog.byTitle("Подключить к сетевому интерфейсу")
                            .setSelectValue("Сетевой интерфейс", ip));
        }

        @Step("Отключить от сетевого интерфейса {ip}")
        public void detachComputeIp(String ip) {
            runActionWithParameters(btn, "Отключить от сетевого интерфейса", "Подтвердить", () ->
                    Dialog.byTitle("Отключить от сетевого интерфейса")
                            .setSelectValue("Сетевой интерфейс", ip));
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
