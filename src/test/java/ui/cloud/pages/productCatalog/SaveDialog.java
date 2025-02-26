package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveDialog extends Dialog {
    private final SelenideElement saveButton = $x("//div[@role='dialog']//div[text() = 'Сохранить']/parent::button");
    private final SelenideElement cancelButton = $x("//div[@role='dialog']//div[text() = 'Отмена']/parent::button");
    private final Input newVersionInput = Input.byName("newVersion");

    public SaveDialog() {
        super("Сохранить изменения?");
    }

    @Step("Проверка невозможности сохранения с некорректной версией '{newVersion}'")
    public void checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        setInputByName("newVersion", newVersion);
        assertTrue($x("//*[text() = 'Версия должна быть выше, чем " + currentVersion + "']").isDisplayed());
        assertFalse(saveButton.isEnabled());
        cancelButton.shouldBe(Condition.enabled).click();
    }

    @Step("Сохранение с указанной версией '{newVersion}'")
    public void saveWithVersion(String newVersion, String alertText) {
        setInputByName("newVersion", newVersion);
        saveButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
    }

    @Step("Проверка невозможности сохранения с версией некорректного формата '{newVersion}'")
    public void checkSaveWithInvalidVersionFormat(String newVersion) {
        setInputByName("newVersion", newVersion);
        assertTrue($x("//*[text() = 'Некорректный формат номера версии']").isDisplayed());
        assertFalse(saveButton.isEnabled());
        cancelButton.shouldBe(Condition.enabled).click();
    }

    @Step("Сохранение со следующей патч версией")
    public void saveWithNextPatchVersion(String alertText) {
        saveButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
    }

    @Step("Проверка предлагаемой для сохранения версии и сохранение")
    public void checkNextVersionAndSave(String nextVersion, String alertText) {
        newVersionInput.getInput().shouldHave(Condition.exactValue(nextVersion));
        saveButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
    }
}
