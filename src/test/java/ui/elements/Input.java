package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;

import static core.helper.StringUtils.$x;

public class Input implements TypifiedElement {
    @Getter
    SelenideElement input;

    public Input(SelenideElement input) {
        this.input = input;
    }

    public static Input byLabel(String label) {
        return new Input($x("//label[starts-with(.,'{}')]/parent::*//input", label));
    }

    public static Input byPlaceholder(String placeholder) {
        return new Input($x("//input[@placeholder='{}']", placeholder));
    }

    public void click() {
        input.click();
    }

    public String getValue() {
        input.shouldBe(Condition.visible);
        return input.getValue();
    }

    public void setValue(String value) {
        input.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        clear();
        input.setValue(value);
    }

    public void clear() {
        input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }
}
