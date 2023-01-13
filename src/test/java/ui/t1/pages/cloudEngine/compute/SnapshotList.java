package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.DataTable;

import java.util.List;


public class SnapshotList {

    public Snapshot selectSnapshot(String name) {
        new SnapshotsTable().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME).shouldBe(Condition.visible).click();
        return new Snapshot();
    }

    public List<String> geSnapshotList() {
        return new SnapshotsTable().getColumnValuesList(Column.NAME);
    }

    public static class SnapshotsTable extends DataTable {

        public SnapshotsTable() {
            super(Column.NAME);
        }
    }
}
