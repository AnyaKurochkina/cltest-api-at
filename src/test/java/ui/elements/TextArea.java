package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Keys;

import static core.helper.StringUtils.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

public class TextArea implements TypifiedElement {
    @Getter
    SelenideElement textArea;

    public TextArea(SelenideElement input) {
        this.textArea = input;
    }

    public static TextArea byLabel(String label) {
        return new TextArea($x("//label[text()='{}']/../following::textarea[1]", label));
    }

    public static TextArea byName(String name) {
        return new TextArea($x("//textarea[@name='{}']", name));
    }

    public TextArea setValue(String value) {
        textArea.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        clear();
        textArea.setValue(value);
        return this;
    }

    public TextArea click() {
        textArea.scrollIntoView(scrollCenter);
        textArea.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        return this;
    }

    public void clear() {
        textArea.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }
}
