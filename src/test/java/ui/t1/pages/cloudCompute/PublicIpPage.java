package ui.t1.pages.cloudCompute;

import ui.elements.Dialog;

public class PublicIpPage extends IProductT1Page {

    @Override
    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(BLOCK_PARAMETERS, "Освободить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(DiskPage.TopInfo.POWER_STATUS_DELETED);
    }
}
