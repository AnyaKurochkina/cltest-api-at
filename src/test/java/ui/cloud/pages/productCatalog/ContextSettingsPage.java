package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Input;

import static core.helper.StringUtils.$x;

@Getter
public class ContextSettingsPage {

    private final Input devProjectInput = Input.byXpath("//input[@name='dev'][@placeholder]");
    private final Input testProjectInput = Input.byXpath("//input[@name='test'][@placeholder]");
    private final Input prodProjectInput = Input.byXpath("//input[@name='prod'][@placeholder]");
    private final Button restoreDefaultProjectsButton = Button.byText("Восстановить проекты по умолчанию");
    private final Button resetEnteredDataButton = Button.byText("Сбросить введенные данные");
    private final Button saveButton = Button.byText("Сохранить");

    public ContextSettingsPage() {
        $x("//legend[text()='Задайте тип среды по умолчанию']").shouldBe(Condition.visible);
    }

    public void save() {
        saveButton.click();
        Alert.green("Настройки успешно сохранены");
    }
}
