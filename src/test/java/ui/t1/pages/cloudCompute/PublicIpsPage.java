package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.DropDown;

public class PublicIpsPage {

    //new PublicIpsPage().addIp("ru-central1-c");
    public void addIp(String availabilityZone) {
        IpTable ipTable = new IpTable();
        int countRow = ipTable.rowSize();
        ipTable.clickAdd();
        DropDown.byLabel("Зона доступности").select(availabilityZone);
        EntitiesUtils.clickOrder();
        //Todo: пока нет сокетов
        Waiting.sleep(40000);
        Assertions.assertEquals(countRow + 1, new IpTable().rowSize());
    }

    public PublicIpPage selectIp(String name){
        new IpTable().getRowElementByColumnValue(IpTable.COLUMN_IP, name).shouldBe(Condition.visible).click();
        return new PublicIpPage();
    }

    public static class IpTable extends DataTable {
        public static final String COLUMN_IP = "IP-адрес";

        public IpTable() {
            super(COLUMN_IP);
        }
    }
}
