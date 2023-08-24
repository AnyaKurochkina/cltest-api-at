package ui.t1.pages.cloudEngine.vpc;

import core.utils.Waiting;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;
import java.util.List;

@Getter
public class RouterCreate {
    private String region;
    private String name;
    private List<String> networks;
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
        MultiSelect.byLabel("Подключенные сети").setContains(network);
        this.networks.add(network);
        return this;
    }

    public RouterCreate setDesc(String desc) {
        RadioGroup.byLabel("Описание").select(desc);
        this.desc = desc;
        return this;
    }

    public RouterCreate clickOrder() {
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
//        boolean isEmpty = new VirtualIpList.IpTable().isEmpty();
//        OrderUtils.waitCreate(() -> {
//            if(!isEmpty) {
//                String oldIp = new VirtualIpList.IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
//                Waiting.find(()-> !new VirtualIpList.IpTable().getFirstValueByColumn(Column.IP_ADDRESS).contains(oldIp), Duration.ofSeconds(80));
//            }
//            else
//                Waiting.find(()-> !new VirtualIpList.IpTable().isEmpty(), Duration.ofMinutes(1));
//            Waiting.find(()-> !new VirtualIpList.IpTable().getFirstValueByColumn("Сеть").equals("—"), Duration.ofMinutes(1));
//        });
//        ip = new VirtualIpList.IpTable().getFirstValueByColumn(Column.IP_ADDRESS);
        return this;
    }
}
