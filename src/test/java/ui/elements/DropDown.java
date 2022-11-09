package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

public class DropDown implements TypifiedElement {
    @Getter
    protected SelenideElement element;
    private final ElementsCollection options = $$x("//ul[@role='listbox']/li");

    public DropDown(SelenideElement element) {
        this.element = element;
    }

    public static DropDown byLabel(String name) {
        return new DropDown($x("//label[text()='{}']/following::div[1]", name));
    }
    public static DropDown byXpath(String xpath) {
        return new DropDown($x(xpath));
    }

    public static DropDown byInputName(String name) {
        return new DropDown($x("//div[input[@name='{}']]", name));
    }

    @Step("Выбрать в select элемент с названием '{value}'")
    public void select(String value) {
        click();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        if (element.getText().equals(value))
            return;
        element.shouldBe(clickableCnd).click();
        $x("//li[text()='{}']", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    @Step("Выбрать в select элемент с текстом '{value}'")
    public void selectByDivText(String value) {
        element.shouldBe(clickableCnd).click();
        $x("//li/div[text()='{}']", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    @Step("Выбрать в select элемент со значением '{value}'")
    public void selectByValue(String value) {
        click();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//ul/li[@data-value='{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    @Step("Выбрать в select элемент с ID равным '{value}'")
    public void selectById(String value) {
        click();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//ul/li//div[@id='{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    public DropDown click() {
        element.scrollIntoView(scrollCenter);
        element.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        return this;
    }

    @Step("Очистить select")
    public DropDown clear() {
        element.scrollIntoView(scrollCenter).$x("descendant::button[@aria-label='Clear']").hover().click();
        return this;
    }

    @Step("Выбрать в select элемент с заголовком '{value}'")
    public void selectByTitle(String value) {
        click();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//*[@title = '{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    public String getValue() {
        return element.$x("input").getValue();
    }

    private boolean isDataValue() {
        return options.shouldBe(CollectionCondition.sizeNotEqual(0)).first().getAttribute("data-value") != null;
    }

    /**
     * Получить все названия элементов списка
     * (пердварительно нужно его раскрыть)
     */
    public List<String> getOptionNames() {
        return options.shouldBe(CollectionCondition.allMatch("All options visible", WebElement::isDisplayed)).texts();
    }

    /**
     * Получить все значения выпадающего списка
     * (пердварительно нужно его раскрыть)
     */
    public List<String> getOptionValues() {
        if (!isDataValue()) {
            List<String> list = getOptionNames();
            list.add(0, getValue());
            return list;
        }
        return getOptionNames();
    }
}
