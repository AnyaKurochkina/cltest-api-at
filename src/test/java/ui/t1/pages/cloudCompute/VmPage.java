package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.Table;

import static ui.t1.pages.cloudCompute.VmPage.DiskInfo.COLUMN_NAME;

public class VmPage extends IProductT1Page {

    public DiskPage selectDisk(String disk){
        new DiskInfo().getRowElementByColumnValue(COLUMN_NAME, disk).shouldBe(Condition.visible).click();
        return new DiskPage();
    }

    public NetworkInterfacePage selectNetworkInterface(){
        new DiskInfo().getRowByIndex(0).shouldBe(Condition.visible).click();
        return new NetworkInterfacePage();
    }

    public static class DiskInfo extends Table {
        public static final String COLUMN_SYSTEM = "Системный";
        public static final String COLUMN_NAME = "Имя";
        public DiskInfo() {
            super(COLUMN_SYSTEM);
        }
    }

    public static class NetworkInfo extends Table {
        public static final String COLUMN_IP = "IP";
        public NetworkInfo() {
            super(COLUMN_IP);
        }
    }
}
