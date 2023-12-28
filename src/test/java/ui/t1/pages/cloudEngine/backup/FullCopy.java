package ui.t1.pages.cloudEngine.backup;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.Table;
import ui.t1.pages.cloudEngine.Column;

public class FullCopy extends IncrementalCopy {

    @Step("Переход к инкрементальной копии {name}")
    public IncrementalCopy selectIncrementalCopy(String name) {
        Button.byElement(new IncrementalCopyList().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME)).click();
        return new IncrementalCopy();
    }

    public static class IncrementalCopyList extends Table {

        public IncrementalCopyList() {
            super(Column.CREATED_DATE, 2);
        }

        public IncrementalCopyList(SelenideElement table) {
            super(table);
        }
    }
}
