package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static core.helper.StringUtils.$x;

public class Input implements TypifiedElement {
    SelenideElement input;

    public Input(SelenideElement input) {
        this.input = input;
    }

    public static Input byLabel(String label) {
        return new Input($x("//label[starts-with(.,'{}')]/parent::*//input", label));
    }

    public void click() {
        input.click();
    }

    public void setValue(String value) {
        input.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        input.clear();
        input.setValue(value);
        input.sendKeys(Keys.chord(Keys.TAB));
    }

    public String getValue() {
        input.shouldBe(Condition.visible);
        return input.getValue();
    }

    public void clear() {
        input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }
}
