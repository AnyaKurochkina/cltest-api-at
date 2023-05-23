package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
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

    public static Menu byElement(SelenideElement element) {
        return new Menu(element);
    }

    public void select(String item) {
        element.scrollIntoView(scrollCenter).hover().shouldBe(clickableCnd).click();
        waitItem(item);
        getItem(item).hover().shouldBe(clickableCnd).click();
    }

    private SelenideElement getItem(String item) {
        return $$x("//li[.='{}']", item)
                .shouldBe(CollectionCondition.anyMatch("Поиск элемента меню " + item, WebElement::isDisplayed))
                .filter(Condition.visible)
                .first();
    }

    private void waitItem(String item) {
        Waiting.find(() -> getItem(item).isDisplayed(), Duration.ofSeconds(10));
    }
}
