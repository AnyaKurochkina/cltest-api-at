package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.SelenideElement;
import ui.elements.Table;

public class ComputeHistory extends Table {
    public static final String COLUMN_OPERATION = "Операция";
    public static final String COLUMN_STATUS = "Статус";
    public ComputeHistory() {
        super(COLUMN_OPERATION);
    }

    public static SelenideElement getLastActionStatus(){
        return new ComputeHistory().getValueByColumnInFirstRow(COLUMN_STATUS);
    }
}
