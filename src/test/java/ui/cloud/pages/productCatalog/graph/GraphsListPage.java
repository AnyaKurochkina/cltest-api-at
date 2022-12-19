package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.models.Graph;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphsListPage extends BaseListPage {

    private static final String graphNameColumn = "Код графа";
    private final SelenideElement graphsPageTitle = $x("//div[text() = 'Графы']");
    private final SelenideElement createNewGraphButton = $x("//div[@data-testid = 'graph-list-add-button']//button");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputDescriptionField = $x("//input[@name='description']");
    private final SelenideElement inputAuthorField = $x("//*[@name ='author']");
    private final DropDown typeDropDown = DropDown.byLabel("Тип");
    private final SelenideElement createGraphButton = $x("//*[text()='Создать']/..");
    private final Input searchInput = Input.byPlaceholder("Поиск");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement clearSearchButton = $x("//*[@placeholder='Поиск']/../button");
    private final SelenideElement cancelButton = $x("//div[text()='Отмена']/parent::button");
    private final SelenideElement nothingFoundMessage = $x("//td[text()='Нет данных для отображения']");
    private final SelenideElement graphNameValidationHint = $x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement titleRequiredFieldHint = $x("//input[@name='title']/parent::div/following-sibling::div");
    private final SelenideElement nameRequiredFieldHint = $x("//input[@name='name']/parent::div/following-sibling::div");
    private final SelenideElement authorRequiredFieldHint = $x("//input[@name='author']/parent::div/following-sibling::div");
    private final SelenideElement sortByCreateDate = $x("//div[text()='Дата создания']");

    public GraphsListPage() {
        graphsPageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание графа '{graph.name}'")
    public GraphPage createGraph(Graph graph) {
        createNewGraphButton.click();
        inputTitleField.setValue(graph.getTitle());
        inputNameField.setValue(graph.getName());
        typeDropDown.selectByTitle(graph.getType());
        inputDescriptionField.setValue(graph.getDescription());
        inputAuthorField.setValue(graph.getAuthor());
        createGraphButton.click();
        return new GraphPage();
    }

    @Step("Копирование графа '{name}'")
    public GraphsListPage copyGraph(String name) {
        new BaseListPage().copy(graphNameColumn, name);
        new Alert().checkText("Граф успешно скопирован").checkColor(Alert.Color.GREEN).close();
        cancelButton.shouldBe(Condition.enabled).click();
        return this;
    }

    @Step("Проверка, что граф '{graph.name}' найден при поиске по значению '{value}'")
    public GraphsListPage findGraphByValue(String value, Graph graph) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        new Table(graphNameColumn).isColumnValueEquals(graphNameColumn, graph.getName());
        return this;
    }

    @Step("Проверка, что граф не найден при поиске по '{graphName}'")
    public GraphsListPage checkGraphNotFound(String graphName) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        searchInput.setValue(graphName);
        TestUtils.wait(1000);
        nothingFoundMessage.shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление графа '{name}'")
    public GraphsListPage deleteGraph(String name) {
        BaseListPage.openActionMenu(graphNameColumn, name);
        deleteAction.click();
        new DeleteDialog().inputValidIdAndDelete();
        return this;
    }

    @Step("Проверка заголовков списка графов")
    public GraphsListPage checkGraphsListHeaders() {
        Table graphsList = new Table(graphNameColumn);
        assertEquals(0, graphsList.getHeaderIndex("Наименование"));
        assertEquals(1, graphsList.getHeaderIndex("Код графа"));
        assertEquals(2, graphsList.getHeaderIndex("Дата создания"));
        assertEquals(3, graphsList.getHeaderIndex("Описание"));
        return this;
    }

    @Step("Проверка валидации некорректных параметров при создании графа")
    public GraphsListPage checkCreateGraphDisabled(String title, String name, String type, String description, String author) {
        createNewGraphButton.shouldBe(Condition.visible).click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        typeDropDown.selectByTitle(type);
        inputDescriptionField.setValue(description);
        inputAuthorField.setValue(author);
        if (title.isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (name.isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (author.isEmpty()) {
            authorRequiredFieldHint.shouldBe(Condition.visible);
        }
        createGraphButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return new GraphsListPage();
    }

    @Step("Поиск и открытие страницы графа '{name}'")
    public GraphPage findAndOpenGraphPage(String name) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        searchInput.setValue(name);
        TestUtils.wait(500);
        new Table(graphNameColumn).getRowElementByColumnValue(graphNameColumn, name).click();
        TestUtils.wait(500);
        return new GraphPage();
    }

    @Step("Открытие страницы графа '{name}'")
    public GraphPage openGraphPage(String name) {
        new Table(graphNameColumn).getRowElementByColumnValue(graphNameColumn, name).click();
        return new GraphPage();
    }

    @Step("Открытие страницы графа '{name}'")
    public GraphPage openGraphPageWithMouse(String name) {
        new Table(graphNameColumn).getRowElementByColumnValue(graphNameColumn, name);
        return new GraphPage();
    }

    @Step("Проверка валидации недопустимых значений в коде графа")
    public GraphsListPage checkGraphNameValidation(String[] names) {
        createNewGraphButton.shouldBe(Condition.visible).click();
        for (String name : names) {
            inputNameField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputNameField.setValue(name);
            TestUtils.wait(1000);
            if (!graphNameValidationHint.exists()) {
                inputNameField.sendKeys("t");
            }
            graphNameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public GraphsListPage checkSortingByTitle() {
        BaseListPage.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду графа")
    public GraphsListPage checkSortingByName() {
        BaseListPage.checkSortingByStringField("Код графа");
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public GraphsListPage checkSortingByCreateDate() {
        BaseListPage.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Сортировка по дате создания")
    public GraphsListPage sortByCreateDate() {
        sortByCreateDate.click();
        return this;
    }

    @Step("Импорт графа из файла")
    public GraphsListPage importGraph(String path) {
        importButton.click();
        new InputFile(path).importFileAndSubmit();
        new Alert().checkText("Импорт выполнен успешно").checkColor(Alert.Color.GREEN).close();
        return this;
    }

    @Step("Переход на следующую страницу списка")
    public GraphsListPage nextPage() {
        super.nextPage();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public GraphsListPage lastPage() {
        super.lastPage();
        return this;
    }

    public void checkGraphIsHighlighted(String name) {
        Table graphsList = new Table(graphNameColumn);
        Assertions.assertTrue(graphsList.getRowElementByColumnValue(graphNameColumn, name)
                .getCssValue("color").contains("196, 202, 212"));
    }
}