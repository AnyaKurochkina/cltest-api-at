package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import ui.cloud.pages.productCatalog.BaseList;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.InputFile;

import static com.codeborne.selenide.Selenide.$x;

public class GraphsListPage {
    private final SelenideElement graphsPageTitle = $x("//div[text() = 'Графы']");
    private final SelenideElement createNewGraphButton = $x("//div[@data-testid = 'graph-list-add-button']//button");
    private final SelenideElement inputTitleField = $x("//*[@name ='title']");
    private final SelenideElement inputNameField = $x("//*[@name ='name']");
    private final SelenideElement inputDescriptionField = $x("//input[@name='description']");
    private final SelenideElement inputAuthorField = $x("//*[@name ='author']");
    private final SelenideElement selectType = $x("//div[@aria-labelledby='type']");
    private final SelenideElement actionType = $x("//*[@data-value='action']");
    private final SelenideElement creatingType = $x("//*[@data-value='creating']");
    private final SelenideElement serviceType = $x("//*[@data-value='service']");
    private final SelenideElement createGraphButton = $x("//*[text()='Создать']/..");
    private final SelenideElement inputSearch = $x("//input[@placeholder = 'Поиск']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement graphId = $x("//form//p//b");
    private final SelenideElement idInput = $x("//input[@name = 'id']");
    private final SelenideElement deleteButton = $x("//span[text() = 'Удалить']");
    private final SelenideElement clearSearchButton = $x("//*[@placeholder='Поиск']/../button");
    private final SelenideElement cancelButton = $x("//span[text()='Отмена']/..");
    private final SelenideElement nothingFoundMessage = $x("//td[text()='Нет данных для отображения']");
    private final SelenideElement graphNameValidationHint = $x("//p[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement titleRequiredFieldHint = $x("//input[@name='title']/parent::div/following-sibling::p");
    private final SelenideElement nameRequiredFieldHint = $x("//input[@name='name']/parent::div/following-sibling::p");
    private final SelenideElement authorRequiredFieldHint = $x("//input[@name='author']/parent::div/following-sibling::p");
    private final SelenideElement importGraphButton = $x("//button[@title='Импортировать граф']");

    public GraphsListPage() {
        graphsPageTitle.shouldBe(Condition.visible);
    }

    public GraphsListPage createGraph(String title, String name, String type, String description, String author) {
        createNewGraphButton.click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        selectType(type);
        inputDescriptionField.setValue(description);
        inputAuthorField.setValue(author);
        createGraphButton.click();
        return this;
    }

    public GraphsListPage copyGraph(String name) {
        openActionMenu(name);
        copyAction.click();
        cancelButton.shouldBe(Condition.enabled).click();
        return this;
    }

    public GraphsListPage findGraphByName(String graphName) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(graphName);
        TestUtils.wait(1000);
        $x("//*[@value = '" + graphName + "']").shouldBe(Condition.visible);
        return new GraphsListPage();
    }

    public GraphsListPage findGraphByTitle(String title) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(title);
        TestUtils.wait(1000);
        $x("//*[@value = '" + title + "']").shouldBe(Condition.visible);
        return this;
    }

    public GraphsListPage checkGraphNotFound(String graphName) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(graphName);
        TestUtils.wait(1000);
        nothingFoundMessage.shouldBe(Condition.visible);
        return this;
    }

    public GraphsListPage deleteGraph(String name) {
        openActionMenu(name);
        deleteAction.click();
        String id = graphId.getText();
        idInput.setValue(id);
        deleteButton.click();
        return this;
    }

    private void openActionMenu(String graphName) {
        $x("//td[text() = '" + graphName + "']//parent::tr//button[@id = 'actions-menu-button']").click();
    }

    @Step("Проверка заголовков списка графов")
    public GraphsListPage checkGraphsListHeaders() {
        $x("//table[@class='MuiTable-root']//th[1]").shouldHave(Condition.exactText("Наименование"));
        $x("//table[@class='MuiTable-root']//th[2]").shouldHave(Condition.exactText("Код графа"));
        $x("//table[@class='MuiTable-root']//th[3]").shouldHave(Condition.exactText("Дата создания"));
        $x("//table[@class='MuiTable-root']//th[4]").shouldHave(Condition.exactText("Описание"));
        return this;
    }

    @Step("Проверка недоступности создания графа")
    public GraphsListPage checkCreateGraphDisabled(String title, String name, String type, String description, String author) {
        createNewGraphButton.shouldBe(Condition.visible).click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
        selectType(type);
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

    @Step("Открытие страницы графа")
    public GraphPage openGraphPage(String name) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(name);
        $x("//td[@value = '" + name + "']").shouldBe(Condition.visible).click();
        return new GraphPage();
    }

    @Step("Проверка указания некорректных значений в коде графа")
    public GraphsListPage checkGraphNameValidation(String[] names) {
        createNewGraphButton.shouldBe(Condition.visible).click();
        for (String name : names) {
            inputNameField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputNameField.setValue(name);
            TestUtils.wait(600);
            if (!graphNameValidationHint.exists()) {
                inputNameField.sendKeys("t");
            }
            graphNameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    private void selectType(String type) {
        selectType.click();
        switch (type) {
            case "creating":
                creatingType.click();
                break;
            case "action":
                actionType.click();
                break;
            case "service":
                serviceType.click();
                break;
        }
    }

    @Step("Проверка сортировки по наименованию")
    public GraphsListPage checkSortingByTitle() {
        BaseList.checkSortingByStringField("Наименование", 1);
        return this;
    }

    @Step("Проверка сортировки по коду графа")
    public GraphsListPage checkSortingByName() {
        BaseList.checkSortingByStringField("Код графа", 2);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public GraphsListPage checkSortingByCreateDate() {
        BaseList.checkSortingByDateField("Дата создания", 3);
        return this;
    }

    @Step("Импорт графа из файла")
    public GraphsListPage importGraph(String path) {
        importGraphButton.click();
        new InputFile(path).importFile();
        new Alert().checkText("Импорт выполнен успешно").checkColor(Alert.Color.GREEN).close();
        return this;
    }
}