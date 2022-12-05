package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.Table;

public class NetworkInterface extends IProductT1Page {

    public PublicIp selectIp(String ip) {
        new IpTable().getRowElementByColumnValue(IpTable.COLUMN_NAME, ip).shouldBe(Condition.visible).click();
        return new PublicIp();
    }

    private static class IpTable extends Table {
        public static final String COLUMN_NAME = "Имя";

        public IpTable() {
            super(COLUMN_NAME, 2);
        }
    }
}
