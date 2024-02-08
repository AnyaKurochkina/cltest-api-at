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

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;

public class Select implements TypifiedElement {
    public static final String RANDOM_VALUE = "RANDOM_VALUE";
    protected final ElementsCollection options = $$x("((/html/body/div)[last()])//*[text()!='']");
    @Getter
    protected SelenideElement element;

    public Button getBtnOpen() {
        return Button.byElement(element.$x(".//*[name()='path' and starts-with(@d,'M5.226 8.56c0-.18.07-.35.21-.48.27-.24.68-.22.92.04l5.74')]/.. " +
                "| .//button[@aria-label='Open'] " +
                "| .//*[name()='path' and starts-with(@d,'M7 10l5 5 5-5z')]/../.. " +
                "| .//*[name()='path' and starts-with(@d,'M3 12a9 9 0 1 1 18 0')]/.."));
    }

    public Select(SelenideElement element) {
        this.element = element;
    }

    public static Select byLabel(String label) {
        return byLabel(label, 1);
    }

    @Step("Получение Select по label {label} с индексом {index}")
    public static Select byLabel(String label, int index) {
        return new Select($x("(//label[text()='{}']/following::div[1])" + postfix, label, TypifiedElement.getIndex(index)));
    }

    public static Select byXpath(@Language("XPath") String xpath) {
        return new Select($x(xpath));
    }

    @Step("Получение Select по name '{name}'")
    public static Select byName(String name) {
        return new Select($x("//div[select[@name='{}']]", name));
    }

    @Step("Получение Select по placeholder '{placeholder}'")
    public static Select byPlaceholder(String placeholder) {
        return new Select($x("//input[@placeholder='{}']", placeholder));
    }

    public static Select byInputName(String name) {
        return new Select($x("//div[input[@name='{}']]", name));
    }

    public Select hover() {
        element.scrollIntoView(scrollCenter);
        element.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        if (getValue().isEmpty()) {
            Waiting.sleep(2000);
        }
        return this;
    }

    @Step("Select. Очистить")
    public Select clear() {
        getClearBtn().shouldBe(Condition.visible).click();
        return this;
    }

    @Step("Select. Выбрать элемент с названием '{value}'")
    public String set(String value) {
        hover();
        Waiting.sleep(() -> !getValue().isEmpty(), Duration.ofSeconds(1));
        String currentTitle = getValue();
        if (currentTitle.equals(value))
            return value;
        getBtnOpen().click();
        if (value.equals(RANDOM_VALUE))
            setItem(getRandomIndex());
        else
            getOptions().filter(Condition.exactText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return value;
    }

    @Step("Select. Выбрать элемент с data-value '{dataValue}'")
    public String setByDataValue(String dataValue) {
        hover();
        Waiting.sleep(() -> !getValue().isEmpty(), Duration.ofSeconds(1));
        getBtnOpen().click();
        if (dataValue.equals(RANDOM_VALUE))
            setItem(getRandomIndex());
        else
            getOptions().filter(Condition.attribute("data-value", dataValue))
                    .first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return dataValue;
    }

    @Step("Select. Выбрать элемент с названием содержащим '{value}'")
    public String setContains(String value) {
        hover();
        Waiting.sleep(() -> !getValue().isEmpty(), Duration.ofSeconds(1));
        String currentTitle = getValue();
        if (currentTitle.contains(value))
            return value;
        getBtnOpen().click();
        if (value.equals(RANDOM_VALUE)) {
            setItem(getRandomIndex());
        } else
            getOptions().filter(Condition.matchText(value))
                    .first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return value;
    }

    private int getRandomIndex() {
        int size = getOptions().size();
        return (size == 0) ? -1 : new Random().nextInt(size);
    }

    @Step("Select. Выбрать элемент с названием начинающимся с '{value}'")
    public String setStart(String value) {
        hover();
        Waiting.sleep(() -> !getValue().isEmpty(), Duration.ofSeconds(1));
        String currentTitle = getValue();
        if (currentTitle.startsWith(value))
            return value;
        getBtnOpen().click();
        if (value.equals(RANDOM_VALUE))
            setItem(getRandomIndex());
        else
            getOptions().filter(Condition.matchText(value + "[^\\\\>]*")).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return value;
    }

    @Step("Select. получить текущее значение")
    public String getValue() {
        List<String> titles = element.$$x("descendant::input[@value != '']").filter(Condition.visible)
                .stream().map(SelenideElement::getValue).collect(Collectors.toList());
        if (titles.isEmpty())
            titles = element.$$x("descendant::*[text() != '']").filter(Condition.visible).texts();
        if (titles.isEmpty())
            titles.add("");
        return titles.get(titles.size() - 1);
    }

    protected SelenideElement getClearBtn() {
        return element.scrollIntoView(scrollCenter).hover().$x("descendant::button[@aria-label='Clear']");
    }

    protected String setItem(int index) {
        if(index == -1) {
            element.click();
            return getValue();
        }
        List<String> texts = getOptions().texts();
        if (getValue().equals(texts.get(index)))
            element.click();
        else
            getOptions().filter(Condition.exactText(texts.get(index))).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return texts.get(index);
    }

    public ElementsCollection getOptions() {
        return options.shouldBe(CollectionCondition.allMatch("All options visible", WebElement::isDisplayed))
                .filter(Condition.not(Condition.exactText("Не найдено")));
    }

    @Step("Select. получить текущие значения")
    public String getValues(Integer fromIdx) {
        List<String> titles = element.$$x("descendant::input[@value != '']").filter(Condition.visible)
                .stream().map(SelenideElement::getValue).collect(Collectors.toList());
        if (titles.isEmpty())
            titles = element.$$x("descendant::*[text() != '']").filter(Condition.visible).texts();
        if (titles.isEmpty())
            titles.add("");
        return String.join(", ", titles.subList(fromIdx, titles.size() - 1));
    }
}
