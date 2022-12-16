package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.cloud.pages.EntitiesUtils;
import ui.elements.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static core.helper.StringUtils.$x;

//new VirtualMachinePage().name("name").description("desc").addSecurityGroups("default").image(new Image("Ubuntu", "20.04")).sshKey("qw")
@Getter
public class VmCreate {
    private String name;
    private String description;
    private String availabilityZone;
    private SelectBox.Image image;
    private String userImage;
    private Long bootSize;
    private String bootType;
    private Boolean deleteOnTermination;
    private String flavorName;
    private String flavor;
    private String subnet;
    private String publicIp;
    private List<String> securityGroups;
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

    public VmCreate setBootSize(long bootSize) {
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
        DropDown dropDown = DropDown.byLabel("Группы безопасности сетевого интерфейса");
        if(Objects.isNull(this.securityGroups)){
            this.securityGroups = new ArrayList<>();
            dropDown.clear();
        }
        this.securityGroups.add(securityGroups);
        dropDown.select(securityGroups);
        return this;
    }

    public VmCreate setImage(SelectBox.Image image) {
        this.image = image;
        SelectBox.setMarketPlaceImage(image);
        return this;
    }

    public VmCreate setUserImage(String image) {
        this.userImage = image;
        SelectBox.setUserImage(image);
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
