package ui.elements;

import com.codeborne.selenide.*;
import core.utils.Waiting;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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
        Selenide.executeJavaScript(Alert.script);
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
    static WebElement findNearestElement(@Language("XPath") String xpathSearchElement, @Language("XPath") String xpathNearElement) {
            SelenideElement elementE1 = $x(xpathNearElement);
            ElementsCollection elementsE2 = $$x(xpathSearchElement).shouldBe(CollectionCondition.sizeNotEqual(0));
            if (elementsE2.isEmpty()) {
                throw new NoSuchElementException("No elements matching xpathSearchElement found");
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
            return nearestElement;
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

    //TODO: До фикса доступа к балансу учеток закрываем все окна
    static void checkProject() {
//        new Alert().checkColor(Alert.Color.GREEN).checkText("Выбран контекст").close();
        Alert.closeAll();
    }

    static void open(String url) {
        Selenide.open(url);
        Selenide.executeJavaScript(Alert.script);
        checkProject();
    }
}
