package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.util.List;

public class VirtualIpList {

    @Step("Добавить IP в регионе {region}")
    public String addIp(String region, String name, String network, String mode, boolean l2) {
        new IpTable().clickAdd();
        Input.byLabel("Имя VIP").setValue(name);
        Select.byLabel("Регион").set(region);
        Select.byLabel("Подсеть").setContains(network);
        RadioGroup.byLabel("Режим").select(mode);
        Switch.byText("Задействовать L2").setEnabled(l2);
        OrderUtils.clickOrder();

        Waiting.sleep(20000);

        return new IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
    }

    public VirtualIp selectIp(String name) {
        new IpTable().getRowByColumnValue(Column.IP_ADDRESS, name).get().shouldBe(Condition.visible).click();
        return new VirtualIp();
    }

    public List<String> getIpList() {
        return new IpTable().getColumnValuesList(Column.IP_ADDRESS);
    }

    public static class IpTable extends DataTable {

        public IpTable() {
            super(Column.IP_ADDRESS);
        }
    }
}
