package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static core.helper.StringUtils.$x;

//new VirtualMachinePage().name("name").description("desc").addSecurityGroups("default").image(new Image("Ubuntu", "20.04")).sshKey("qw")
@Getter
public class VmCreate {
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
    private String publicIp;
    private final List<String> securityGroups = new ArrayList<>();
    private String sshKey;

    public VmCreate setName(String name) {
        this.name = name;
        Input.byLabel("Имя виртуальной машины").setValue(name);
        return this;
    }

    public VmCreate addDisk(String name, int size, String type, boolean deleteOnTermination){
        new Button($x("//button[contains(@class, 'array-item-add')]")).click();
        Input.byLabel("Имя диска", -1).setValue(name);
        Input.byLabel("Размер диска, Гб", -1).setValue(size);
        DropDown.byLabel("Тип", -1).selectByTextContains(type);
        CheckBox.byLabel("Удалять вместе с виртуальной машиной", -1).setChecked(deleteOnTermination);
        return this;
    }

    public VmCreate setBootSize(int bootSize) {
        this.bootSize = bootSize;
        Input.byLabel("Размер диска, Гб").setValue(bootSize);
        return this;
    }

    public VmCreate setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
        DropDown.byLabel("Зона доступности").select(availabilityZone);
        return this;
    }

    public VmCreate setDeleteOnTermination(boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
        CheckBox.byLabel("Удалять вместе с виртуальной машиной").setChecked(deleteOnTermination);
        return this;
    }

    public VmCreate setDescription(String description) {
        this.description = description;
        Input.byLabel("Описание").setValue(description);
        return this;
    }

    public VmCreate addSecurityGroups(String securityGroups) {
        this.securityGroups.add(securityGroups);
        DropDown.byLabel("Группы безопасности сетевого интерфейса").select(securityGroups);
        return this;
    }

    public VmCreate setImage(SelectBox.Image image) {
        this.image = image;
        SelectBox.setMarketPlaceImage(image);
        return this;
    }

    public VmCreate setSshKey(String sshKey) {
        this.sshKey = sshKey;
        DropDown.byLabel("Публичный SSH ключ").selectByTextContains(sshKey);
        return this;
    }

    public VmCreate setPublicIp(String publicIp) {
        this.publicIp = publicIp;
        Switch.byLabel("Подключить публичный IP").setEnabled(true);
        DropDown.byLabel("Публичный IP").select(publicIp);
        return this;
    }

    public VmCreate clickOrder() {
        EntitiesUtils.clickOrder();
        new VmList.VmTable()
                .getRowByColumnValue(VmList.VmTable.COLUMN_NAME, name)
                .getElementByColumn(VmList.VmTable.COLUMN_STATUS)
                .shouldBe(Condition.matchText("Включено"), Duration.ofMinutes(1));
        return this;
    }
}
