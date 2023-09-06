package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.Objects;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.exist;
import static org.openqa.selenium.support.Color.fromString;

public class Alert implements TypifiedElement {
    SelenideElement element;

    public Alert(SelenideElement element) {
        this.element = element;
    }

    public Alert() {
    }

    private SelenideElement getElement() {
        if (Objects.nonNull(element))
            return element;
        return $x("//div[@role='alert' and string-length(.)>1][button]");
    }

    public static Alert green(String text, Object... args) {
        return new Alert().check(Color.GREEN, text, args).close();
    }

    public static Alert red(String text, Object... args) {
        return new Alert().check(Color.RED, text, args).close();
    }

    public void waitClose() {
        try {
            if (element.exists())
                element.shouldNot(Condition.visible);
        } catch (Throwable ignored) {
        }
    }

    public Alert close() {
        try {
            Button button = Button.byElement(getElement().$x("button[.='']"));
            button.click();
            waitClose();
        } catch (Throwable ignored) {
        }
        return this;
    }

    @Step("Проверка alert на цвет {color} и вхождение текста {text}")
    public Alert check(Color color, String text, Object... args) {
        String message = StringUtils.format(text, args);
        element = getElement().shouldBe(Condition.visible);
        final String elementText = element.getText();
        Assertions.assertTrue(elementText.toLowerCase().contains(message.toLowerCase()),
                String.format("Найден Alert с текстом : '%s'\nОжидаемый текст: '%s'", elementText, message));
        Assertions.assertEquals(color.getValue(), fromString(element.getCssValue("border-bottom-color")).asHex(),
                "Неверный цвет Alert");
        return this;
    }

    @Step("Проверка на отсутствие красных алертов")
    public static void  checkNoRedAlerts() {
        SelenideElement element = new Alert().getElement();
        if (exist(element, Duration.ofSeconds(3)))
            Assertions.assertNotEquals(fromString(element.getCssValue("border-bottom-color")).asHex(), Color.RED.getColor());
    }


    @Step("Закрытие всех всплывающих уведомлений")
    public static void closeAll() {
        try {
            SelenideElement e = new Alert().getElement().shouldBe(Condition.visible, Duration.ofSeconds(15));
            while (e.exists() && e.isDisplayed()) {
                new Alert(e).close();
            }
        } catch (Throwable ignored) {
        }
    }

    public enum Color {
        RED("#d92020"),
        GREEN("#1ba049");
        @Getter
        String color;

        public String getValue() {
            return color;
        }

        Color(String color) {
            this.color = color;
        }
    }
}
