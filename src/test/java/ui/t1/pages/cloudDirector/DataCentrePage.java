package ui.t1.pages.cloudDirector;

import ui.elements.Dialog;
import ui.t1.pages.IProductT1Page;

public class DataCentrePage extends IProductT1Page<DataCentrePage> {

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(INFO_DATA_CENTRE, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(VirtualMachine.POWER_STATUS_DELETED);
    }
}
