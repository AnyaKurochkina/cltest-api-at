package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;

public class Menu implements TypifiedElement {
    SelenideElement element;

    public Menu(SelenideElement element) {
        this.element = element.shouldBe(activeCnd.because("Menu. Кнопка раскрытия меню не найдена"));
    }

    public static Menu byElement(SelenideElement element) {
        return new Menu(element);
    }

    public static Menu byText(String text) {
        return new Menu($x("//*[@role = 'menu']//*[text() = '{}']", text));
    }

    public void select(String item) {
        click();
        waitItem(item);
        getItem(item).hover().shouldBe(clickableCnd).click();
    }

    public Menu click() {
        element.scrollIntoView(scrollCenter).hover().shouldBe(clickableCnd).click();
        return this;
    }

    private SelenideElement getItem(String item) {
        SelenideElement element = $$x("//li[.='{}']", item)
                .shouldBe(CollectionCondition.anyMatch("Не найден пункт меню: " + item, WebElement::isDisplayed))
                .filter(Condition.visible)
                .first();
        String disabled = element.getAttribute("aria-disabled");
        if (Objects.nonNull(disabled))
            if (disabled.equals("true"))
                throw new ElementClickInterceptedException(String.format("Элемент '%s' disabled", item));
        return element;
    }

    private void waitItem(String item) {
        Waiting.find(() -> getItem(item).isDisplayed(), Duration.ofSeconds(10));
    }

    @Step("Проверка отображения пункта '{title}'")
    public boolean isItemDisplayed(String title) {
        click();
        return $$x("//li[.='{}']", title).first().isDisplayed();
    }

    @Step("Проверка отображения доступного пункта '{title}'")
    public boolean isItemDisplayedEnabled(String title) {
        click();
        return $$x("//li[.='{}']", title)
                .filter(Condition.attribute("aria-disabled", "false"))
                .first().isDisplayed();
    }

    @Step("Проверка отображения недоступного пункта '{title}'")
    public boolean isItemDisplayedDisabled(String title) {
        click();
        return $$x("//li[.='{}']", title)
                .filter(Condition.attribute("aria-disabled", "true"))
                .first().isDisplayed();
    }

    public List<String> getOptions() {
        return $$x("//*[@role = 'menu']").filter(Condition.visible).get(0).$$("li").texts();
    }
}
