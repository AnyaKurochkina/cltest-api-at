package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.cloud.pages.orders.OrderUtils;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.DataTable;
import ui.elements.Select;

import java.time.Duration;
import java.util.List;

public class PublicIpList {

    @Step("Добавить IP в зоне {availabilityZone}")
    public String addIp(String availabilityZone) {
        new IpTable().clickAdd();
        Select.byLabel("Зона доступности").set(availabilityZone);
        OrderUtils.clickOrder();
        boolean isEmpty = new IpTable().isEmpty();
        OrderUtils.waitCreate(() -> {
            IpTable table = new IpTable();
            if(!isEmpty) {
                String oldIp = table.getFirstValueByColumn(Column.IP_ADDRESS);
                table.getRow(0).getElementByColumn(Column.IP_ADDRESS).shouldNotBe(Condition.exactText(oldIp), Duration.ofMinutes(1));
            }
            else
                table.getRows().shouldBe(CollectionCondition.sizeNotEqual(0), Duration.ofMinutes(1));
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
