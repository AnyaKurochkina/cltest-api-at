package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import static core.helper.StringUtils.$x;

public class Alert implements TypifiedElement {
    SelenideElement element = $x("//div[@role='alert']");

    public Alert() {
        element.shouldBe(Condition.visible);
    }

    @Step("Закрыть alert")
    public void close() {
        element.$("button").shouldBe(Condition.enabled).click();
        element.shouldNotBe(Condition.visible);
    }

    @Step("Проверка alert на вхождение текста {text}")
    public Alert checkText(String text) {
        String message = element.getText();
        Assertions.assertTrue(message.toLowerCase().contains(text.toLowerCase()), String.format("Alert с сообщением '%s' не содержит текст '%s'", message, text));
        return this;
    }

    @Step("Проверка alert на цвет {color}")
    public Alert checkColor(Color color) {
        Assertions.assertEquals(color.toString(), org.openqa.selenium.support.Color.fromString(element.getCssValue("background-color")).asHex(),
                "Произошла ошибка: " + element.getText());
        return this;
    }

    public enum Color {
        RED("#d32f2f"),
        GREEN("#43a047");
        @Getter
        String color;

        @Override
        public String toString() {
            return color;
        }

        Color(String color) {
            this.color = color;
        }
    }
}
