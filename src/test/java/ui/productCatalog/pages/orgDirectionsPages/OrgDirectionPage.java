package ui.productCatalog.pages.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionPage {
    private final SelenideElement orgDirListLink = $x("//a[text() = 'Список направлений']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final SelenideElement saveButton = $x("//button/span[text() = 'Сохранить']");
    private final SelenideElement cancelButton = $x("//button/span[text() = 'Отмена']");

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

    public boolean isNameChanged(String name) {
        return name.equals(inputNameField.getValue());
    }


}
