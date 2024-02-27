package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.action.Action;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class ActionsListPage extends EntityListPage {
    public static final String ACTION_NAME_COLUMN = "Код действия";
    private final SearchSelect typeSelect = SearchSelect.byLabel("Тип");
    private final SearchSelect providerSelect = SearchSelect.byLabel("Провайдер");

    public ActionsListPage() {
        SelenideElement actionPageTitle = $x("//div[text() = 'Действия'][@type]");
        actionPageTitle.shouldBe(Condition.visible);
    }

    @Step("Открытие диалога создания действия")
    public ActionPage openAddActionDialog() {
        TestUtils.scrollToTheTop();
        addNewObjectButton.click();
        return new ActionPage();
    }

    /**
     * Метод для проверки существования действия по колонке "Код действия"
     *
     * @param name значение для поиска в колонке "Код действия"
     * @return true если действие существует, false если действие не существует.
     */
    @Step("Проверка отображения действия '{name}' в списке")
    public boolean isActionDisplayed(String name) {
        Table table = new Table(ACTION_NAME_COLUMN);
        if (table.isColumnValueEquals(ACTION_NAME_COLUMN, name)) return true;
        while (nextPageButtonV2.getButton().isEnabled()) {
            nextPageV2();
            if (table.isColumnValueEquals(ACTION_NAME_COLUMN, name)) return true;
        }
        return false;
    }

    @Step("Копирование действия {name}")
    public ActionPage copyAction(String name) {
        copy(ACTION_NAME_COLUMN, name);
        Alert.green("Копирование выполнено успешно");
        return new ActionPage();
    }

    @Step("Удаление действия {name}")
    public DeleteDialog deleteAction(String name) {
        delete(ACTION_NAME_COLUMN, name);
        return new DeleteDialog();
    }

    @Step("Открытие страницы действия {name}")
    public ActionPage openActionPage(String name) {
        new Table(ACTION_NAME_COLUMN).getRowByColumnValue(ACTION_NAME_COLUMN, name).get().click();
        Waiting.sleep(2000);
        return new ActionPage();
    }

    @Step("Открытие формы действия по строке {number}")
    public ActionPage openActionFormByRowNumber(int number) {
        new Table(ACTION_NAME_COLUMN).getRow(number).get().click();
        Waiting.sleep(2000);
        return new ActionPage();
    }

    @Step("Импорт действия из файла")
    public ActionsListPage importAction(String path) {
        importButton.scrollIntoView(TypifiedElement.scrollCenter).click();
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }

    @Step("Переход на следующую страницу списка действий")
    public ActionsListPage goToNextPageActionList() {
        String firstActionName = new Table("Наименование").getFirstValueByColumn("Наименование");
        nextPageV2();
        String secondActionName = new Table("Наименование").getFirstValueByColumn("Наименование");
        assertNotEquals(firstActionName, secondActionName);
        return this;
    }

    @Step("Проверяем, что строка отличается визуально")
    public ActionsListPage checkActionIsHighlighted(int rowNumber) {
        Table actionList = new Table(ACTION_NAME_COLUMN);
        Assertions.assertTrue(actionList.getRow(rowNumber).get().$x("td")
                .getCssValue("color").contains("176, 181, 189"));
        return this;
    }

    @Step("Проверка заголовков списка действий")
    public ActionsListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(ACTION_NAME_COLUMN),
                "Наименование", ACTION_NAME_COLUMN, "Дата создания", "Дата изменения", "Тип", "Провайдер", "Теги",
                "Открыто/Закрыто", "", "");
        return this;
    }

    @Step("Проверка сортировки списка действий")
    public ActionsListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(ACTION_NAME_COLUMN);
        checkSortingByDateField("Дата создания");
        checkSortingByDateField("Дата изменения");
        return this;
    }

    @Step("Поиск действия по значению '{value}'")
    public ActionsListPage findActionByValue(String value, Action action) {
        searchInput.setValue(value);
        Waiting.sleep(1000);
        assertTrue(new Table(ACTION_NAME_COLUMN).isColumnValueEquals(ACTION_NAME_COLUMN, action.getName()));
        return this;
    }

    @Step("Проверка, что действия не найдены при поиске по '{value}'")
    public ActionsListPage checkActionNotFound(String value) {
        search(value);
        assertTrue(new Table(ACTION_NAME_COLUMN).isEmpty());
        return this;
    }

    @Step("Поиск и открытие страницы действия '{name}'")
    public ActionPage findAndOpenActionPage(String name) {
        search(name);
        DataTable table = new DataTable(ACTION_NAME_COLUMN);
        new DataTable(ACTION_NAME_COLUMN).searchAllPages(t -> table.isColumnValueContains(ACTION_NAME_COLUMN, name))
                .getRowByColumnValueContains(ACTION_NAME_COLUMN, name).get().click();
        Waiting.sleep(1000);
        return new ActionPage();
    }
}
