package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionPage extends EntityPage {
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private final SelenideElement orgDirListLink = $x("//a[text() = 'Список направлений']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final String saveOrgDirectionAlertText = "Направление успешно изменено";

    public OrgDirectionPage() {
        orgDirListLink.shouldBe(Condition.visible);
    }

    public OrgDirectionPage editNameField(String name) {
        Input.byName(NAME).setValue(name);
        saveButton.getButton().scrollIntoView(TypifiedElement.scrollCenter);
        saveButton.click();
        Alert.green(saveOrgDirectionAlertText);
        return this;
    }

    @Step("Заполнение полей title, name, description и сохранение")
    public OrgDirectionsListPage fillAndSave(String title, String name, String description) {
        Input.byName(TITLE).setValue(title);
        Input.byName(NAME).setValue(name);
        TextArea.byName(DESCRIPTION).setValue(description);
        new FileImportDialog("src/test/resources/icons/testIcon.png").importFile();
        TestUtils.wait(2000);
        TestUtils.scrollToTheBottom();
        saveButton.click();
        TestUtils.scrollToTheBottom();
        backButton.click();
        return new OrgDirectionsListPage();
    }

    @Step("Удаление направления")
    public DeleteDialog deleteDirection() {
        deleteButton.click();
        return new DeleteDialog();
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

    @Step("Возврат в список направлений")
    public OrgDirectionsListPage backToOrgDirectionsList() {
        backButton.click();
        return new OrgDirectionsListPage();
    }


    @Step("Проверка изменения имени")
    public boolean isNameChanged(String name) {
        TestUtils.wait(1000);
        return name.equals(inputNameField.getValue());
    }

    @Step("Удаление иконки")
    public OrgDirectionPage deleteIcon() {
        deleteIconButton.click();
        saveWithoutPatchVersion(saveOrgDirectionAlertText);
        addIconLabel.shouldBe(Condition.visible);
        return this;
    }
}
