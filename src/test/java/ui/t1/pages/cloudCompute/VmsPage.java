package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;

public class VmsPage {

    public VmCreatePage addVm(){
        new VmTable().clickAdd();
        return new VmCreatePage();
    }

    public VmPage selectCompute(String name){
        new VmTable().getRowElementByColumnValue(VmTable.COLUMN_NAME, name).shouldBe(Condition.visible).click();
        return new VmPage();
    }

    public static class VmTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";
        public static final String COLUMN_STATUS = "Статус";

        public VmTable() {
            super(COLUMN_NAME);
        }
    }


}
