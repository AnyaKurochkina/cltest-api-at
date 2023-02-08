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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$$x;
import static core.helper.StringUtils.$x;

public class Select implements TypifiedElement {
    public static final String RANDOM_VALUE = "RANDOM_VALUE";
    protected final ElementsCollection options = $$x("((/html/body/div)[last()])//*[text()!='Не найдено']");
    @Getter
    protected SelenideElement element;

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

    public static Select byInputName(String name) {
        return new Select($x("//div[input[@name='{}']]", name));
    }

    public Select hover() {
        element.scrollIntoView(scrollCenter);
        element.shouldBe(activeCnd).hover().shouldBe(clickableCnd);
        if (getValue().equals("")) {
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
        String currentTitle = getValue();
        if (currentTitle.equals(value) || (value.equals(RANDOM_VALUE) && !currentTitle.equals("")))
            return value;
        element.click();
        if (value.equals(RANDOM_VALUE))
            value = getRandomItem();
        getOptions().filter(Condition.exactText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return value;
    }

    @Step("Select. Выбрать элемент с названием содержащим '{value}'")
    public String setContains(String value) {
        hover();
        String currentTitle = getValue();
        if (currentTitle.contains(value) || (value.equals(RANDOM_VALUE) && !currentTitle.equals("")))
            return value;
        element.click();
        if (value.equals(RANDOM_VALUE))
            value = getRandomItem();
        getOptions().filter(Condition.matchText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return value;
    }

    @Step("Select. Выбрать элемент с названием начинающимся с '{value}'")
    public String setStart(String value) {
        hover();
        String currentTitle = getValue();
        if (currentTitle.startsWith(value) || (value.equals(RANDOM_VALUE) && !currentTitle.equals("")))
            return value;
        element.click();
        if (value.equals(RANDOM_VALUE))
            value = getRandomItem();
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

    private String random(List<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()) % list.size());
    }

    protected SelenideElement getClearBtn() {
        return element.scrollIntoView(scrollCenter).hover().$x("descendant::button[@aria-label='Clear']");
    }

    protected String getRandomItem() {
        return random(getOptions().texts());
    }

    public ElementsCollection getOptions() {
        return options.shouldBe(CollectionCondition.allMatch("All options visible", WebElement::isDisplayed));
    }
}
