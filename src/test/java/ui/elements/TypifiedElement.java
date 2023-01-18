package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

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

    //  *[contains(text(), '₽/сут.')]    button[.='Действия']
    static SelenideElement getNearElement(@Language("XPath") String xpathSearchElement, @Language("XPath") String xpathNearElement) {
        return $x(String.format("(//%s/ancestor-or-self::*[count(.//%s) = 1])[last()]//%s", xpathNearElement, xpathSearchElement, xpathSearchElement));
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
        checkProject();
    }
}
