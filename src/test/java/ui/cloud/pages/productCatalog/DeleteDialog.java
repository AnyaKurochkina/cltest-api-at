package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteDialog extends Dialog {
    private final SelenideElement deleteButton = $x("//button[@type ='submit']");
    private final SelenideElement idNotValidMsg = $x("//p[text() = 'Идентификаторы не совпадают']");
    private final SelenideElement id = $x("//form//p//b");

    public DeleteDialog() {
        super("Удаление");
    }

    @Step("Вводим неверный id")
    public DeleteDialog inputInvalidId(String idValue) {
        setInputValue("Идентификатор", idValue);
        assertTrue(idNotValidMsg.isDisplayed());
        assertFalse(deleteButton.isEnabled());
        return this;
    }

    @Step("Вводим верный id")
    public void inputValidIdAndDelete() {
        setInputValue("Идентификатор", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        new Alert().checkText("Удаление выполнено успешно").checkColor(Alert.Color.GREEN);
    }

    @Step("Вводим верный id")
    public void inputValidIdAndDelete(String alertText) {
        setInputValue("Идентификатор", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        new Alert().checkText(alertText).checkColor(Alert.Color.GREEN);
    }

    @Step("Вводим верный id")
    public void inputValidIdAndDeleteNotAvailable(String alertText) {
        setInputValue("Идентификатор", id.getText());
        deleteButton.shouldBe(Condition.enabled).click();
        new Alert().checkText(alertText);
        TestUtils.wait(6000);
    }

    @Step("Подтверждение удаления объекта")
    public void submitAndDelete(String alertText) {
        deleteButton.shouldBe(Condition.enabled).click();
        new Alert().checkText(alertText).checkColor(Alert.Color.GREEN);
    }
}
