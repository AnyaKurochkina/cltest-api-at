package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.intellij.lang.annotations.Language;
import org.openqa.selenium.Keys;

import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

public class TextArea implements TypifiedElement {
    @Getter
    SelenideElement element;

    public TextArea(SelenideElement input) {
        this.element = input;
    }

    public static TextArea byLabel(String label) {
        return new TextArea($x("//*[text()='{}']/following::textarea[1]", label));
    }

    public static TextArea byLabelContains(String label) {
        return new TextArea($x("//*[contains(text(),'{}')]/following::textarea[1]", label));
    }

    public static TextArea byName(String name) {
        return TextArea.byName(name, 1);
    }

    public static TextArea byName(String name, int index) {
        return new TextArea($x("(//textarea[@name='{}'])" + postfix, name, TypifiedElement.getIndex(index)));
    }

    public static TextArea byXPath(@Language("XPath") String xPath) {
        return new TextArea($x(xPath));
    }

    public void setValue(String value) {
        element.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        clear();
        element.setValue(value);
    }

    public void setValueAndPressEnter(String value) {
        element.shouldBe(Condition.visible).shouldBe(Condition.enabled);
        clear();
        element.setValue(value).pressEnter();
    }

    public TextArea click() {
        element.scrollIntoView(scrollCenter);
        element.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        return this;
    }

    public void clear() {
        element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }

    @Step("Получение значения TextArea")
    public String getValue() {
        return element.getValue();
    }

    @Step("Получение значения TextArea без пробелов")
    public String getWhitespacesRemovedValue() {
        return element.getValue().replaceAll("\\s", "");
    }
}
