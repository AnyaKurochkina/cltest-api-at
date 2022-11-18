package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import ui.elements.DataTable;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.ArrayList;
import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class VirtualMachinePage {
    String name;
    String description;
    String availabilityZone;
    Image image;
    Integer bootSize;
    String bootType;
    Boolean deleteOnTermination;
    String flavorName;
    String flavor;
    String subnet;
    Boolean publicIp;
    List<String> securityGroups = new ArrayList<>();
    String sshKey;

    //new VirtualMachinePage().name("name").description("desc").addSecurityGroups("default").image(new VirtualMachinePage.Image("Ubuntu", "20.04")).sshKey("qw")

    public VirtualMachinePage name(String name){
        this.name = name;
        Input.byLabel("Имя виртуальной машины").setValue(name);
        return this;
    }

    public VirtualMachinePage description(String description){
        this.description = description;
        Input.byLabel("Описание").setValue(description);
        return this;
    }

    public VirtualMachinePage addSecurityGroups(String securityGroups){
        this.securityGroups.add(securityGroups);
        DropDown.byLabel("Группы безопасности сетевого интерфейса").select(securityGroups);
        return this;
    }

    public VirtualMachinePage image(Image image){
        this.image = image;
        SelectBox.byName(image);
        return this;
    }

    public VirtualMachinePage sshKey(String sshKey){
        this.sshKey = sshKey;
        DropDown.byLabel("Публичный SSH ключ").selectByTextContains(sshKey);
        return this;
    }

    private static class VirtualMachineTable extends DataTable{
        public VirtualMachineTable() {
            super("Дата создания");
        }
    }

    @AllArgsConstructor
    public static class Image{
        String os;
        String version;
    }

    private static class SelectBox {
        SelenideElement select;

        private SelectBox(SelenideElement element) {
            element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
            this.select = element;
        }

        public static void byName(Image image){
            SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
            selectBox.select(image.version);
        }

        private void select(String text){
            select.parent().parent().parent().click();
            select.click();
            $x("//*[@title = '{}']", text).shouldBe(activeCnd)
                    .hover().shouldBe(clickableCnd)
                    .click();
        }
    }
}
