package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

public class CheckBox implements TypifiedElement {
    SelenideElement input;

    public CheckBox(SelenideElement input) {
        this.input = input;
    }

    public static CheckBox byLabel(String label) {
        return byLabel(label, 1);
    }

    @Step("Получение CheckBox по label {label} с индексом {index}")
    public static CheckBox byLabel(String label, int index) {
        return new CheckBox($x("(//label[.='{}']//input[@type='checkbox'])" + postfix, label, TypifiedElement.getIndex(index)).parent());
    }

    @Step("Получение значения CheckBox")
    public boolean getChecked() {
        return input.$x("descendant::*[name()='svg']/*[name()='path' and @d='M19 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.11 0 2-.9 2-2V5c0-1.1-.89-2-2-2zm-9 14l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z'] ")
                .isDisplayed();
    }

    @Step("Установка CheckBox в положение {checked}")
    public void setChecked(boolean checked) {
        if (getChecked() != checked)
            input.parent().shouldBe(clickableCnd).click();
        Assertions.assertEquals(checked, getChecked());
    }
}
