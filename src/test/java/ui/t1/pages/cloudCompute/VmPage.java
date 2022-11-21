package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.DropDown;
import ui.elements.Input;

import java.util.ArrayList;
import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

//new VirtualMachinePage().name("name").description("desc").addSecurityGroups("default").image(new Image("Ubuntu", "20.04")).sshKey("qw")
@Getter
public class VmPage {
    private String name;
    private String description;
    private String availabilityZone;
    private Image image;
    private Integer bootSize;
    private String bootType;
    private Boolean deleteOnTermination;
    private String flavorName;
    private String flavor;
    private String subnet;
    private Boolean publicIp;
    private final List<String> securityGroups = new ArrayList<>();
    private String sshKey;

    public VmPage setName(String name) {
        this.name = name;
        Input.byLabel("Имя виртуальной машины").setValue(name);
        return this;
    }

    public VmPage setDescription(String description) {
        this.description = description;
        Input.byLabel("Описание").setValue(description);
        return this;
    }

    public VmPage addSecurityGroups(String securityGroups) {
        this.securityGroups.add(securityGroups);
        DropDown.byLabel("Группы безопасности сетевого интерфейса").select(securityGroups);
        return this;
    }

    public VmPage setImage(Image image) {
        this.image = image;
        SelectBox.byName(image);
        return this;
    }

    public VmPage setSshKey(String sshKey) {
        this.sshKey = sshKey;
        DropDown.byLabel("Публичный SSH ключ").selectByTextContains(sshKey);
        return this;
    }

    public VmPage clickOrder() {
        Button.byText("Заказать").click();
        return this;
    }

    @AllArgsConstructor
    public static class Image {
        String os;
        String version;
    }

    private static class SelectBox {
        SelenideElement select;

        private SelectBox(SelenideElement element) {
            element.shouldBe(Condition.visible).scrollIntoView(scrollCenter);
            this.select = element;
        }

        public static void byName(Image image) {
            SelectBox selectBox = new SelectBox($x("//*[.='{}']/parent::*//*[name()='svg']", image.os));
            selectBox.select(image.version);
        }

        private void select(String text) {
            select.parent().parent().parent().click();
            select.click();
            $x("//*[@title = '{}']", text).shouldBe(activeCnd)
                    .hover().shouldBe(clickableCnd)
                    .click();
        }
    }
}
