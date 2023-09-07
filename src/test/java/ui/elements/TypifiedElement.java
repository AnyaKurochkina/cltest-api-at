package ui.elements;

import com.codeborne.selenide.*;
import core.utils.Waiting;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;

public interface TypifiedElement {
    String scrollCenter = "{block: 'center'}";
    String postfix = "[{}]";

    class Wait extends Condition {
        private final int ms;

        public Wait(int ms) {
            super("Wait time");
            this.ms = ms;
        }

        @Override
        public boolean apply(@NotNull Driver driver, @NotNull WebElement webElement) {
            Waiting.sleep(ms);
            return true;
        }
    }

    static void refresh() {
        Selenide.refresh();
        checkProject();
    }

    /**
     * Получить ближайший по DOM элемент с xpathSearchElement к элементу xpathNearElement
     * *[contains(text(), '₽/сут.')]    button[.='Действия']
     * @param xpathSearchElement xpath искомого element
     * @param xpathNearElement xpath близкого к искомому element элемента
     * @return искомый element
     */
    static SelenideElement getNearElement(@Language("XPath") String xpathSearchElement, @Language("XPath") String xpathNearElement) {
        return $x(String.format("(//%s/ancestor-or-self::*[count(.//%s) = 1])[last()]//%s", xpathNearElement, xpathSearchElement, xpathSearchElement));
    }

    /**
     * Получить ближайший по расстоянию элемент с xpathSearchElement к элементу xpathNearElement
     * @param xpathSearchElement xpath искомого element
     * @param xpathNearElement xpath близкого к искомому element элемента
     * @return искомый element
     */
    static SelenideElement findNearestElement(@Language("XPath") String xpathSearchElement, @Language("XPath") String xpathNearElement) {
            SelenideElement elementE1 = $x(xpathNearElement).shouldBe(Condition.exist);
            ElementsCollection elementsE2 = $$x(xpathSearchElement).shouldBe(CollectionCondition.sizeNotEqual(0));
            if (elementsE2.isEmpty()) {
                throw new NoSuchElementException(String.format("No elements matching %s found", xpathSearchElement));
            }
            WebElement nearestElement = null;
            double minDistance = Double.MAX_VALUE;
            for (WebElement element : elementsE2) {
                double distance = calculateDistance(element, elementE1);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestElement = element;
                }
            }
            if (nearestElement == null)
                throw new NoSuchElementException("No nearest element found");
            return (SelenideElement) nearestElement;
    }

    static double calculateDistance(WebElement element1, WebElement element2) {
        int x1 = element1.getLocation().getX();
        int y1 = element1.getLocation().getY();
        int x2 = element2.getLocation().getX();
        int y2 = element2.getLocation().getY();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    static String getIndex(int index) {
        return (index == -1) ? "last()" : String.valueOf(index);
    }

    @SneakyThrows
    //Для решения редкой проблемы динамически обновляемого контента (когда между Condition и getText() может обновиться элемент)
    static String findNotEmptyText(SelenideElement element, Duration duration) {
        Instant start = Instant.now();
        element.shouldBe(Condition.visible);
        while (duration.compareTo(Duration.between(start, Instant.now())) > 0) {
            final String s = element.getText();
            if (!s.isEmpty()) return s;
            Waiting.sleep(300);
        }
        throw new TimeoutException("Return empty string, duration: " + duration);
    }

    //TODO: До фикса доступа к балансу учеток закрываем все окна
    static void checkProject() {
//        new Alert().checkColor(Alert.Color.GREEN).checkText("Выбран контекст").close();
        Alert.closeAll();
    }

    static void open(String url) {
        Selenide.open(url);
        checkProject();
    }
}
