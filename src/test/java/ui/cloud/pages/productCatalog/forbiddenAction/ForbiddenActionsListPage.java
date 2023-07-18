package ui.cloud.pages.productCatalog.forbiddenAction;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.FileImportDialog;
import ui.elements.SearchSelect;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class ForbiddenActionsListPage extends EntityListPage {

    private final String nameColumn = "Код запрещенного действия";
    private final SearchSelect typeSelect = SearchSelect.byLabel("Тип");
    private final SearchSelect providerSelect = SearchSelect.byLabel("Провайдер");

    public ForbiddenActionsListPage() {
        $x("//div[text()='Код запрещенного действия']").shouldBe(Condition.visible);
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Step("Проверка сортировки списка запрещённых действий")
    public ForbiddenActionsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(nameColumn);
        checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка, что запрещенное действие '{forbiddenAction.name}' найдено при поиске по значению '{value}'")
    public ForbiddenActionsListPage findForbiddenActionByValue(String value, ForbiddenAction forbiddenAction) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, forbiddenAction.getName()));
        return this;
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public ForbiddenActionsListPage checkForbiddenActionNotFound(String value) {
        search(value);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Удаление запрещенного действия '{name}' из списка")
    public ForbiddenActionsListPage delete(String name) {
        EntityListPage.delete(nameColumn, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Открытие страницы запрещенного действия '{name}'")
    public ForbiddenActionPage openForbiddenActionPage(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        return new ForbiddenActionPage();
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public ForbiddenActionsListPage checkNoResultsFound(String value) {
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Проверка отображения запрещенного действия '{name}' в списке")
    public boolean isForbiddenActionDisplayed(String name) {
        Table table = new Table(nameColumn);
        if (table.isEmpty()) return false;
        if (table.isColumnValueEquals(nameColumn, name)) return true;
        while (nextPageButtonV2.getButton().isEnabled()) {
            nextPageV2();
            if (table.isColumnValueEquals(nameColumn, name)) return true;
        }
        return false;
    }

    @Step("Поиск и открытие страницы запрещенного действия '{name}'")
    public ForbiddenActionPage findAndOpenForbiddenActionPage(String name) {
        search(name);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        Waiting.sleep(1000);
        return new ForbiddenActionPage();
    }

    @Step("Открытие страницы создания запрещенного действия")
    public ForbiddenActionPage addNewForbbidenAction() {
        addNewObjectButton.click();
        return new ForbiddenActionPage();
    }

    @Step("Импорт запрещенного действия из файла '{path}'")
    public ForbiddenActionsListPage importForbiddenAction(String path) {
        importButton.click();
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }
}
