package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class CheckBox implements TypifiedElement {
    @Getter
    SelenideElement element;

    public CheckBox(SelenideElement input) {
        this.element = input;
    }

    public static CheckBox byLabel(String label) {
        return byLabel(label, 1);
    }

    @Step("Получение CheckBox по label {label} с индексом {index}")
    public static CheckBox byLabel(String label, int index) {
        return new CheckBox($x("(//label[.='{}']//input[@type='checkbox'])" + postfix, label, TypifiedElement.getIndex(index)).parent());
    }

    public static CheckBox byName(String name) {
        return new CheckBox($x("//input[@type='checkbox'][@name='{}']", name).parent());
    }

    @Step("CheckBox. Получение значения")
    public boolean getChecked() {
        return element.$x("descendant::*[name()='svg']/*[name()='path' and @d='M19 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.11 0 2-.9 2-2V5c0-1.1-.89-2-2-2zm-9 14l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z'] ")
                .isDisplayed();
    }

    @Step("CheckBox. Установка в положение {checked}")
    public void setChecked(boolean checked) {
        if (getChecked() != checked)
            element.parent().shouldBe(clickableCnd).click();
        if (checked)
            element.$x("..//input").shouldBe(Condition.checked);
        else
            element.$x("..//input").shouldNotBe(Condition.checked);
    }
}
