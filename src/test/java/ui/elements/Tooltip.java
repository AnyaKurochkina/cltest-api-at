package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static core.helper.StringUtils.$x;

public class Tooltip implements TypifiedElement {
    @Getter
    SelenideElement tooltip;
    SelenideElement element = $x("//div[@role='tooltip']");

    public Tooltip() {
        element.shouldBe(Condition.visible);
    }

    public static boolean isVisible(){
        return $x("//div[@role='tooltip']").isDisplayed();
    }

    @Override
    public String toString() {
        return element.getAttribute("content");
    }
}
