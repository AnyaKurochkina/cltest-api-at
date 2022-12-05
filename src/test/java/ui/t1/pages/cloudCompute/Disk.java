package ui.t1.pages.cloudCompute;

import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.CheckBox;
import ui.elements.Dialog;

import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_NAME;

public class Disk extends IProductT1Page {

    public void attachComputeVolume(String vmName, boolean deleteOnTermination) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к виртуальной машине", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к виртуальной машине")
                                .setDropDownValue("Доступные виртуальные машины", vmName + ":")
                                .setCheckBox(CheckBox.byLabel("Удалять вместе с виртуальной машиной"), deleteOnTermination),
                ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).build());
    }

    public void detachComputeVolume() {
        String name = new TopInfo().getFirstValueByColumn(COLUMN_NAME);
        runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить диск от виртуальной машины");
        Assertions.assertFalse(new Vm.DiskInfo().isColumnValueEquals(COLUMN_NAME, name));
    }

}
