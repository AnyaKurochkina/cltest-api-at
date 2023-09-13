package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteDialog extends Dialog {
    private final SelenideElement deleteButton = $x("//button[@type ='submit']");
    private final SelenideElement idNotValidMsg = $x("//*[text() = 'Идентификаторы не совпадают']");
    private final SelenideElement id = $x("//*//p//b");

    public DeleteDialog() {
        super("Удаление");
    }

    public DeleteDialog(String title) {
        super(title);
    }

    @Step("Вводим неверный id")
    public DeleteDialog inputInvalidId(String idValue) {
        setInputByName("id", idValue);
        assertTrue(idNotValidMsg.isDisplayed());
        assertFalse(deleteButton.isEnabled());
        return this;
    }

    @Step("Вводим верный id")
    public void inputValidIdAndDelete() {
        setInputByName("id", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        Alert.green("Удаление выполнено успешно");
    }

    @Step("Вводим верный id")
    public void inputValidIdAndDelete(String alertText) {
        setInputByName("id", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
    }

    @Step("Проверка недоступности удаления и текста уведомления")
    public void submitAndCheckNotDeletable(String alertText) {
        deleteButton.shouldBe(Condition.enabled).click();
        Alert.red(alertText);
    }

    @Step("Проверка недоступности удаления и текста уведомления")
    public void inputIdAndCheckNotDeletable(String alertText) {
        setInputByName("id", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        Alert.red(alertText);
    }

    @Step("Подтверждение удаления объекта")
    public void submitAndDelete(String alertText) {
        deleteButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
    }

    @Step("Подтверждение удаления объекта")
    public void submitAndDelete() {
        deleteButton.shouldBe(Condition.enabled).click();
    }

    @Step("Проверка текста предупреждения перед удалением")
    public DeleteDialog checkText(String text) {
        getDialog().$x(".//div[contains(.,'" + text + "')]").shouldBe(Condition.visible);
        return this;
    }
}
