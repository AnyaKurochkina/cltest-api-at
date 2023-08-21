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

public class VirtualIpList extends IProductListT1Page {
    public VirtualIpCreate addIp() {
        new IpTable().clickAdd();
        return new VirtualIpCreate();
    }

    public VirtualIp selectIp(String ip) {
        new IpTable().getRowByColumnValue(Column.IP_ADDRESS, ip).getElementByColumn(Column.IP_ADDRESS).shouldBe(Condition.visible).click();
        return new VirtualIp();
    }

    public Menu getMenuVirtualIp(String ip) {
        return new Menu(getMenuElement(new IpTable().getRowByColumnValue(Column.IP_ADDRESS, ip).getIndex()));
    }

    public Menu getMenuVirtualIp(SelenideElement btn) {
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

    public List<String> getIpList() {
        return new IpTable().getColumnValuesList(Column.IP_ADDRESS);
    }

    public static class IpTable extends DataTable {

        public IpTable() {
            super(Column.IP_ADDRESS);
        }

        public static SelenideElement getMenuElement(int index) {
            return new IpTable().getRow(index).get().$("button");
        }
    }
}
