package ui.cloud.tests.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.actions.ActionPage;
import ui.elements.Alert;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveDialog extends Dialog {
    private final SelenideElement saveAsNextVersionCheckBox = $x("//input[@name = 'saveAsNextVersion']");
    private final SelenideElement saveButton = $x("//div[@role='dialog']//span[text() = 'Сохранить']/parent::button");
    private final SelenideElement cancelButton = $x("//div[@role='dialog']//span[text() = 'Отмена']/parent::button");

    public SaveDialog() {
        super("Сохранить изменения?");
    }

    @Step("Вводим неверную версию")
    public ActionPage setInvalidVersion(String version, String expectedVersion) {
        saveAsNextVersionCheckBox.click();
        setInputValue("Новая версия", version);
        assertTrue($x("//p[text() = 'Версия должна быть выше, чем " + expectedVersion + "']").isDisplayed());
        assertFalse(saveButton.isEnabled());
        cancelButton.shouldBe(Condition.enabled).click();
        return new ActionPage();
    }

    @Step("Вводим валидную версию")
    public ActionPage setVersion(String version) {
        saveAsNextVersionCheckBox.click();
        setInputValue("Новая версия", version);
        saveButton.shouldBe(Condition.enabled).click();
        new Alert().checkText("Действие успешно изменено").checkColor(Alert.Color.GREEN);
        return new ActionPage();
    }

    @Step("Вводим неверный формат версии")
    public ActionPage setInvalidFormatVersion(String version) {
        saveAsNextVersionCheckBox.click();
        setInputValue("Новая версия", version);
        assertTrue($x("//p[text() = 'Некорректный формат номера версии']").isDisplayed());
        assertFalse(saveButton.isEnabled());
        cancelButton.shouldBe(Condition.enabled).click();
        return new ActionPage();
    }

    @Step("Сохраняем действие")
    public ActionPage saveAsNextVersion() {
        saveButton.shouldBe(Condition.enabled).click();
        new Alert().checkText("Действие успешно изменено").checkColor(Alert.Color.GREEN);
        return new ActionPage();
    }
}
