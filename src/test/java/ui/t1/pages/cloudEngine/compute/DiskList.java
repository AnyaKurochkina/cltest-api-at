package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.DataTable;

import java.util.List;

public class DiskList {

    public DiskCreate addDisk(){
        new DiskTable().clickAdd();
        return new DiskCreate();
    }

    public Disk selectDisk(String name){
        new DiskTable().getRowByColumnValueContains(Column.NAME, name).get().shouldBe(Condition.visible).click();
        return new Disk();
    }

    public List<String> getDiskList(){
        return new DiskTable().getColumnValuesList(Column.NAME);
    }

    public static class DiskTable extends DataTable {
        public DiskTable() {
            super(Column.NAME);
        }
    }
}
