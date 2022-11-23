package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import static core.helper.StringUtils.$x;

public class Alert implements TypifiedElement {
    SelenideElement element = $x("(//div[@role='alert' and descendant::button])[last()]");

    public Alert() {
        element.shouldBe(Condition.visible).shouldBe(Condition.matchText(".{1,}"));
    }

    @Step("Закрыть alert")
    public void close() {
        element.$("button").hover().shouldBe(Condition.enabled).click();
        element.shouldNotBe(Condition.visible);
    }

    @Step("Проверка alert на вхождение текста {text}")
    public Alert checkText(String text, Object... args) {
        text = StringUtils.format(text,args);
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

    public void closeAll(){
        while (element.exists() && element.isDisplayed()){
            try {
                close();
            }
            catch (ElementNotFound ignored){}
            Waiting.sleep(2000);
        }
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
