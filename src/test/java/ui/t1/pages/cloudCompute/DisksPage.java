package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

public class DisksPage {

    public DiskCreatePage addDisk(){
        new DisksPageTable().clickAdd();
        return new DiskCreatePage();
    }

    public DiskPage selectDisk(String name){
        new DisksPageTable().getRowElementByColumnValue(DisksPageTable.COLUMN_NAME, name).shouldBe(Condition.visible).click();
        return new DiskPage();
    }

    public static class DisksPageTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";

        public DisksPageTable() {
            super(COLUMN_NAME);
        }
    }
}
