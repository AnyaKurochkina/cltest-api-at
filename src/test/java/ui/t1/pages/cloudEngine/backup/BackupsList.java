package ui.t1.pages.cloudEngine.backup;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.IProductListT1Page;

public class BackupsList extends IProductListT1Page {

    @Step("Открытие формы создания backup")
    public BackupCreate addBackup() {
        new BackupTable().clickAdd();
        return new BackupCreate();
    }

    @Step("Открытие backup {name}")
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
