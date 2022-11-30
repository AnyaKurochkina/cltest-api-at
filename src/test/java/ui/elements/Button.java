package ui.elements;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Button implements TypifiedElement {
    private final SelenideElement button;

    public Button(SelenideElement button) {
        this.button = button;
    }

    @Step("Получение Button по тексту {text}")
    public static Button byText(String text) {
        return new Button($x("//button[.='{}']", text));
    }

    public void click() {
        button.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }
}
