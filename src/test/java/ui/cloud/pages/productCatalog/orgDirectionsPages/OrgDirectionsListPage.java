package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Input;
import ui.elements.InputFile;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrgDirectionsListPage extends BaseListPage {

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
    private final SelenideElement actionMenu = $x(".//button[@id = 'actions-menu-button'])");


    public OrgDirectionsListPage() {
        SelenideElement directionPageTitle = $x("//div[text() = 'Направления']");
        directionPageTitle.shouldBe(Condition.visible);
    }

    public OrgDirectionPage createDirection() {
        addNewObjectButton.scrollIntoView(TypifiedElement.scrollCenter).click();
        return new OrgDirectionPage();
    }

    @Step("Поиск направления по имени")
    public OrgDirectionsListPage findDirectionByName(String dirName) {
        Input.byPlaceholder("Поиск").setValue(dirName);
        TestUtils.wait(1000);
        assertTrue(new Table("Код направления").isColumnValueEquals("Код направления", dirName));
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
        Input.byPlaceholder("Поиск").setValue(name);
        Waiting.sleep(1000);
        new Table("Код направления").getRowElementByColumnValue("Код направления", name).click();
        Waiting.sleep(1000);
        return new OrgDirectionPage();
    }

    @Step("Выбор действия 'удаление'")
    public DeleteDialog deleteActionMenu(String dirName) {
        delete("Код направления", dirName);
        return new DeleteDialog();
    }

    @Step("Выбор действия 'копирование'")
    public OrgDirectionPage copyActionMenu(String dirName) {
        copy("Код направления", dirName);
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
        importButton.scrollIntoView(TypifiedElement.scrollCenter).click();
        new InputFile(path).importFileAndSubmit();
        return this;
    }

    @Step("Проверка существования направления")
    public boolean isOrgDirectionExist(String dirName) {
        Input.byPlaceholder("Поиск").setValue(dirName);
        TestUtils.wait(1000);
        return new Table("Код направления").isColumnValueEquals("Код направления", dirName);
    }
}