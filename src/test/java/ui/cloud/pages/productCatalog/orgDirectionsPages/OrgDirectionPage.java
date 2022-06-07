package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Input;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionPage {
    private final SelenideElement orgDirListLink = $x("//a[text() = 'Список направлений']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final SelenideElement saveButton = $x("//button/span[text() = 'Сохранить']");
    private final SelenideElement cancelButton = $x("//button/span[text() = 'Отмена']");
    private final SelenideElement deleteButton = $x("//span[text() ='Удалить']");
    private final SelenideElement id = $x("//p/b");
    private final SelenideElement inputId = $x("//input[@name = 'id']");
    private final SelenideElement frameDeleteButton = $x("//button[@type ='submit']");

    public OrgDirectionPage() {
        orgDirListLink.shouldBe(Condition.visible);
    }

    public OrgDirectionPage editNameField(String name) {
        new Input(inputNameField).setValue(name);
        TestUtils.scrollToTheBottom();
        saveButton.click();
        return this;
    }
    @Step("Запонение полей title, name, description и сохранение")
    public OrgDirectionsListPage fillAndSave(String title, String name, String description) {
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        inputDescriptionField.setValue(description);
        TestUtils.scrollToTheBottom();
        saveButton.click();
        TestUtils.scrollToTheBottom();
        cancelButton.click();
        return new OrgDirectionsListPage();
    }
    @Step("Удаление направления")
    public OrgDirectionPage deleteDirection() {
        deleteButton.click();
        return this;
    }
    @Step("Ввод id и удаление")
    public OrgDirectionsListPage fillIdAndDelete() {
        new Input(inputId).setValue(id.getText());
        frameDeleteButton.shouldBe(Condition.enabled).click();
        return new OrgDirectionsListPage();
    }
    @Step("Ввод невалидного id")
    public OrgDirectionPage inputInvalidId(String dirId) {
        new Input(inputId).setValue(dirId);
        frameDeleteButton.shouldBe(Condition.disabled);
        inputId.clear();
        return this;
    }
    @Step("Проверка значений полей")
    public boolean isFieldsCompare(String name, String title, String description) {
        String cloneName = name + "-clone";
        inputTitleField.shouldBe(Condition.exactValue(title));
        inputNameField.shouldBe(Condition.exactValue(cloneName));
        inputDescriptionField.shouldBe(Condition.exactValue(description));
        return Objects.requireNonNull(inputTitleField.getValue()).equals(title)
                && Objects.requireNonNull(inputNameField.getValue()).equals(cloneName)
                && Objects.requireNonNull(inputDescriptionField.getValue()).equals(description);
    }
    @Step("Проверка изменения имени")
    public boolean isNameChanged(String name) {
        return name.equals(inputNameField.getValue());
    }


}
