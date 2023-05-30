package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$$x;
import static org.openqa.selenium.support.Color.fromString;

public class Graph implements TypifiedElement {
    public static final Status COMPLETED = new Status("#4caf50");
    public static final Status STARTED = new Status("#ffb300");
    public static final Status ERROR = new Status("#da0b20");
    public static final Status SKIPPED = new Status("#0d69f2");
    public static final Status NOT_STARTED = new Status("#9e9e9e");

    private final Button btnFullScreen = Button.byAriaLabel("fullscreen");
    private final Button btnCloseWindow = Button.byText("Закрыть");

    private ElementsCollection getNodes() {
        return $$x("//*[contains(@class, 'react-flow__node-custom')]")
                .shouldBe(CollectionCondition.sizeNotEqual(0));
    }

    @Step("Проверка графа на недопустимые статусы нод")
    public void notContainsStatus(Status... colors) {
        btnFullScreen.click();
        getNodes().forEach(e -> {
            if (!Status.getColor(e).isDeniedStatuses(colors)) {
                e.hover();
                Assertions.fail(String.format("Нода графа '%s' имеет цвет недопустимый статус", e.$("strong").getText()));
            }
        });
        btnFullScreen.click();
        btnCloseWindow.click();
    }

    @Getter
    @AllArgsConstructor
    public static class Status {
        String color;

        public static Status getColor(SelenideElement e) {
            return new Status(fromString(e.getCssValue("background-color")).asHex());
        }

        @Override
        public String toString() {
            return color;
        }

        private boolean isDeniedStatuses(Status... statuses) {
            return Arrays.stream(statuses).anyMatch(s -> !color.equals(s.toString()));
        }
    }
}
