package ui.t1.pages.cloudEngine.vpc;

import io.qameta.allure.Step;
import ui.elements.Dialog;
import ui.t1.pages.IProductT1Page;

public class VirtualIp extends IProductT1Page<VirtualIp> {

    @Step("Подключить к сетевому интерфейсу {networkInterface}")
    public void attachComputeIp(String networkInterface) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к виртуальной машине", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к виртуальной машине")
                                .setSelectValue("Сетевой интерфейс", networkInterface));
    }

    @Step("Отключить от сетевого интерфейса {ip}")
    public void detachComputeIp(String ip) {
        runActionWithParameters(BLOCK_PARAMETERS, "Отключить от сетевого интерфейса", "Подтвердить", () ->
                Dialog.byTitle("Отключить от сетевого интерфейса")
                        .setSelectValue("Сетевой интерфейс", ip));
    }
}
