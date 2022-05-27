package ui.productCatalog.pages.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionsListPage {
    private final SelenideElement directionPageTitle = $x("//div[text() = 'Направления']");
    private final SelenideElement createButton = $x("//*[@title= 'Создать']");
    private final SelenideElement inputSearch = $x("//input[@placeholder = 'Поиск']");
    private final SelenideElement titleColumn = $x("//th[text()='Наименование']");
    private final SelenideElement nameColumn = $x("//th[text()='Код направления']");
    private final SelenideElement descriptionColumn = $x("//th[text()='Описание']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement id = $x("//p/b");
    private final SelenideElement inputId = $x("//input[@name = 'id']");
    private final SelenideElement deleteButton = $x("//button[@type ='submit']");
    private final SelenideElement noData = $x("//*[text() = 'Нет данных для отображения']");

    public OrgDirectionsListPage() {
        directionPageTitle.shouldBe(Condition.visible);
    }

    public OrgDirectionPage createDirection() {
        createButton.click();
        return new OrgDirectionPage();
    }

    public OrgDirectionsListPage findDirectionByName(String dirName) {
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

    public OrgDirectionPage openOrgDirectionPage(String name) {
        inputSearch.setValue(name);
        $x("//td[@value = '" + name + "']").shouldBe(Condition.visible).click();
        return new OrgDirectionPage();
    }

    public OrgDirectionsListPage clickActionMenu(String dirName) {
        $x("//td[text() = '" + dirName + "']//ancestor::tr//*[@id = 'actions-menu-button']").click();
        return this;
    }

    public OrgDirectionsListPage deleteActionMenu() {
        deleteAction.click();
        return this;
    }

    public OrgDirectionPage copyActionMenu() {
        copyAction.click();
        return new OrgDirectionPage();
    }

    public OrgDirectionsListPage fillIdAndDelete() {
        String dirId = id.getText();
        inputId.setValue(dirId);
        deleteButton.shouldBe(Condition.enabled).click();
        return this;
    }

    public OrgDirectionsListPage inputInvalidId(String dirId) {
        inputId.setValue(dirId);
        deleteButton.shouldBe(Condition.disabled);
        inputId.clear();
        return this;
    }

    public boolean isNotExist(String dirName) {
        inputSearch.setValue(dirName);
        return noData.exists();
    }
}