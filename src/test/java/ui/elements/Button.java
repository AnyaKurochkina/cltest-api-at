package ui.elements;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import lombok.Getter;
import org.intellij.lang.annotations.Language;

import java.time.Duration;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

@Getter
public class Button implements TypifiedElement {
    @Getter
    private final SelenideElement button;

    public Button(SelenideElement button) {
        this.button = button;
    }

    public static Button byElement(SelenideElement button) {
        return new Button(button);
    }

    public static Button byText(String text) {
        return byText(text, 1);
    }

    public static Button byTextContains(String text) {
        return byTextContains(text, 1);
    }

    public static Button byText(String text, int index) {
        return new Button($x("(//button[.='{}'])" + postfix, text, TypifiedElement.getIndex(index)));
    }

    public static Button byTextContains(String text, int index) {
        return new Button($x("(//button[contains(., '{}')])" + postfix, text, TypifiedElement.getIndex(index)));
    }

    public static Button byAriaLabel(String value) {
        return new Button($x("//button[@aria-label='{}']", value));
    }

    public static Button byId(String id) {
        return new Button($x("//button[@id='{}']", id));
    }

    public static Button byDataTestId(String id) {
        return new Button($x("//button[@data-testid='{}']", id));
    }

    public static Button byXpath(@Language("XPath") String xPath) {
        return new Button($x(xPath));
    }

    public boolean isVisible() {
        return Waiting.sleep(button::isDisplayed, Duration.ofSeconds(10));
    }

    public void click() {
        button.hover().shouldBe(activeCnd).shouldBe(clickableCnd).click();
        Waiting.sleep(200);
    }
}
