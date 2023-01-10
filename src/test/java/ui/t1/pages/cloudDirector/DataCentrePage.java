package ui.t1.pages.cloudDirector;

import ui.elements.Dialog;
import ui.t1.pages.cloudCompute.Disk;
import ui.t1.pages.cloudCompute.IProductT1Page;

public class DataCentrePage extends IProductT1Page {

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(INFO_DATA_CENTRE, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(Disk.TopInfo.POWER_STATUS_DELETED);
    }
}
