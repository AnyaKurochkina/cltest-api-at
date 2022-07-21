package ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveDialog extends Dialog{
    private final SelenideElement saveAsNextVersionCheckBox = $x("//input[@name = 'saveAsNextVersion']");
    private final SelenideElement saveButton = $x("//span[text() = 'Сохранить']/parent::button");
    private final SelenideElement cancelButton = $x("//span[text() = 'Отмена']/parent::button");

    public SaveDialog() {
        super("Сохранить изменения?");
    }

    @Step("Вводим неверную версию")
    public SaveDialog setInvalidVersion(String version, String expectedVersion) {
        saveAsNextVersionCheckBox.click();
        setInputValue("Новая версия", version);
        assertTrue($x("//p[text() = 'Версия должна быть выше, чем " + expectedVersion + "']").isDisplayed());
        assertFalse(saveButton.isEnabled());
        cancelButton.shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Вводим валидную версию")
    public void setVersion(String version) {
        saveAsNextVersionCheckBox.click();
        setInputValue("Новая версия", version);
        saveButton.shouldBe(Condition.enabled).click();
        new Alert().checkText("Действие успешно изменено").checkColor(Alert.Color.GREEN);
    }
}
