package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;

public class NetworkInterface extends IProductT1Page {

    public PublicIp selectIp(String ip) {
        getTableByHeader("Публичные IP").getRowElementByColumnValue("IP", ip).shouldBe(Condition.visible).click();
        return new PublicIp();
    }
}
