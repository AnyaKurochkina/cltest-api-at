package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.CheckBox;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selenide.actions;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableSettingsDialog extends Dialog {
    SelenideElement hintText = $x("//*[text() = 'Выберите необходимые колонки в левом поле и настройте нужную вам последовательность в правой части.']");
    CheckBox expander = CheckBox.byLabel("Название");
    Button saveSettings = Button.byText("Сохранить");

    public TableSettingsDialog(String title) {
        super(title);
        hintText.shouldBe(Condition.visible);
    }
    @Step("Удаляем колонку {columnName}")
    public TableSettingsDialog removeColumn(String columnName) {
        if (columnName.equals("Название")) {
            assertTrue(expander.getChecked());
            assertFalse(expander.getElement().is(Condition.disabled));
            Button.byElement($x("//span[text() = '{}']/parent::div/following-sibling::button", columnName)).click();
            Alert.red(String.format("Нельзя удалить колонку \"%s\"", columnName));
            return this;
        }
        CheckBox.byLabel(columnName).setChecked(false);
        assertFalse($x("//div/span[text() = '{}']", columnName).isDisplayed());
        return this;
    }

    @Step("Сохраняем настройки")
    public OrgStructurePage saveSettings() {
        saveSettings.click();
        Alert.green("Таблица успешно изменена");
        return new OrgStructurePage();
    }

    @Step("Передвигаем колонку {sourceColumn}, на координаты {x}, {y}")
    public TableSettingsDialog moveColumnTo(String sourceColumn, Integer x, Integer y) {
        SelenideElement e = $x("//div/span[text() = '{}']", sourceColumn);
        actions().clickAndHold(e).moveToElement(e, x, y).release(e).build().perform();
        Waiting.sleep(1000);
        return  this;
    }
}
