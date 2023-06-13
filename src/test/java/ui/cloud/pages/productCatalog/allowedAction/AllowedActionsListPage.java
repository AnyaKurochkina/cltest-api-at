package ui.cloud.pages.productCatalog.allowedAction;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.FileImportDialog;
import ui.elements.SearchSelect;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class AllowedActionsListPage extends BaseListPage {

    private final String nameColumn = "Код разрешенного действия";
    private final SearchSelect typeSelect = SearchSelect.byLabel("Тип");
    private final SearchSelect providerSelect = SearchSelect.byLabel("Провайдер");

    public AllowedActionsListPage() {
        $x("//div[text()='Код разрешенного действия']").shouldBe(Condition.visible);
    }

    @Step("Проверка сортировки списка разрешенных действий")
    public AllowedActionsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(nameColumn);
        checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка, что разрешенное действие '{allowedActionAction.name}' найдено при поиске по значению '{value}'")
    public AllowedActionsListPage findAllowedActionByValue(String value, AllowedAction allowedActionAction) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, allowedActionAction.getName()));
        return this;
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public AllowedActionsListPage checkAllowedActionNotFound(String value) {
        search(value);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Удаление разрешенного действия '{name}' из списка")
    public AllowedActionsListPage delete(String name) {
        BaseListPage.delete(nameColumn, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Открытие страницы разрешенного действия '{name}'")
    public AllowedActionPage openAllowedActionPage(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        return new AllowedActionPage();
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public AllowedActionsListPage checkNoResultsFound(String value) {
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Проверка отображения разрешенного действия '{name}' в списке")
    public boolean isAllowedActionDisplayed(String name) {
        Table table = new Table(nameColumn);
        if (table.isEmpty()) return false;
        if (table.isColumnValueEquals(nameColumn, name)) return true;
        while (nextPageButtonV2.getButton().isEnabled()) {
            nextPageV2();
            if (table.isColumnValueEquals(nameColumn, name)) return true;
        }
        return false;
    }

    @Step("Поиск и открытие страницы разрешенного действия '{name}'")
    public AllowedActionPage findAndOpenAllowedActionPage(String name) {
        search(name);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        Waiting.sleep(1000);
        return new AllowedActionPage();
    }

    @Step("Открытие страницы создания разрешенного действия")
    public AllowedActionPage addNewAllowedAction() {
        addNewObjectButton.click();
        return new AllowedActionPage();
    }

    @Step("Импорт разрешенного действия из файла '{path}'")
    public AllowedActionsListPage importAllowedAction(String path) {
        importButton.click();
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }
}
