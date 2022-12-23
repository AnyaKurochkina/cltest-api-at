package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.SelenideElement;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.elements.Select;

import java.util.Arrays;

import static ui.t1.pages.cloudCompute.NetworkInterfaceList.NetworkInterfaceTable.COLUMN_VM;
import static ui.t1.pages.cloudCompute.NetworkInterfaceList.NetworkInterfaceTable.getMenuElement;

public class NetworkInterfaceList extends IProductListT1Page {

    public Menu getMenuNetworkInterface(String vm) {
        return new Menu(new NetworkInterfaceTable().getRowByColumnValue(COLUMN_VM, vm).getIndex());
    }

    public class Menu {
        private final int index;

        public Menu(int index) {
            this.index = index;
        }

        public void attachIp(String ip) {
            runActionWithParameters(getMenuElement(index), "Подключить публичный IP", "Подтвердить", () ->
                    Dialog.byTitle("Подключить публичный IP").setSelectValue("Публичный IP", ip));
        }

        public void detachIp() {
            runActionWithoutParameters(getMenuElement(index), "Отключить от сетевого интерфейса");
        }

        public void updateSecurityGroups(String... groups) {
            runActionWithParameters(getMenuElement(index), "Изменить группы безопасности", "Подтвердить", () -> {
                Dialog.byTitle("Изменить группы безопасности");
                Select select = Select.byLabel("Группы безопасности сетевого интерфейса").clear();
                Arrays.stream(groups).forEach(select::set);
            });
        }
    }

    public static class NetworkInterfaceTable extends DataTable {
        public static final String COLUMN_VM = "Виртуальная машина";

        public NetworkInterfaceTable() {
            super(COLUMN_VM);
        }

        public static SelenideElement getMenuElement(int index) {
            return new NetworkInterfaceTable().getRow(index).get().$("button");
        }
    }
}
