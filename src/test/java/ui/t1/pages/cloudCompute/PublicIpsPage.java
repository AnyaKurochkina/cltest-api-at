package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.DataTable;
import ui.elements.DropDown;

import static ui.t1.pages.cloudCompute.PublicIpsPage.IpTable.COLUMN_IP;

public class PublicIpsPage {

    //new PublicIpsPage().addIp("ru-central1-c");
    public String addIp(String availabilityZone) {
        IpTable ipTable = new IpTable();
        String oldIp = ipTable.getFirstValueByColumn(COLUMN_IP);
        ipTable.clickAdd();
        DropDown.byLabel("Зона доступности").select(availabilityZone);
        EntitiesUtils.clickOrder();
        //Todo: пока нет сокетов
        Waiting.sleep(40000);
        String newIp = ipTable.update().getFirstValueByColumn(COLUMN_IP);
        Assertions.assertNotEquals(oldIp, newIp);
        return newIp;
    }

    public PublicIpPage selectIp(String name){
        new IpTable().getRowElementByColumnValue(COLUMN_IP, name).shouldBe(Condition.visible).click();
        return new PublicIpPage();
    }

    public static class IpTable extends DataTable {
        public static final String COLUMN_IP = "IP-адрес";

        public IpTable() {
            super(COLUMN_IP);
        }
    }
}
