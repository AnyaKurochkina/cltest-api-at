package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.intellij.lang.annotations.Language;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;

@Deprecated
public class DropDown implements TypifiedElement {
    private final ElementsCollection options = $$x("//ul[@role='listbox']/li");
    @Getter
    protected SelenideElement element;

    public DropDown(SelenideElement element) {
        this.element = element;
    }

    public static DropDown byLabel(String label) {
        return byLabel(label, 1);
    }

    @Step("Получение DropDown по label {label} с индексом {index}")
    public static DropDown byLabel(String label, int index) {
        return new DropDown($x("(//label[text()='{}']/following::div[1])" + postfix, label, TypifiedElement.getIndex(index)));
    }

    public static DropDown byXpath(@Language("XPath") String xpath) {
        return new DropDown($x(xpath));
    }

    public static DropDown byInputName(String name) {
        return new DropDown($x("//div[input[@name='{}']]", name));
    }

    @Step("Выбрать в select элемент с названием '{value}'")
    public void select(String value) {
        hover();
        if (element.$x(String.format("input[starts-with(@value,'%s')]", value)).exists())
            return;
        if (element.getText().equals(value))
            return;
        element.click();
        $x("//li[starts-with(.,'{}')]", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    @Step("Выбрать в select элемент с названием '{value}'")
    public void selectByTextContains(String value) {
        hover();
        if (element.$x(String.format("input[contains(@value,'%s')]", value)).exists())
            return;
        if (Objects.nonNull(element.getValue()) && element.getValue().contains(value))
            return;
        element.click();
        $x("//li[contains(.,'{}')]", value)
                .shouldBe(Condition.enabled)
                .click();
    }

    @Step("Выбрать в select элемент со значением '{value}'")
    public void selectByValue(String value) {
        hover();
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
        hover();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//ul/li//div[@id='{}']", value)
                .shouldBe(activeCnd)
                .hover().shouldBe(clickableCnd)
                .click();
    }

    public DropDown hover() {
        if (Objects.isNull(element.getValue())) {
            Waiting.sleep(2000);
        }
        element.scrollIntoView(scrollCenter);
        element.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        return this;
    }

    @Step("Очистить select")
    public DropDown clear() {
        element.scrollIntoView(scrollCenter).hover().$x("descendant::button[@aria-label='Clear']").shouldBe(Condition.visible).click();
        return this;
    }

    @Step("Выбрать в select элемент с заголовком '{value}'")
    public void selectByTitle(String value) {
        hover();
        if (element.$x(String.format("input[@value='%s']", value)).exists())
            return;
        element.click();
        $x("//*[starts-with(@title, '{}')]", value)
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
