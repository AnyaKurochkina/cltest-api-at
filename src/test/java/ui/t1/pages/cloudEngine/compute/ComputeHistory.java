package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.SelenideElement;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.Table;

public class ComputeHistory extends Table {
    public ComputeHistory() {
        super(Column.OPERATION);
    }

    public static SelenideElement getLastActionStatus(){
        return new ComputeHistory().getValueByColumnInFirstRow(Column.STATUS);
    }
}
