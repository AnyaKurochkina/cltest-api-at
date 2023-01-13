package ui.t1.pages.cloudEngine.vpc;

import io.qameta.allure.Step;
import ui.elements.Dialog;
import ui.t1.pages.cloudEngine.compute.Disk;
import ui.t1.pages.cloudEngine.compute.IProductT1Page;

public class PublicIp extends IProductT1Page<PublicIp> {

    @Step("Подключить IP к ВМ {vmName}")
    public void attachComputeIp(String vmName) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к виртуальной машине", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к виртуальной машине")
                                .setSelectValue("Доступные виртуальные машины", vmName));
    }

    @Step("Отключить IP от интерфейса")
    public void detachComputeIp() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить от сетевого интерфейса");
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
