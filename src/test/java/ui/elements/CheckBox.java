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
        return Boolean.valueOf(element.$x("..//input").getAttribute("checked"));
    }

    @Step("CheckBox. Установка в положение {checked}")
    public void setChecked(boolean checked) {
        if (getChecked() != checked)
            element.parent().shouldBe(clickableCnd).click();
        Assertions.assertEquals(getChecked(), checked);
    }
}
