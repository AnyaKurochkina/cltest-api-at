package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

import java.util.List;

import static ui.t1.pages.cloudCompute.DiskList.DiskTable.COLUMN_NAME;

public class DiskList {

    public DiskCreate addDisk(){
        new DiskTable().clickAdd();
        return new DiskCreate();
    }

    public Disk selectDisk(String name){
        new DiskTable().getRowByColumnValueContains(COLUMN_NAME, name).get().shouldBe(Condition.visible).click();
        return new Disk();
    }

    public List<String> getDiskList(){
        return new DiskTable().getColumnValuesList(COLUMN_NAME);
    }

    public static class DiskTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";
        public static final String COLUMN_DATE = "Дата создания";

        public DiskTable() {
            super(COLUMN_NAME);
        }
    }
}
