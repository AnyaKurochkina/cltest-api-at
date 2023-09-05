package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.elements.DataTable;
import ui.t1.pages.cloudEngine.Column;

import java.util.List;

public class VmList extends IProductListT1Page{

    public VmCreate addVm(){
        new VmTable().clickAdd();
        return new VmCreate();
    }

    public Vm selectCompute(String name){
        new VmTable().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME).shouldBe(Condition.visible).click();
        return new Vm();
    }

    public List<String> getVmList(){
        return new VmTable().getColumnValuesList(Column.NAME);
    }

    public static class VmTable extends DataTable {
        public VmTable() {
            super(Column.NAME);
        }
    }


}
