package ui.t1.pages.cloudEngine.backup;

import ui.elements.Button;
import ui.elements.DataTable;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.IProductListT1Page;

public class BackupsList extends IProductListT1Page {

    public BackupCreate addBackup() {
        new BackupTable().clickAdd();
        return new BackupCreate();
    }

    public Backup selectBackup(String name) {
        Button.byElement(new BackupTable().getRowByColumnValue(Column.OBJECT_NAME, name).getElementByColumn(Column.OBJECT_NAME)).click();
        return new Backup();
    }

    public static class BackupTable extends DataTable {
        public BackupTable() {
            super("Имя объекта");
        }
    }
}
