package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;
import ui.elements.TextArea;

import static com.codeborne.selenide.Selenide.$x;

public class GraphPage extends BasePage {
    protected static final String saveGraphAlertText = "Граф успешно сохранен";
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final SelenideElement graphVersion = $x("//div[@aria-labelledby='version']");
    private final TextArea descriptionTextArea = TextArea.byName("description");
    private final Input nameInput = Input.byName("name");
    private final SelenideElement graphTitleInput = $x("//input[@name='title']");
    private final Input authorInput = Input.byName("author");
    private final SelenideElement usageLink = $x("//a[text()='Перейти в Использование']");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов графа '{graph.name}'")
    public GraphPage checkAttributes(Graph graph) {
        nameInput.getInput().shouldHave(Condition.exactValue(graph.getName()));
        graphTitleInput.shouldHave(Condition.exactValue(graph.getTitle()));
        checkGraphVersion(graph.getVersion());
        descriptionTextArea.getTextArea().shouldHave(Condition.exactText(graph.getDescription()));
        authorInput.getInput().shouldHave(Condition.exactValue(graph.getAuthor()));
        return new GraphPage();
    }

    @Step("Проверка, что отображаемая версия графа равна '{version}'")
    public GraphPage checkGraphVersion(String version) {
        graphVersion.shouldBe(Condition.visible).shouldHave(Condition.exactText(version));
        return new GraphPage();
    }

    @Step("Выбор версии графа '{version}'")
    public GraphPage selectGraphVersion(String version) {
        DropDown graphVersion = DropDown.byLabel("Выберите версию");
        graphVersion.selectByValue(version);
        return this;
    }

    @Step("Редактирование графа '{graph.name}'")
    public GraphPage setAttributes(Graph graph) {
        goToMainTab();
        descriptionTextArea.setValue(graph.getDescription());
        authorInput.setValue(graph.getAuthor());
        return new GraphPage();
    }

    @Step("Сохранение графа со следующей патч-версией")
    public GraphPage saveGraphWithPatchVersion() {
        saveButton.click();
        new SaveDialog().saveWithNextPatchVersion(saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Проверка, что следующая предлагаемая версия равна '{nextVersion}', и сохранение")
    public GraphPage checkNextVersionAndSave(String nextVersion) {
        saveButton.click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Сохранение графа с указанием новой версии '{newVersion}'")
    public GraphPage saveGraphWithManualVersion(String newVersion) {
        saveButton.click();
        new SaveDialog().saveWithVersion(newVersion, saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Проверка сохранения графа с некорректно указанной версией '{newVersion}'")
    public GraphPage trySaveGraphWithIncorrectVersion(String newVersion, String currentVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return new GraphPage();
    }

    @Step("Проверка недоступности сохранения графа при достижении лимита версий")
    public GraphPage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']")
                .shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        return new GraphPage();
    }

    @Step("Переход на вкладку 'Общая информация'")
    public GraphPage goToMainTab() {
        goToTab("Общая информация");
        return this;
    }

    @Step("Переход на вкладку 'Узлы'")
    public GraphNodesPage goToNodesTab() {
        goToTab("Узлы");
        return new GraphNodesPage();
    }

    @Step("Переход на вкладку 'Модификаторы'")
    public GraphModifiersPage goToModifiersTab() {
        goToTab("Модификаторы");
        return new GraphModifiersPage();
    }

    @Step("Переход на вкладку 'Параметры заказа'")
    public GraphOrderParamsPage goToOrderParamsTab() {
        goToTab("Параметры заказа");
        return new GraphOrderParamsPage();
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public GraphPage goToVersionComparisonTab() {
        goToTab("Сравнение версий");
        return this;
    }

    @Step("Переход на вкладку 'История изменений'")
    public AuditPage goToAuditTab() {
        goToTab("История изменений");
        return new AuditPage();
    }

    @Step("Открытие диалога удаления графа")
    public DeleteDialog openDeleteDialog() {
        deleteButton.click();
        return new DeleteDialog();
    }

    @Step("Удаление графа")
    public void delete() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete();
    }

    @Step("Проверка недоступности удаления используемого графа")
    public GraphPage checkDeleteUsedGraphUnavailable(Graph graph) {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDeleteNotAvailable("Нельзя удалить граф, который используется другими" +
                " объектами. Отвяжите граф от объектов и повторите попытку");
        usageLink.click();
        checkTabIsSelected("Использование");
        return this;
    }

    @Step("Возврат в список графов")
    public GraphsListPage returnToGraphsList() {
        graphsListLink.scrollIntoView(false).click();
        return new GraphsListPage();
    }

    @Step("Задание в поле 'Автор' значения '{value}'")
    public GraphPage setAuthor(String value) {
        goToMainTab();
        authorInput.setValue(value);
        return this;
    }

    @Step("Проверка, что отображается объект использования '{product.name}'")
    public GraphPage checkUsageInProduct(Product product) {
        checkTabIsSelected("Использование");
        String nameColumn = "Имя";
        Table table = new Table(nameColumn);
        table.getRowByColumnValue(nameColumn, product.getName());
        table.getRowByColumnValue("Тип", "Продукт");
        table.getRowByColumnValue("Версия объекта", product.getVersion());
        table.getRowByColumnValue("Версия графа", product.getGraphVersion());
        table.getRowByColumnValue("Расчетная версия графа", product.getGraphVersion());
        return this;
    }

    @Step("Проверка, что отображается объект использования '{graph.name}'")
    public GraphPage checkUsageInGraph(Graph graph) {
        checkTabIsSelected("Использование");
        String nameColumn = "Имя";
        Table table = new Table(nameColumn);
        table.getRowByColumnValue(nameColumn, graph.getName());
        table.getRowByColumnValue("Тип", "Граф");
        table.getRowByColumnValue("Версия объекта", "");
        table.getRowByColumnValue("Версия графа", "");
        table.getRowByColumnValue("Расчетная версия графа", "");
        return this;
    }
}
