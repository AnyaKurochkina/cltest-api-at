package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.Table;

import static ui.t1.pages.cloudCompute.VmPage.DiskInfo.COLUMN_NAME;

public class VmPage extends IProductT1Page {

    public DiskPage selectDisk(String disk){
        new DiskInfo().getRowElementByColumnValue(COLUMN_NAME, disk).shouldBe(Condition.visible).click();
        return new DiskPage();
    }

    public static class DiskInfo extends Table {
        public static final String COLUMN_SYSTEM = "Системный";
        public static final String COLUMN_NAME = "Имя";
        public DiskInfo() {
            super(COLUMN_SYSTEM);
        }
    }
}
