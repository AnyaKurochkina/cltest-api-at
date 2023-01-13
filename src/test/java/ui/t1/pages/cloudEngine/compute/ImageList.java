package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.DataTable;

import java.util.List;


public class ImageList {

    public Image selectImage(String name) {
        new ImageTable().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME).shouldBe(Condition.visible).click();
        return new Image();
    }

    public List<String> getImageList() {
        return new ImageTable().getColumnValuesList(Column.NAME);
    }

    public static class ImageTable extends DataTable {
        public ImageTable() {
            super(Column.NAME);
        }
    }
}
