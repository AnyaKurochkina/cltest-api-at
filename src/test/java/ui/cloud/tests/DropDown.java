package ui.cloud.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$x;

public class DropDown {
    @Getter
    SelenideElement element;

    public DropDown(SelenideElement element) {
        this.element = element;
    }

    public static DropDown name(String name){
        return new DropDown($x(String.format("//div[label[text()='%s']]/div", name)));
    }

    public void select(String value){
        element.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover()
                .shouldNotBe(Condition.cssValue("cursor", "default")).click();
        $x(String.format("//ul/li[text()='%s']", value))
                .shouldBe(Condition.enabled)
                .click();
    }
}
