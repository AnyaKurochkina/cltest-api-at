package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$$x;

public class Menu implements TypifiedElement {
    SelenideElement element;

    private Menu(SelenideElement element) {
        this.element = element.shouldBe(activeCnd);
    }

    public static Menu byElement(SelenideElement element){
        return new Menu(element);
    }

    public void select(String item) {
        element.scrollIntoView(scrollCenter).hover().shouldBe(clickableCnd).shouldBe(new ClickAndRunMenu(item), Duration.ofSeconds(10));
        getItem(item).hover().shouldBe(clickableCnd).click();
    }

    private SelenideElement getItem(String item) {
        return $$x("//ul//li[.='{}']", item).filter(Condition.visible).first();
    }

    private class ClickAndRunMenu extends Condition {
        private final String item;
        private boolean isClicked;

        public ClickAndRunMenu(String item) {
            super("Ожидание отображения меню");
            this.item = item;
        }

        @Override
        public boolean apply(@NotNull Driver driver, @NotNull WebElement webElement) {
            if (!isClicked && webElement.isDisplayed() && webElement.isEnabled()) {
                webElement.click();
                isClicked = true;
            }
            return getItem(item).isDisplayed();
        }
    }
}
