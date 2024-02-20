package ui.t1.pages.cloudEngine.vpc;

import core.utils.Waiting;
import lombok.Getter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

@Getter
public class VirtualIpCreate {
    private String region;
    private String name;
    private String network;
    private String mode;
    private Boolean l2;
    private Boolean internet;
    private String networkInterface;
    private String vMac;

    private String ip;

    public VirtualIpCreate setRegion(String region) {
        Select.byLabel("Регион").set(region);
        this.region = region;
        return this;
    }

    public VirtualIpCreate setName(String name) {
        Input.byLabel("Имя виртуального IP-адреса").setValue(name);
        this.name = name;
        return this;
    }

    public VirtualIpCreate setNetwork(String network) {
        Select.byLabel("Подсеть").setContains(network);
        this.network = network;
        return this;
    }

    public VirtualIpCreate setMode(String mode) {
        RadioGroup.byLabel("Режим").select(mode);
        this.mode = mode;
        return this;
    }

    public VirtualIpCreate setL2(Boolean l2) {
        Switch.byText("Задействовать L2").setEnabled(l2);
        this.l2 = l2;
        return this;
    }

    public VirtualIpCreate setVMac(String vMac) {
        Input.byLabel("vMAC-адрес").setValue(vMac);
        this.vMac = vMac;
        return this;
    }

    public VirtualIpCreate setInternet(Boolean internet) {
        Switch.byText("Разрешить доступ из Интернет").setEnabled(internet);
        this.internet = internet;
        return this;
    }

    public VirtualIpCreate setNetworkInterface(String networkInterface) {
        Switch.byText("Задать IP адрес сетевого интерфейса").setEnabled(true);
        Input.byPlaceholder("0.0.0.0").setValue(networkInterface);
        this.networkInterface = networkInterface;
        return this;
    }

    public VirtualIpCreate clickOrder() {
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
        VirtualIpList.IpTable ipTable = new VirtualIpList.IpTable();
        OrderUtils.waitCreate(() -> {
            if (!ipTable.isEmpty()) {
                String oldIp = ipTable.update().getFirstValueByColumn(Column.IP_ADDRESS);
                Waiting.sleep(() -> !ipTable.update().getFirstValueByColumn(Column.IP_ADDRESS).equals(oldIp), Duration.ofMinutes(2));
            }
            else
                Waiting.sleep(() -> !ipTable.update().isEmpty(), Duration.ofMinutes(2));
            Waiting.sleep(() -> !ipTable.update().getFirstValueByColumn("Сеть").equals("—"), Duration.ofMinutes(1));
        });
        ip = ipTable.update().getFirstValueByColumn(Column.IP_ADDRESS);
        return this;
    }
}
