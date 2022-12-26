package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import ui.t1.pages.cloudEngine.vpc.PublicIp;

public class NetworkInterface extends IProductT1Page {

    public PublicIp selectIp(String ip) {
        getTableByHeader("Публичные IP").getRowElementByColumnValue("IP", ip).shouldBe(Condition.visible).click();
        return new PublicIp();
    }
}
