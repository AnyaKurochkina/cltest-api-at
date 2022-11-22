package ui.t1.pages.cloudCompute;

import lombok.Getter;
import ui.elements.Button;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.ArrayList;
import java.util.List;

//new VirtualMachinePage().name("name").description("desc").addSecurityGroups("default").image(new Image("Ubuntu", "20.04")).sshKey("qw")
@Getter
public class VmCreatePage {
    private String name;
    private String description;
    private String availabilityZone;
    private SelectBox.Image image;
    private Integer bootSize;
    private String bootType;
    private Boolean deleteOnTermination;
    private String flavorName;
    private String flavor;
    private String subnet;
    private Boolean publicIp;
    private final List<String> securityGroups = new ArrayList<>();
    private String sshKey;

    public VmCreatePage setName(String name) {
        this.name = name;
        Input.byLabel("Имя виртуальной машины").setValue(name);
        return this;
    }

    public VmCreatePage setDescription(String description) {
        this.description = description;
        Input.byLabel("Описание").setValue(description);
        return this;
    }

    public VmCreatePage addSecurityGroups(String securityGroups) {
        this.securityGroups.add(securityGroups);
        DropDown.byLabel("Группы безопасности сетевого интерфейса").select(securityGroups);
        return this;
    }

    public VmCreatePage setImage(SelectBox.Image image) {
        this.image = image;
        SelectBox.setMarketPlaceImage(image);
        return this;
    }

    public VmCreatePage setSshKey(String sshKey) {
        this.sshKey = sshKey;
        DropDown.byLabel("Публичный SSH ключ").selectByTextContains(sshKey);
        return this;
    }

    public VmCreatePage clickOrder() {
        Button.byText("Заказать").click();
        return this;
    }
}
