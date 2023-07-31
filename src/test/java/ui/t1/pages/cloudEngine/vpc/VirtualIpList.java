package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class VirtualIpList {

    @Step("Добавить IP в регионе {region}")
    public String addIp(String region, String name, String network, String mode, boolean l2, String networkInterface) {
        new IpTable().clickAdd();
        Input.byLabel("Имя VIP").setValue(name);
        Select.byLabel("Регион").set(region);
        Select.byLabel("Подсеть").setContains(network);
        RadioGroup.byLabel("Режим").select(mode);
        Switch.byText("Задействовать L2").setEnabled(l2);
        if(Objects.nonNull(networkInterface)) {
            Switch.byText("Задать IP адрес сетевого интерфейса").setEnabled(true);
            Input.byPlaceholder("0.0.0.0").setValue(networkInterface);
        }
        OrderUtils.clickOrder();
        OrderUtils.waitCreate(() -> new IpTable()
                .getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.CREATED_DATE)
                .shouldNot(Condition.exactText(""), Duration.ofSeconds(60)));
        return new IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
    }

    public VirtualIp selectIp(String name) {
        new IpTable().getRowByColumnValue(Column.IP_ADDRESS, name).get().shouldBe(Condition.visible).click();
        return new VirtualIp();
    }

    public List<String> getIpList() {
        return new IpTable().getColumnValuesList(Column.IP_ADDRESS);
    }

    public static class IpTable extends DataTable {

        public IpTable() {
            super(Column.IP_ADDRESS);
        }
    }
}
