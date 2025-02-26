package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;

import java.util.Objects;

import static core.helper.StringUtils.$x;

public class Slider extends Input {
    SelenideElement slider;
    SelenideElement point;

    public Slider(SelenideElement e) {
        super(e.$("input"));
        point = e.$("div[role='slider']");
        this.slider = e;
    }

    @Step("Получение Slider по label {label}")
    public static Slider byLabel(String label) {
        return new Slider($x("(//*[starts-with(.,'{}')]/parent::*)[last()]", label));
    }

    @Override
    public void setValue(Object value) {
        input.shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(value), Keys.TAB);
        Assertions.assertEquals(String.valueOf(value), point.getAttribute("aria-valuenow"));
    }

    public String getMinValue() {
        return Objects.requireNonNull(point.getAttribute("aria-valuemin"));
    }

    public String getMaxValue() {
        return Objects.requireNonNull(point.getAttribute("aria-valuemax"));
    }
}
