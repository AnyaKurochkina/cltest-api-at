package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.Table;

import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_NAME;

public class Vm extends IProductT1Page {

    public Disk selectDisk(String disk){
        new DiskInfo().getRowByColumnValue(COLUMN_NAME, disk).get().shouldBe(Condition.visible).click();
        return new Disk();
    }

    public NetworkInterface selectNetworkInterface(){
        new NetworkInfo().getRow(0).get().shouldBe(Condition.visible).click();
        return new NetworkInterface();
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
