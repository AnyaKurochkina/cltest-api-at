package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.DataTable;
import ui.elements.Select;

import java.time.Duration;
import java.util.List;

import static ui.t1.pages.cloudEngine.vpc.PublicIpList.IpTable.COLUMN_IP;

public class PublicIpList {

    public String addIp(String availabilityZone) {
        new IpTable().clickAdd();
        Select.byLabel("Зона доступности").set(availabilityZone);
        EntitiesUtils.clickOrder();
        EntitiesUtils.waitCreate(() -> {
            IpTable table = new IpTable();
            if(table.rowSize() > 0) {
                String oldIp = table.getFirstValueByColumn(COLUMN_IP);
                table.getRow(0).getElementByColumn(COLUMN_IP).shouldNotBe(Condition.exactText(oldIp), Duration.ofMinutes(1));
            }
            else
                table.getRows().shouldBe(CollectionCondition.sizeNotEqual(0), Duration.ofMinutes(1));
        });
        return new IpTable().getFirstValueByColumn(COLUMN_IP);
    }

    public PublicIp selectIp(String name) {
        new IpTable().getRowElementByColumnValue(COLUMN_IP, name).shouldBe(Condition.visible).click();
        return new PublicIp();
    }

    public List<String> getIpList() {
        return new IpTable().getColumnValuesList(COLUMN_IP);
    }

    public static class IpTable extends DataTable {
        public static final String COLUMN_IP = "IP-адрес";

        public IpTable() {
            super(COLUMN_IP);
        }
    }
}
