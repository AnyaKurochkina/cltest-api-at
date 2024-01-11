package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.elements.DataTable;
import ui.t1.pages.cloudEngine.Column;

public class PlacementList {

    @Step("Открытие формы создания политики")
    public PlacementCreate addPlacement() {
        new PlacementTable().clickAdd();
        return new PlacementCreate();
    }

    @Step("Открыть политику {name}")
    public Placement selectPlacement(String name) {
        new PlacementTable().getRowByColumnValueContains(Column.NAME, name).getElementByColumnIndex(0).shouldBe(Condition.visible).click();
        return new Placement();
    }

    /* Таблица политик размещения */
    public static class PlacementTable extends DataTable {
        public PlacementTable() {
            super(Column.NAME);
        }
    }
}
