package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

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
        String delete = Keys.chord(Keys.CONTROL, "a") + Keys.DELETE;
        inputNameField.setValue(delete);
        inputNameField.setValue(name);
        saveButton.scrollTo().click();
        return this;
    }

    public OrgDirectionsListPage fillAndSave(String title, String name, String description) {
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        inputDescriptionField.setValue(description);
        saveButton.scrollTo().click();
        cancelButton.scrollTo().click();
        return new OrgDirectionsListPage();
    }

    public OrgDirectionPage deleteDirection() {
        deleteButton.click();
        return this;
    }

    public OrgDirectionsListPage fillIdAndDelete() {
        String dirId = id.getText();
        inputId.setValue(dirId);
        frameDeleteButton.shouldBe(Condition.enabled).click();
        return new OrgDirectionsListPage();
    }

    public OrgDirectionPage inputInvalidId(String dirId) {
        inputId.setValue(dirId);
        frameDeleteButton.shouldBe(Condition.disabled);
        inputId.clear();
        return this;
    }

    public boolean isFieldsCompare(String name, String title, String description) {
        String cloneName = name + "-clone";
        inputTitleField.shouldBe(Condition.exactValue(title));
        inputNameField.shouldBe(Condition.exactValue(cloneName));
        inputDescriptionField.shouldBe(Condition.exactValue(description));
        return Objects.requireNonNull(inputTitleField.getValue()).equals(title)
                && Objects.requireNonNull(inputNameField.getValue()).equals(cloneName)
                && Objects.requireNonNull(inputDescriptionField.getValue()).equals(description);
    }

    public boolean isNameChanged(String name) {
        return name.equals(inputNameField.getValue());
    }


}
