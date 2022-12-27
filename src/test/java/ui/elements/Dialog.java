package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static core.helper.StringUtils.$x;

@Getter
public class Dialog implements TypifiedElement {
    SelenideElement dialog;

    public Dialog(SelenideElement dialog) {
        this.dialog = dialog;
    }

    public Dialog(String title) {
        this.dialog = $x("//h2[.='{}']/ancestor::div[@role='dialog']", title);
    }

    @Step("Получение Dialog по заголовку {title}")
    public static Dialog byTitle(String title) {
        return new Dialog(title);
    }

    public Dialog setInputValue(String label, String value) {
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        new Input(element).clear();
        new Input(element).setValue(value);
        return this;
    }

    public String getInputValue(String label) {
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        return new Input(element).getValue();
    }

    public Dialog setSelectValue(String label, String value) {
        Select select = Select.byLabel(label);
        select.setStart(value);
        return this;
    }

    public Dialog setSelectValue(Select select, String value) {
        select.set(value);
        return this;
    }

    public Dialog setTextarea(TextArea textarea, String text) {
        textarea.setValue(text);
        return this;
    }

    public Dialog setCheckBox(CheckBox checkBox, boolean checked) {
        checkBox.setChecked(checked);
        return this;
    }

    public void clickButton(String btn) {
        dialog.$x("descendant::button[.='" + btn + "']").shouldBe(Condition.enabled).click();
    }

}
