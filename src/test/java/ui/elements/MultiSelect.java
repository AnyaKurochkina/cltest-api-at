package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

public class MultiSelect extends Select {

    public MultiSelect(SelenideElement element) {
        super(element);
    }

    public static MultiSelect byLabel(String label) {
        return new MultiSelect(Select.byLabel(label).getElement());
    }

    @Step("MultiSelect. Выбор элемента '{value}'")
    public String set(String value) {
        hover();
        clear();
        element.click();
        if (value.equals(RANDOM_VALUE)) value = getRandomItem();
        getOptions().filter(Condition.exactText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd)
                .click();
        hideOptions();
        return value;
    }

    @Override
    @Step("MultiSelect. Выбор элемента по вхождению '{value}'")
    public String setContains(String value) {
        super.setContains(value);
        hideOptions();
        return value;
    }

    @Step("MultiSelect. Выбор элементов '{values}'")
    public String[] set(String... values) {
        hover();
        clear();
        element.click();
        for (String value : values) {
            if (value.equals(RANDOM_VALUE)) value = getRandomItem();
            getOptions().filter(Condition.exactText(value)).first().shouldBe(activeCnd).hover().shouldBe(clickableCnd)
                    .click();
        }
        hideOptions();
        return values;
    }

    @Step("MultiSelect. Скрытие выпадающего списка")
    private void hideOptions() {
        element.$x(".//*[name()='svg'][@class][not(@id)]").click();
    }

    @Step("MultiSelect. Очистка поля")
    public MultiSelect clear() {
        while (element.$x(".//button").exists()) {
            element.$x(".//button").click();
        }
        return this;
    }
}
