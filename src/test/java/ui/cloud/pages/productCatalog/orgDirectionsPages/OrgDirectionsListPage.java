package ui.cloud.pages.productCatalog.orgDirectionsPages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.FileImportDialog;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrgDirectionsListPage extends EntityListPage {

    private final String nameColumn = "Код направления";

    public OrgDirectionsListPage() {
        SelenideElement directionPageTitle = $x("//div[text() = 'Направления'][@type]");
        directionPageTitle.shouldBe(Condition.visible);
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    public OrgDirectionPage createDirection() {
        addNewObjectButton.getButton().scrollIntoView(TypifiedElement.scrollCenter).click();
        return new OrgDirectionPage();
    }

    @Step("Поиск направления по имени")
    public OrgDirectionsListPage findDirectionByName(String dirName) {
        searchInput.setValue(dirName);
        TestUtils.wait(1000);
        assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, dirName));
        return new OrgDirectionsListPage();
    }

    @Step("Поиск направления по значению '{value}'")
    public OrgDirectionsListPage findDirectionByValue(String value, OrgDirection orgDirection) {
        searchInput.setValue(value);
        Waiting.sleep(1000);
        assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, orgDirection.getName()));
        return this;
    }

    public OrgDirectionsListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Дата изменения", "Описание", "", "");
        return this;
    }

    @Step("Переход на страницу редактирования направления с именем {name}")
    public OrgDirectionPage openOrgDirectionPage(String name) {
        searchInput.setValue(name);
        Waiting.sleep(1000);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
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

    @Step("Выбор и импорт файла")
    public OrgDirectionsListPage uploadFile(String path) {
        importButton.scrollIntoView(TypifiedElement.scrollCenter).click();
        new FileImportDialog(path).importFileAndSubmit();
        closeButton.click();
        return this;
    }

    @Step("Проверка существования направления")
    public boolean isOrgDirectionExist(String dirName) {
        searchInput.setValue(dirName);
        TestUtils.wait(1000);
        return new Table("Код направления").isColumnValueEquals("Код направления", dirName);
    }

    @Step("Проверка сортировки списка направлений")
    public OrgDirectionsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(nameColumn);
        checkSortingByDateField("Дата создания");
        return this;
    }
}