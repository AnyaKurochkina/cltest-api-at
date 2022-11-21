package ui.elements;

import com.codeborne.selenide.SelenideElement;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Button implements TypifiedElement {
    private final SelenideElement button;

    public Button(SelenideElement button) {
        this.button = button;
    }

    public static Button byText(String text) {
        return new Button($x("//button[.='{}']", text));
    }

    public void click() {
        button.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }
}
