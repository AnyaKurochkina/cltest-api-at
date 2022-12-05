package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

import static ui.t1.pages.cloudCompute.DiskList.DisksPageTable.COLUMN_NAME;

public class DiskList {

    public DiskCreate addDisk(){
        new DisksPageTable().clickAdd();
        return new DiskCreate();
    }

    public Disk selectDisk(String name){
        new DisksPageTable().getRowElementByColumnValueContains(COLUMN_NAME, name).shouldBe(Condition.visible).click();
        return new Disk();
    }

//    public Menu getMenuRow(String disk){
//        return new Menu(new DisksPageTable().getRowByColumnValueContains(COLUMN_NAME, disk).getElementByColumn("").$x("button"));
//    }

    public static class DisksPageTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";

        public DisksPageTable() {
            super(COLUMN_NAME);
        }
    }
}
