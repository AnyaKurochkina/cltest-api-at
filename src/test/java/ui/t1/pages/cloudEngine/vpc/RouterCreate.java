package ui.t1.pages.cloudEngine.vpc;

import core.utils.Waiting;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RouterCreate {
    private String region;
    private String name;
    private final List<String> networks = new ArrayList<>();
    private String desc;

    public RouterCreate setRegion(String region) {
        Select.byLabel("Регион").set(region);
        this.region = region;
        return this;
    }

    public RouterCreate setName(String name) {
        Input.byLabel("Имя").setValue(name);
        this.name = name;
        return this;
    }

    public RouterCreate addNetwork(String network) {
        Select.byLabel("Подключенные сети").setContains(network);
        this.networks.add(network);
        return this;
    }

    public RouterCreate setDesc(String desc) {
        Input.byLabel("Описание").setValue(desc);
        this.desc = desc;
        return this;
    }

    public RouterCreate clickOrder() {
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
        OrderUtils.waitCreate(() -> OrderUtils.waitCreate(() -> Waiting.find(() -> new RouterList.RouterTable().isColumnValueEquals(Column.NAME, name), Duration.ofMinutes(1))));
        OrderUtils.waitCreate(() -> OrderUtils.waitCreate(() -> Waiting.find(() -> !new RouterList.RouterTable()
                .getRowByColumnValue(Column.NAME, name)
                .getValueByColumn(Column.CREATED_DATE)
                .isEmpty(), Duration.ofMinutes(2))));
        return this;
    }
}
