package ui.elements;

import com.codeborne.selenide.Selenide;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.JavascriptException;

import java.util.Map;

import static org.openqa.selenium.support.Color.fromString;

public class Alert implements TypifiedElement {

    private static final String script = "const waitTimeout = %d; " +
            "return new Promise((resolve, reject) => { " +
            "  const startTime = new Date().getTime(); " +
            "  const interval = setInterval(() => { " +
            "    const element = document.evaluate('//div[@role=\"alert\" and string-length(.)>1][button]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; " +
            "    if (element) { " +
            "      const text = element.textContent; " +
            "      const color = window.getComputedStyle(element).borderBottomColor; " +
            "      const button = element.querySelector('button'); " +
            "      button.click(); " +
            "      clearInterval(interval); " +
            "      resolve({ text, color }); " +
            "    } " +
            "    if (new Date().getTime() - startTime > waitTimeout) { " +
            "      clearInterval(interval); " +
            "      reject(new Error('Alert not found')); " +
            "    } " +
            "  }, 1000); " +
            "});";

    public Alert() {
    }

    private Map<String, String> getElementData(int timeout) {
        return Selenide.executeJavaScript(String.format(script, timeout));
    }

    public static Alert green(String text, Object... args) {
        return new Alert().check(Color.GREEN, text, args);
    }

    public static Alert red(String text, Object... args) {
        return new Alert().check(Color.RED, text, args);
    }

    @Step("Проверка alert на цвет {color} и вхождение текста {text}")
    public Alert check(Color color, String text, Object... args) {
        String message = StringUtils.format(text, args);
        final Map<String, String> data = getElementData(30000);
        Assertions.assertTrue(data.get("text").toLowerCase().contains(message.toLowerCase()),
                String.format("Найден Alert с текстом : '%s'\nОжидаемый текст: '%s'", data.get("text"), message));
        Assertions.assertEquals(color.getValue(), fromString(data.get("color")).asHex(), "Неверный цвет Alert");
        return this;
    }

    @Step("Проверка на отсутствие красных алертов")
    public static void checkNoRedAlerts() {
        try {
            Map<String, String> data = new Alert().getElementData(3000);
            Assertions.assertNotEquals(fromString(data.get("color")), Color.RED.getColor());
        } catch (JavascriptException ignore) {
        }
    }


    @Step("Закрытие всех всплывающих уведомлений")
    public static void closeAll() {
        try {
            for (int i = 0; i < 10; i++)
                new Alert().getElementData(10000);
        } catch (Throwable ignore) {
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
