package ui.elements;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.intellij.lang.annotations.Language;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class SearchSelect extends Select {
    String closeXpath = ".//*[@d = 'M5.226 8.56c0-.18.07-.35.21-.48.27-.24.68-.22.92.04l5.74 6.37 5.55-6.41a.65.65 0 0 1 .92-.04c.26.24.28.65.04.92l-5.99 6.9c-.28.31-.76.31-1.04 0L5.396 9a.627.627 0 0 1-.17-.44Z']/parent::*/parent::*";

    public SearchSelect(SelenideElement element) {
        super(element);
    }

    public static SearchSelect byLabel(String label) {
        return byLabel(label, 1);
    }

    public static SearchSelect byLabel(String label, int index) {
        return new SearchSelect($x("(//label[text()='{}']/following::div[1])" + postfix, label, TypifiedElement.getIndex(index)));
    }

    public static SearchSelect byXpath(@Language("XPath") String xpath) {
        return new SearchSelect($x(xpath));
    }

    public static SearchSelect byName(String name) {
        return new SearchSelect($x("//div[select[@name='{}']]", name));
    }

    public static SearchSelect byInputName(String name) {
        return new SearchSelect($x("//div[input[@name='{}']]", name));
    }

    @Step("Получение SearchSelect по placeholder '{placeholder}'")
    public static SearchSelect byPlaceholder(String placeholder) {
        return new SearchSelect($x("//div[input[@placeholder='{}']]", placeholder));
    }

    @Step("SearchSelect. Очистить")
    public SearchSelect clear() {
        if (element.$x(".//*[@id='searchSelectClearIcon']").exists()) {
            element.$x(".//*[@id='searchSelectClearIcon']").click();
        }
        return this;
    }

    @Step("SearchSelect. Закрыть")
    public SearchSelect close() {
        element.$x(closeXpath).click();
        return this;
    }

    @Step("SearchSelect. Выбрать элемент с текстом '{value}'")
    @Override
    public String set(String value) {
        hover();
        String currentTitle = getValue();
        if (currentTitle.equals(value))
            return value;
        element.click();
        clear();
        element.$x(".//input").setValue(value);
        getOptions().filter(Condition.exactText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd)
                .click();
        return value;
    }

    @Step("SearchSelect. Выбрать элемент с текстом содержащим '{value}'")
    public String setContains(String value) {
        hover();
        String currentTitle = getValue();
        if (currentTitle.contains(value))
            return value;
        element.click();
        clear();
        element.$x(".//input").setValue(value);
        getOptions().filter(Condition.matchText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd)
                .click();
        return value;
    }

    @Step("SearchSelect. Выбрать элемент с текстом начинающимся с '{value}'")
    public String setStart(String value) {
        hover();
        String currentTitle = getValue();
        if (currentTitle.startsWith(value))
            return value;
        element.click();
        clear();
        element.$x(".//input").setValue(value);
        getOptions().filter(Condition.matchText(value + "[^\\\\>]*")).first().shouldBe(activeCnd).hover()
                .shouldBe(clickableCnd).click();
        return value;
    }

    @Step("SearchSelect. Проверить, что не найдено совпадений по значению '{value}'")
    public void checkNoMatches(String value) {
        hover();
        element.click();
        clear();
        element.$x(".//input").setValue(value);
        getOptions().shouldHave(CollectionCondition.size(1)).filter(Condition.exactText("Нет совпадений"))
                .first().shouldBe(Condition.visible);
    }
}
