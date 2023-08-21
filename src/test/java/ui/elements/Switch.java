package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import java.util.function.Supplier;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class Switch implements TypifiedElement {
    private final Supplier<SelenideElement> label;

    public Switch(SelenideElement element) {
        this.label = () -> element;
    }

    private Switch(Supplier<SelenideElement> element) {
        this.label = element;
    }

    @Step("Получение Switch по тексту '{text}'")
    public static Switch byText(String text) {
        return new Switch(() -> TypifiedElement.findNearestElement("//label[@role='switch']", String.format("//*[text()='%s']", text)));
    }


    @Step("Получение Switch по input name {name}")
    public static Switch byInputName(String name) {
        return new Switch($x("//label[@role='switch'][input[@name='{}']]", name));
    }

    public boolean isEnabled() {
        return label.get().is(Condition.attribute("aria-checked", "true"));
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled)
            label.get().hover().shouldBe(clickableCnd).click();
        Waiting.sleep(1500);
        Assertions.assertEquals(enabled, isEnabled());
    }

    public SelenideElement getLabel() {
        return label.get();
    }
}
