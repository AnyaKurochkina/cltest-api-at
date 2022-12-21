package ui.elements;

import com.codeborne.selenide.*;
import core.helper.DataFileHelper;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.executeJavaScript;
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
        return $$x("(//div[@role='alert'])").shouldBe(CollectionCondition.anyMatch("Не найден alert", WebElement::isDisplayed));
    }

    public static Alert green(String text, Object... args) {
        return new Alert().check(Color.GREEN, text, args);
    }

    public static Alert red(String text, Object... args) {
        return new Alert().check(Color.RED, text, args);
    }

    public void waitClose() {
        element.shouldNot(Condition.visible);
    }

    @Step("Проверка alert на цвет {color} и вхождение текста {text}")
    public Alert check(Color color, String text, Object... args) {
        String message = StringUtils.format(text, args);
        element = getElements().filter(Condition.visible).stream()
                .filter(e -> e.getText().toLowerCase().contains(message.toLowerCase()) && fromString(e.getCssValue("border-bottom-color")).asHex().equals(color.getValue()))
                .findFirst().orElseThrow(() -> new NotFoundException(String.format("Не найден Alert с сообщением '%s' и цветом %s", text, color)));
        return this;
    }

    public static void closeAll() {
        SelenideElement e = new Alert().getElements().first();
        while (e.exists() && e.isDisplayed()) {
            Waiting.sleep(3000);
            executeJavaScript("arguments[0].style.display = 'none'", e);
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
