package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.graph.Graph;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class GraphsListPage extends EntityListPage {

    private static final String nameColumn = "Код графа";
    private final SelenideElement graphsPageTitle = $x("//div[text() = 'Графы'][@type]");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionTextArea = TextArea.byName("description");
    private final SelenideElement inputAuthorField = $x("//*[@name ='author']");
    private final RadioGroup typeRadioGroup = RadioGroup.byFieldsetLabel("Тип");
    private final SelenideElement clearSearchButton = $x("//*[@placeholder='Поиск']/../button");
    private final SelenideElement graphNameValidationHint = $x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement titleRequiredFieldHint = $x("//input[@name='title']/parent::div/following-sibling::div");
    private final SelenideElement nameRequiredFieldHint = $x("//input[@name='name']/parent::div/following-sibling::div");
    private final SelenideElement authorRequiredFieldHint = $x("//input[@name='author']/parent::div/following-sibling::div");
    private final SelenideElement sortByCreateDate = $x("//div[text()='Дата создания']");
    private final Button usageLinkButton = Button.byText("Перейти в использование");

    public GraphsListPage() {
        graphsPageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание графа '{graph.name}'")
    public GraphPage createGraph(Graph graph) {
        addNewObjectButton.click();
        new GraphPage().setAttributes(graph).getSaveButton().click();
        return new GraphPage();
    }

    @Step("Копирование графа '{name}'")
    public GraphsListPage copyGraph(String name) {
        copy(nameColumn, name);
        Alert.green("Копирование выполнено успешно");
        backButton.click();
        return this;
    }

    @Step("Проверка, что граф '{graph.name}' найден при поиске по значению '{value}'")
    public GraphsListPage checkGraphFoundByValue(String value, Graph graph) {
        search(value);
        assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, graph.getName()), "Граф найден");
        return this;
    }

    @Step("Проверка, что граф не найден при поиске по '{graphName}'")
    public GraphsListPage checkGraphNotFound(String graphName) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        searchInput.setValue(graphName);
        Waiting.sleep(1000);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Удаление графа '{name}'")
    public GraphsListPage deleteGraph(String name) {
        delete(nameColumn, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Проверка недоступности удаления используемого графа")
    public GraphPage checkDeleteUsedGraphUnavailable(Graph graph) {
        delete(nameColumn, graph.getName());
        new DeleteDialog().submitAndCheckNotDeletable("Нельзя удалить граф, который используется другими" +
                " объектами. Отвяжите граф от объектов и повторите попытку");
        usageLinkButton.click();
        new GraphPage().checkTabIsSelected("Использование");
        return new GraphPage();
    }

    @Step("Проверка заголовков списка графов")
    public GraphsListPage checkGraphsListHeaders() {
        AssertUtils.assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Дата изменения", "Описание", "Теги", "", "");
        return this;
    }

    @Step("Проверка валидации некорректных параметров при создании графа")
    public GraphsListPage checkCreateGraphDisabled(String title, String name, String type, String description, String author) {
        addNewObjectButton.click();
        inputTitleField.setValue(title);
        nameInput.setValue(name);
        typeRadioGroup.select(type);
        descriptionTextArea.setValue(description);
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
        saveButton.getButton().shouldBe(Condition.disabled);
        backButton.click();
        return new GraphsListPage();
    }

    @Step("Поиск и открытие страницы графа '{name}'")
    public GraphPage findAndOpenGraphPage(String name) {
        search(name);
        DataTable table = new DataTable(nameColumn);
        new DataTable(nameColumn).searchAllPages(t -> table.isColumnValueContains(nameColumn, name))
                .getRowByColumnValueContains(nameColumn, name).get().click();
        Waiting.sleep(1000);
        return new GraphPage();
    }

    @Step("Открытие страницы графа '{name}'")
    public GraphPage openGraphPage(String name) {
        DataTable table = new DataTable(nameColumn);
        new DataTable(nameColumn).searchAllPages(t -> table.isColumnValueContains(nameColumn, name))
                .getRowByColumnValueContains(nameColumn, name).get().click();
        return new GraphPage();
    }

    @Step("Проверка валидации недопустимых значений в коде графа")
    public GraphsListPage checkGraphNameValidation(String[] names) {
        addNewObjectButton.click();
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(graphNameValidationHint::isDisplayed,
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            graphNameValidationHint.shouldBe(Condition.visible);
        }
        backButton.click();
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public GraphsListPage checkSortingByTitle() {
        checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду графа")
    public GraphsListPage checkSortingByName() {
        checkSortingByStringField("Код графа");
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public GraphsListPage checkSortingByCreateDate() {
        EntityListPage.checkSortingByDateField("Дата создания");
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
        new FileImportDialog(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }

    @Step("Переход на следующую страницу списка")
    public GraphsListPage nextPage() {
        super.nextPage();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public GraphsListPage lastPage() {
        super.lastPageV2();
        return this;
    }

    @Step("Проверка, что подсвечен граф '{name}'")
    public void checkGraphIsHighlighted(String name) {
        checkRowIsHighlighted(nameColumn, name);
    }
}