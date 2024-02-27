package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.vpc.PublicIp;

public class NetworkInterface extends IProductT1Page<NetworkInterface> {

    public PublicIp selectIp(String ip) {
        getIpRow(ip).getElementByColumnIndex(0).shouldBe(Condition.visible).click();
        return new PublicIp();
    }

    public void detachComputeIp(String ip) {
        runActionWithoutParameters(getIpRow(ip).get().$("button"), "Отключить от сетевого интерфейса");
    }

    public Table.Row getIpRow(String ip){
        return getTableByHeader("Публичные IP").getRowByColumnValue(Column.NAME, ip);
    }

    @Step("Получение публичного IP у сервера")
    public String getPublicIp() {
        return getTableByHeader("Публичные IP").getFirstValueByColumn(Column.NAME);
    }
}
