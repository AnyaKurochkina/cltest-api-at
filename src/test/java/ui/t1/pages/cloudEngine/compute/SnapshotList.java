package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

import java.util.List;

import static ui.t1.pages.cloudEngine.compute.SnapshotList.SnapshotsTable.COLUMN_NAME;


public class SnapshotList {

    public Snapshot selectSnapshot(String name){
        new SnapshotsTable().getRowByColumnValue(COLUMN_NAME, name).getElementByColumn(COLUMN_NAME).shouldBe(Condition.visible).click();
        return new Snapshot();
    }

    public List<String> geSnapshotList(){
        return new SnapshotsTable().getColumnValuesList(COLUMN_NAME);
    }

    public static class SnapshotsTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";

        public SnapshotsTable() {
            super(COLUMN_NAME);
        }
    }
}
