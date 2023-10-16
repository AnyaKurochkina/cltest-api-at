package ui.t1.pages.cloudEngine.compute;

import core.utils.Waiting;
import lombok.Getter;
import lombok.Setter;
import ui.cloud.pages.orders.OrderUtils;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.vpc.VirtualIpCreate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static core.helper.StringUtils.$x;

@Getter
public class VmCreate {
    private String name;
    private String description;
    private String region;
    private String availabilityZone;
    private SelectBox.Image image;
    private String userImage;
    private Long bootSize;
    private String bootType;
    private Boolean deleteOnTermination;
    private String flavorName;
    private String flavor;
    private String network;
    private String subnet;
    private String networkInterface;
    private String publicIp;
    private List<String> securityGroups;
    private String sshKey;

    private Duration createTimeout = Duration.ofMinutes(3);

    public VmCreate setCreateTimeout(Duration timeout) {
        this.createTimeout = timeout;
        return this;
    }

    public VmCreate setName(String name) {
        this.name = name;
        Input.byLabel("Имя сервера").setValue(name);
        return this;
    }

    public VmCreate setFlavorName(String flavorName) {
        this.flavorName = Select.byLabel("Серия").setContains(flavorName);
        return this;
    }

    public VmCreate setNetworkInterface(String networkInterface) {
        Switch.byText("Задать IP адрес сетевого интерфейса").setEnabled(true);
        Input.byPlaceholder("0.0.0.0").setValue(networkInterface);
        this.networkInterface = networkInterface;
        return this;
    }

    public VmCreate setFlavor(String flavor) {
        this.flavor = Select.byLabel("Конфигурация").set(flavor);
        return this;
    }

    public VmCreate addDisk(String name, int size, String type, boolean deleteOnTermination) {
        new Button($x("//button[contains(@class, 'array-item-add')]")).click();
        Input.byLabel("Имя диска", -1).setValue(name);
        Input.byLabel("Размер диска, Гб", -1).setValue(size);
        Select.byLabel("Тип", -1).setContains(type);
        CheckBox.byLabel("Удалять вместе с сервером", -1).setChecked(deleteOnTermination);
        return this;
    }

    public VmCreate setBootSize(long bootSize) {
        this.bootSize = bootSize;
        Input.byLabel("Размер диска, Гб").setValue(bootSize);
        return this;
    }

    public VmCreate setBootType(String type) {
        this.bootType = Select.byLabel("Тип", 1).setContains(type);
        return this;
    }

    public VmCreate seNetwork(String network) {
        this.network = Select.byLabel("Сеть").setContains(network);
        return this;
    }

    public VmCreate setSubnet(String subnet) {
        this.subnet = Select.byLabel("Подсеть", 1).setContains(subnet);
        return this;
    }

    public VmCreate setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = Select.byLabel("Зона доступности").set(availabilityZone);
        Waiting.sleep(1000);
        return this;
    }

    public VmCreate setRegion(String region) {
        this.region = Select.byLabel("Регион").set(region);
        return this;
    }

    public VmCreate setDeleteOnTermination(boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
        CheckBox.byLabel("Удалять вместе с сервером").setChecked(deleteOnTermination);
        return this;
    }

    public VmCreate setDescription(String description) {
        this.description = description;
        Input.byLabel("Описание").setValue(description);
        return this;
    }

    public VmCreate addSecurityGroups(String securityGroups) {
        Select select = Select.byLabel("Группы безопасности сетевого интерфейса");
        if (Objects.isNull(this.securityGroups)) {
            this.securityGroups = new ArrayList<>();
        }
        this.securityGroups.add(securityGroups);
        select.set(securityGroups);
        return this;
    }

    public VmCreate setImage(SelectBox.Image image) {
        this.image = image;
        SelectBox.setOsImage(image);
        return this;
    }

    public VmCreate setUserImage(String image) {
        this.userImage = image;
        SelectBox.setUserImage(image);
        return this;
    }

    public VmCreate setMarketplaceImage(String image) {
        this.userImage = image;
        SelectBox.setMarketplaceImage(image);
        return this;
    }

    public VmCreate setSshKey(String sshKey) {
        this.sshKey = MultiSelect.byLabel("SSH-ключи").setContains(sshKey);
        return this;
    }

    public VmCreate setSwitchPublicIp(boolean checked){
        Switch.byText("Подключить публичный IP адрес").setEnabled(checked);
        return this;
    }

    public VmCreate setPublicIp(String publicIp) {
        setSwitchPublicIp(true);
        Waiting.sleep(1000);
        this.publicIp = Select.byLabel("Публичный IP адрес").set(publicIp);
        return this;
    }

    public VmCreate clickOrder() {
        /* Временный костыль для prod */

        Select.byLabel("Серия").setContains("Intel Cascade Lake 3.0 GHz");
        Waiting.sleep(3000);

        /* */
        OrderUtils.clickOrder();
        OrderUtils.waitCreate(() -> Waiting.find(() -> new VmList.VmTable()
                .getRowByColumnValue(Column.NAME, name)
                .getValueByColumn(Column.STATUS)
                .contains("Включено"), createTimeout));
        return this;
    }
}
