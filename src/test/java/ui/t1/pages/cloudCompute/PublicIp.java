package ui.t1.pages.cloudCompute;

import ui.cloud.tests.ActionParameters;
import ui.elements.Dialog;

public class PublicIp extends IProductT1Page {

    public void attachComputeIp(String vmName) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к виртуальной машине", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к виртуальной машине")
                                .setSelectValue("Доступные виртуальные машины", vmName),
                ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).build());
    }

    public void detachComputeIp() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить от сетевого интерфейса",
                ActionParameters.builder().waitChangeStatus(false).checkLastAction(false).build());
    }

    @Override
    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(BLOCK_PARAMETERS, "Освободить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(Disk.TopInfo.POWER_STATUS_DELETED);
    }
}
