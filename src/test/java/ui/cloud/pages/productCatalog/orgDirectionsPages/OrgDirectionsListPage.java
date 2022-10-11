package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Input;
import ui.elements.InputFile;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$x;

public class OrgDirectionsListPage {
    private final SelenideElement createButton = $x("//*[@title= 'Создать']");
    private final SelenideElement inputSearch = $x("//input[@placeholder = 'Поиск']");
    private final SelenideElement titleColumn = $x("//th[text()='Наименование']");
    private final SelenideElement nameColumn = $x("//th[text()='Код направления']");
    private final SelenideElement descriptionColumn = $x("//th[text()='Описание']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement exportAction = $x("//li[text() = 'Экспортировать']");
    private final SelenideElement id = $x("//form//p//b");
    private final SelenideElement inputId = $x("//input[@name = 'id']");
    private final SelenideElement deleteButton = $x("//button[@type ='submit']");
    private final SelenideElement noData = $x("//*[text() = 'Нет данных для отображения']");
    private final SelenideElement importDirection = $x("//*[@title = 'Импортировать направление']");


    public OrgDirectionsListPage() {
        SelenideElement directionPageTitle = $x("//div[text() = 'Направления']");
        directionPageTitle.shouldBe(Condition.visible);
    }

    public OrgDirectionPage createDirection() {
        createButton.scrollIntoView(TypifiedElement.scrollCenter);
        createButton.click();
        return new OrgDirectionPage();
    }

    @Step("Поиск направления по имени")
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

    @Step("Переход на страницу редактирования направления с именем {name}")
    public OrgDirectionPage openOrgDirectionPage(String name) {
        inputSearch.setValue(name);
        $x("//td[@value = '" + name + "']").shouldBe(Condition.visible).click();
        return new OrgDirectionPage();
    }

    @Step("Выбор действия 'удаление'")
    public OrgDirectionsListPage deleteActionMenu(String dirName) {
        $x("//td[text() = '" + dirName + "']//ancestor::tr//*[@id = 'actions-menu-button']").click();
        deleteAction.click();
        return this;
    }

    @Step("Выбор действия 'копирование'")
    public OrgDirectionPage copyActionMenu(String dirName) {
        $x("//td[text() = '" + dirName + "']//ancestor::tr//*[@id = 'actions-menu-button']").click();
        copyAction.click();
        return new OrgDirectionPage();
    }

    @Step("Ввод валидного id и удаление")
    public OrgDirectionsListPage fillIdAndDelete() {
        new Input(inputId).setValue(id.getText());
        deleteButton.scrollIntoView(true).shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Ввод невалидного id")
    public OrgDirectionsListPage inputInvalidId(String dirId) {
        new Input(inputId).setValue(dirId);
        deleteButton.shouldBe(Condition.disabled);
        inputId.clear();
        return this;
    }

    @Step("Выбор и импорт файла")
    public OrgDirectionsListPage uploadFile(String path) {
        importDirection.scrollIntoView(TypifiedElement.scrollCenter).click();
        new InputFile(path).importFile();
        return this;
    }

    @Step("Проверка существования направления")
    public boolean isOrgDirectionExist(String dirName) {
        new Input(inputSearch).setValue(dirName);
        return !noData.exists();
    }
}