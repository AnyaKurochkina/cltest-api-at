package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.cloud.tests.ActionParameters;
import ui.elements.Menu;
import ui.elements.Table;
import ui.t1.pages.cloudEngine.vpc.PublicIp;

public class NetworkInterface extends IProductT1Page<NetworkInterface> {

    public PublicIp selectIp(String ip) {
        getIpRow(ip).get().shouldBe(Condition.visible).click();
        return new PublicIp();
    }

    public void detachComputeIp(String ip) {
        runActionWithoutParameters(getIpRow(ip).get().$("button"), "Отключить от сетевого интерфейса");
    }

    public Table.Row getIpRow(String ip){
        return getTableByHeader("Публичные IP").getRowByColumnValue("Имя", ip);
    }

}
