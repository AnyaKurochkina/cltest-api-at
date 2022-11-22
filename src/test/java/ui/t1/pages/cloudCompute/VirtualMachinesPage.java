package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import ui.elements.DataTable;
import ui.elements.Table;

public class VirtualMachinesPage {

    public VmPage addVm(){
        new VirtualMachineTable().clickAdd();
        VmPage vm = new VmPage();
        Table.Row vmRow = new VirtualMachineTable().getRowByColumnValue(VirtualMachineTable.COLUMN_NAME, vm.getName());
        Assertions.assertEquals("Включено", vmRow.getValueByColumn(VirtualMachineTable.COLUMN_STATUS));
        return vm;
    }

    public ComputeInstance selectCompute(String name){
        new VirtualMachineTable().getRowElementByColumnValue(VirtualMachineTable.COLUMN_NAME, name).shouldBe(Condition.visible).click();
        return new ComputeInstance();
    }

    private static class VirtualMachineTable extends DataTable {
        public static final String COLUMN_NAME = "Имя";
        public static final String COLUMN_STATUS = "Статус";

        public VirtualMachineTable() {
            super(COLUMN_NAME);
        }
    }


}
