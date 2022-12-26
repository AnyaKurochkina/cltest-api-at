package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

import java.util.List;

import static ui.t1.pages.cloudEngine.compute.SnapshotList.SnapshotsTable.COLUMN_NAME;


public class ImageList {

    public Image selectImage(String name){
        new ImageTable().getRowByColumnValue(COLUMN_NAME, name).getElementByColumn(COLUMN_NAME).shouldBe(Condition.visible).click();
        return new Image();
    }

    public List<String> geImageList(){
        return new ImageTable().getColumnValuesList(COLUMN_NAME);
    }

    public static class ImageTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";

        public ImageTable() {
            super(COLUMN_NAME);
        }
    }
}
