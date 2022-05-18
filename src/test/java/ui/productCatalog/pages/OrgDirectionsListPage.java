package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionsListPage {
    private final SelenideElement directionPageTitle = $x("//div[text() = 'Направления']");
    private final SelenideElement createButton = $x("//button[@class = 'sc-halPKt jikLRn']");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputDescriptionField = $x("//textarea[@name ='description']");
    private final SelenideElement saveButton = $x("//button/span[text() = 'Сохранить']");
    private final SelenideElement inputSearch = $x("//input[@placeholder = 'Поиск']");
    private final SelenideElement deleteButton = $x("//button[@data-appearance='primary']");
    private final SelenideElement titleColumn = $x("//th[text()='Наименование']");
    private final SelenideElement nameColumn = $x("//th[text()='Код направления']");
    private final SelenideElement descriptionColumn = $x("//th[text()='Описание']");


    public OrgDirectionsListPage() {
        directionPageTitle.shouldBe(Condition.visible);
    }

    public OrgDirectionsListPage createDirection(String title, String name, String description) {
        createButton.click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        inputDescriptionField.setValue(description);
        saveButton.click();
        return new OrgDirectionsListPage();
    }

    public OrgDirectionsListPage findDirectionByName(String dirName) {

//        if (!(inputSearch.getAttribute("value")).isEmpty()) {
//            searchClear.click();
//        }
        inputSearch.setValue(dirName);
        $x("//td[@value = '" + dirName + "']").shouldBe(Condition.visible);
        return new OrgDirectionsListPage();
    }

    public OrgDirectionsListPage checkFields() {
        titleColumn.shouldBe(Condition.visible);
        nameColumn.shouldBe(Condition.visible);
        descriptionColumn.shouldBe(Condition.visible);
        return new OrgDirectionsListPage();
    }
}