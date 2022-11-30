package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
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
        return byLabel(label, 1);
    }

    @Step("Получение Input по label {label} с индексом {index}")
    public static Input byLabel(String label, int index) {
        return new Input($x("(//label[starts-with(.,'{}')]/parent::*//input)" + postfix, label, TypifiedElement.getIndex(index)));
    }

    public static Input byLabelV2(String label) {
        return new Input($x("//label[starts-with(.,'{}')]/following::input[1]", label));
    }

    public static Input byPlaceholder(String placeholder) {
        return new Input($x("//input[@placeholder='{}']", placeholder));
    }

    public static Input byName(String name) {
        return new Input($x("//input[@name='{}']", name));
    }

    public void click() {
        input.click();
    }

    public String getValue() {
        input.shouldBe(Condition.visible);
        return input.getValue();
    }

    public void setValue(Object value) {
        input.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        clear();
        input.setValue(String.valueOf(value));
    }

    public void clear() {
        input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }
}
