package ui.elements;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShouldNot;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$$x;
import static org.openqa.selenium.support.Color.fromString;

public class Alert implements TypifiedElement {
    SelenideElement element;

    public Alert(SelenideElement element) {
        this.element = element;
    }

    private Alert() {
    }

    private ElementsCollection getElements() {
        if (Objects.nonNull(element))
            return new ElementsCollection((Driver) Selenide.webdriver(), Collections.singletonList(element));
        return $$x("(//div[@role='alert' and @aria-live])");
    }

    public static Alert green(String text, Object... args) {
        return new Alert().check(Color.GREEN, text, args);
    }

    public static Alert red(String text, Object... args) {
        return new Alert().check(Color.RED, text, args);
    }

    public void waitClose() {
        try {
            element.shouldNot(Condition.visible);
        } catch (ElementNotFound ignored) {
        }
    }

    public void close() {
        try {
            SelenideElement button = element.$("button").shouldBe(clickableCnd);
            button.click();
            button.shouldNotBe(Condition.visible);
        } catch (ElementNotFound ignored) {}
    }

    @Step("Проверка alert на цвет {color} и вхождение текста {text}")
    public Alert check(Color color, String text, Object... args) {
        String message = StringUtils.format(text, args);
        element = getElements().shouldBe(CollectionCondition.anyMatch("Alert не найден", WebElement::isDisplayed))
                .shouldBe(CollectionCondition.noneMatch("Alert не найден", e -> e.getText().equals("")))
                .filter(Condition.visible).stream()
                .filter(e -> e.getText().toLowerCase().contains(message.toLowerCase()) && fromString(e.getCssValue("border-bottom-color")).asHex().equals(color.getValue()))
                .findFirst().orElseThrow(() -> new NotFoundException(String.format("Не найден Alert с сообщением '%s' и цветом %s", text, color)));
        close();
        return this;
    }

    public static void closeAll() {
        try {
            SelenideElement e = new Alert().getElements().first().shouldBe(Condition.visible, Duration.ofSeconds(5));
            while (e.exists() && e.isDisplayed()) {
                new Alert(e).close();
            }
        } catch (ElementShouldNot ignored) {}
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
