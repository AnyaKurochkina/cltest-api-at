package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.DataTable;
import ui.elements.DropDown;

import java.util.List;

import static ui.t1.pages.cloudEngine.vpc.PublicIpList.IpTable.COLUMN_IP;

public class PublicIpList {

    //new PublicIpsPage().addIp("ru-central1-c");
    public String addIp(String availabilityZone) {
        IpTable ipTable = new IpTable();
        String oldIp = "";
        if(ipTable.rowSize() > 0)
            oldIp = ipTable.getFirstValueByColumn(COLUMN_IP);
        ipTable.clickAdd();
        DropDown.byLabel("Зона доступности").select(availabilityZone);
        EntitiesUtils.clickOrder();
        //Todo: пока нет сокетов
        Waiting.sleep(40000);
        String newIp = ipTable.update().getFirstValueByColumn(COLUMN_IP);
        Assertions.assertNotEquals(oldIp, newIp);
        return newIp;
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
