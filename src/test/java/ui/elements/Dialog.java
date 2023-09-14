package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;

import static core.helper.StringUtils.$x;

@Getter
@NoArgsConstructor
public class Dialog implements TypifiedElement {
    @Language("XPath")
    protected final String xpath = "//*[self::h2 or self::h5][.='{}']/ancestor::div[@role='dialog']";
    protected SelenideElement dialog;

    public Dialog(SelenideElement dialog) {
        this.dialog = dialog;
    }

    public Dialog(String title) {
        this.dialog = $x(xpath, title);
    }

    @Step("Получение Dialog по заголовку {title}")
    public static Dialog byTitle(String title) {
        return new Dialog(title);
    }

    public Dialog setInputValue(String label, String value) {
        Input.byLabel(label).setValue(value);
        return this;
    }

    public void setInputValueV2(String label, String value) {
        Input.byLabelV2(label).setValue(value);
    }

    public void setInputByName(String name, String value) {
        Input.byName(name).setValue(value);
    }

    public String getInputValue(String label) {
        SelenideElement element = dialog.$x(String.format("descendant::div[label[starts-with(.,'%s')]]/div/input", label));
        return new Input(element).getValue();
    }

    public Dialog setSelectValue(String label, String value) {
        Select select = Select.byLabel(label);
        select.setContains(value);
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

    public void setTextareaAndPressEnter(TextArea textarea, String text) {
        textarea.setValueAndPressEnter(text);
    }

    public void setCheckBox(CheckBox checkBox, boolean checked) {
        checkBox.setChecked(checked);
    }

    public void clickButton(String text) {
        dialog.$x("descendant::button[.='" + text + "']").shouldBe(Condition.enabled).click();
        Waiting.sleep(200);
    }

}
