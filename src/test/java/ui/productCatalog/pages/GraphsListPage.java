package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class GraphsListPage {
    private final SelenideElement graphsPageTitle = $x("//div[text() = 'Графы']");
    private final SelenideElement createButton = $x("//div[@data-testid = 'graph-list-add-button']//button");
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
    private final SelenideElement actionMenuButton = $x("//*[@id='actions-menu-button']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement graphId = $x("//p/b");
    private final SelenideElement idField = $x("//*[@name = 'id']");
    private final SelenideElement deleteButton = $x("//span[text() = 'Удалить']");
    private final SelenideElement clearSearchButton = $x("//*[@placeholder='Поиск']/../button");
    private final SelenideElement cancelButton = $x("//span[text()='Отмена']/..");

    public GraphsListPage() {
        graphsPageTitle.shouldBe(Condition.visible);
    }

    public GraphsListPage createGraph(String title, String name, String type, String description, String author) {
        createButton.click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
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
        inputDescriptionField.setValue(description);
        inputAuthorField.setValue(author);
        createGraphButton.click();
        return new GraphsListPage();
    }

    public GraphsListPage copyGraph() {
        actionMenuButton.click();
        copyAction.click();
        cancelButton.shouldBe(Condition.enabled).click();
        return new GraphsListPage();
    }

    public GraphsListPage findGraphByName(String graphName) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(graphName);
        $x("//*[@value = '" + graphName + "']").shouldBe(Condition.visible);
        return new GraphsListPage();
    }

    public GraphsListPage findGraphByTitle(String title) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(title);
        $x("//*[@value = '" + title + "']").shouldBe(Condition.visible);
        return new GraphsListPage();
    }

    public void deleteGraph() {
        actionMenuButton.click();
        deleteAction.click();
        String id = graphId.getText();
        idField.setValue(id);
        deleteButton.click();
    }

    public GraphsListPage checkGraphsListHeaders() {
        $x("//table[@class='MuiTable-root']//th[1]").shouldHave(Condition.exactText("Наименование"));
        $x("//table[@class='MuiTable-root']//th[2]").shouldHave(Condition.exactText("Код графа"));
        $x("//table[@class='MuiTable-root']//th[3]").shouldHave(Condition.exactText("Дата создания"));
        $x("//table[@class='MuiTable-root']//th[4]").shouldHave(Condition.exactText("Версия"));
        $x("//table[@class='MuiTable-root']//th[5]").shouldHave(Condition.exactText("Описание"));
        return new GraphsListPage();
    }

    public GraphsListPage checkCreateGraphDisabled(String title, String name, String type, String description, String author) {
        createButton.shouldBe(Condition.visible).click();
        inputTitleField.setValue(title);
        inputNameField.setValue(name);
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
        inputDescriptionField.setValue(description);
        inputAuthorField.setValue(author);
        createGraphButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return new GraphsListPage();
    }

    public GraphPage openGraphPage(String name) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        inputSearch.setValue(name);
        $x("//td[@value = '" + name + "']").shouldBe(Condition.visible).click();
        return new GraphPage();
    }
}