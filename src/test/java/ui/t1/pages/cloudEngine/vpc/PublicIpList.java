package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.DataTable;
import ui.elements.Select;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;
import java.util.List;

public class PublicIpList {

    @Step("Добавить IP в зоне {region}")
    public String addIp(String region) {
        new IpTable().clickAdd();
        Select.byLabel("Регион").set(region);
        OrderUtils.clickOrder();
        boolean isEmpty = new IpTable().isEmpty();
        OrderUtils.waitCreate(() -> {
            if (!isEmpty) {
                String oldIp = new IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
                Waiting.find(() -> !new IpTable().getFirstValueByColumn(Column.IP_ADDRESS).contains(oldIp), Duration.ofMinutes(1));
            } else {
                Waiting.find(() -> !new IpTable().isEmpty(), Duration.ofMinutes(1));
                Waiting.find(() -> !new IpTable().getFirstValueByColumn(Column.IP_ADDRESS).equals("—"), Duration.ofMinutes(1));
            }
        });
        return new IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
    }

    public PublicIp selectIp(String name) {
        new IpTable().getRowByColumnValue(Column.IP_ADDRESS, name).get().shouldBe(Condition.visible).click();
        return new PublicIp();
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
