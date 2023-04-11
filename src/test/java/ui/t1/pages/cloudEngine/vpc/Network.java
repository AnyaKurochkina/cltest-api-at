package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

import static core.helper.StringUtils.$x;

public class Network {
    Button btnAddSubnet = Button.byElement($x("//button[.='Добавить']"));

    public CreateSubnet addSubnet() {
        btnAddSubnet.click();
        return new CreateSubnet();
    }

    @Step("Удалить подсеть {subnet}")
    public void deleteSubnet(String subnet) {
        SubnetListInfo.getMenuSubnet(subnet).select("Удалить");
        Waiting.findWithRefresh(() -> !new SubnetListInfo().isColumnValueEquals(Column.NOMINATION, subnet), Duration.ofMinutes(1));
    }

    @Getter
    public static class CreateSubnet {
        String name;
        String desc;
        String availabilityZone;
        String cidr;
        int prefix;
        boolean dhcp;

        public CreateSubnet setName(String name) {
            this.name = name;
            Input.byLabel("Имя").setValue(name);
            return this;
        }

        public CreateSubnet setDesc(String desc) {
            this.desc = desc;
            TextArea.byLabel("Описание").setValue(desc);
            return this;
        }

        public CreateSubnet setAvailabilityZone(String availabilityZone) {
            this.availabilityZone = availabilityZone;
            Select.byLabel("Зона").set(availabilityZone);
            return this;
        }

        public CreateSubnet setCidr(String cidr) {
            this.cidr = cidr;
            Input.byLabel("IPv4 CIDR").setValue(cidr);
            return this;
        }

        public CreateSubnet setPrefix(int prefix) {
            this.prefix = prefix;
            Select.byLabel("Prefix").set(String.valueOf(prefix));
            return this;
        }

        public CreateSubnet setDhcp(boolean dhcp) {
            this.dhcp = dhcp;
            CheckBox.byLabel("DHCP").setChecked(dhcp);
            return this;
        }

        public CreateSubnet clickAdd() {
            Dialog dialog = Dialog.byTitle("Добавить подсеть");
            dialog.clickButton("Добавить");
            dialog.getDialog().shouldNotBe(Condition.visible);
            OrderUtils.waitCreate(() ->
                    Waiting.findWithRefresh(() -> SubnetListInfo.getSubnet(name).getValueByColumn(Column.STATUS).equals("Доступно"), Duration.ofMinutes(1)));
            return this;
        }
    }

    public static class SubnetListInfo extends Table {

        public SubnetListInfo() {
            super(Column.NOMINATION);
        }

        public static Row getSubnet(String subnet) {
            return new SubnetListInfo().getRowByColumnValue(Column.NOMINATION, subnet);
        }

        public static Menu getMenuSubnet(String subnet) {
            return Menu.byElement(getSubnet(subnet).get().$("button"));
        }
    }
}
