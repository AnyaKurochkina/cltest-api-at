package ui.t1.pages.cdn;

import ui.elements.DataTable;
import ui.elements.Menu;

public abstract class AbstractCdnTab {

    protected void deleteCdnEntity(String name) {
        DataTable table = new DataTable("Источники");
        Menu.byElement(table.searchAllPages(t -> table.isColumnValueContains("Название", name))
                        .getRowByColumnValueContains("Название", name)
                        .get()
                        .$x(".//button[@id = 'actions-menu-button']"))
                .select("Удалить");
    }
}
