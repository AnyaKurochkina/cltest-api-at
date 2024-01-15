package ui.elements;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

@Getter
public class CheckBox implements TypifiedElement {
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

    public static CheckBox byId(String id) {
        return new CheckBox($x("//*[@id = '{}']", id));
    }

    public static CheckBox byXpath(@Language("XPath") String xPath) {
        return new CheckBox($x(xPath));
    }

    @Step("CheckBox. Получение значения")
    public boolean getChecked() {
        return Boolean.parseBoolean(element.$x("..//input").getAttribute("checked"));
    }

    @Step("CheckBox. Установка в положение {checked}")
    public void setChecked(boolean checked) {
        if (getChecked() != checked) {
            element.parent().shouldBe(clickableCnd).click();
        }
        Assertions.assertEquals(checked, getChecked(), String.format("Значение чекбокса не изменилось на: %s", checked));
    }
}
