package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.util.Arrays;
import java.util.List;

import static ui.t1.pages.cloudEngine.compute.NetworkInterfaceList.NetworkInterfaceTable.COLUMN_VM;
import static ui.t1.pages.cloudEngine.compute.NetworkInterfaceList.NetworkInterfaceTable.getMenuElement;

public class NetworkInterfaceList extends IProductListT1Page {

    public Menu getMenuNetworkInterface(String vm) {
        return new Menu(getMenuElement(networkInterfaceTable().getRowByColumnValue(COLUMN_VM, vm).getIndex()));
    }

    public Menu getMenuNetworkInterface(SelenideElement btn) {
        return new Menu(btn);
    }

    public NetworkInterface selectNetworkInterfaceByVm(String vm) {
        new NetworkInterfaceTable().getRowByColumnValue(COLUMN_VM, vm).getElementByColumn("IP адрес").shouldBe(Condition.visible).click();
        return new NetworkInterface();
    }

    public NetworkInterfaceTable networkInterfaceTable() {
        return new NetworkInterfaceTable();
    }

    public NetworkInterfaceList setInterfaceCheckbox(String name){
        new CheckBox(networkInterfaceTable().getRowByColumnValue(COLUMN_VM, name).getElementByColumnIndex(0).$("input")).setChecked(true);
        return this;
    }

    public void addInSecurityGroup(String group){
        final String action = "Добавить в группу безопасности";
        Button.byText(action).click();
        Dialog.byTitle(action).setSelectValue("Название", group).clickButton("Добавить");
    }

    public class Menu {
        private final SelenideElement btn;

        public Menu(SelenideElement btn) {
            this.btn = btn;
        }

        @Step("Подключить IP {ip}")
        public void attachIp(String ip) {
            runActionWithParameters(btn, "Подключить публичный IP", "Подтвердить", () ->
                    Dialog.byTitle("Подключить публичный IP").setSelectValue("Публичный IP", ip));
        }

        @Step("Изменить группы безопасности на {groups}")
        public void updateSecurityGroups(String... groups) {
            runActionWithParameters(btn, "Изменить группы безопасности", "Подтвердить", () -> {
                Dialog.byTitle("Изменить группы безопасности");
                Select select = Select.byLabel("Группы безопасности сетевого интерфейса").clear();
                Arrays.stream(groups).forEach(select::set);
            });
        }

        @Step("Изменить группы безопасности на {groups}")
        public void updateSubnet(String subnet) {
            runActionWithParameters(btn, "Изменить подсеть", "Подтвердить", () -> {
                Dialog.byTitle("Изменить подсеть");
                Select.byLabel("Подсеть").setStart(subnet);
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

        public static List<String> getSecurityGroups(String vm) {
            return Arrays.asList(new NetworkInterfaceTable().getRowByColumnValue(COLUMN_VM, vm).getValueByColumn("Группы безопасности").split(", "));
        }
    }
}
