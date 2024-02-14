package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.service.Service;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.pages.productCatalog.actions.ActionPage;
import ui.cloud.pages.productCatalog.product.ProductPage;
import ui.cloud.pages.productCatalog.service.ServicePage;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class GraphPage extends EntityPage {

    public final static String SAVE_GRAPH_ALERT_TEXT = "Граф успешно сохранен";
    protected final Tab generalInfoTab = Tab.byText("Общая информация");
    private final Tab orderParamsTab = Tab.byText("Параметры заказа");
    private final Tab objectInfoTab = Tab.byText("Информация о графе");
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final TextArea descriptionTextArea = TextArea.byName("description");
    private final Input authorInput = Input.byName("author");
    private final Button usageButton = Button.byText("Перейти в использование");
    private final String nameColumn = "Имя";
    private final RadioGroup typeRadioGroup = RadioGroup.byFieldsetLabel("Тип");
    private final Button orderFormTab = Button.byText("Форма заказа");
    private final Button plannerTab = Button.byText("Планировщик");
    private final Button addOptionButton = Button.byText("Добавить конфигурацию");
    private final Button saveOptionButton = Button.byXpath("//div[@role='dialog']//button[.='Сохранить']");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
    }

    @Step("Задание атрибутов графа '{graph.name}'")
    public GraphPage setAttributes(Graph graph) {
        goToMainTab();
        titleInput.setValue(graph.getTitle());
        nameInput.setValue(graph.getName());
        typeRadioGroup.select(graph.getType());
        descriptionTextArea.setValue(graph.getDescription());
        authorInput.setValue(graph.getAuthor());
        objectInfoTab.switchTo();
        objectInfoEditor.setValue(graph.getObject_info());
        return new GraphPage();
    }

    @Step("Проверка атрибутов графа '{graph.name}'")
    public GraphPage checkAttributes(Graph graph) {
        nameInput.getInput().shouldHave(Condition.exactValue(graph.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(graph.getTitle()));
        checkGraphVersion(graph.getVersion());
        descriptionTextArea.getElement().shouldHave(Condition.exactText(graph.getDescription()));
        authorInput.getInput().shouldHave(Condition.exactValue(graph.getAuthor()));
        objectInfoTab.switchTo();
        assertEquals(graph.getObject_info(), objectInfoEditor.getText());
        return new GraphPage();
    }

    @Step("Проверка, что отображаемая версия графа равна '{version}'")
    public GraphPage checkGraphVersion(String version) {
        Waiting.find(() -> versionSelect.getValue().equals(version), Duration.ofSeconds(5));
        return new GraphPage();
    }

    @Step("Выбор версии графа '{version}'")
    public GraphPage selectGraphVersion(String version) {
        Select graphVersion = Select.byLabel("Выберите версию");
        graphVersion.set(version);
        return this;
    }

    @Step("Сохранение графа со следующей патч-версией")
    public GraphPage saveGraphWithPatchVersion() {
        saveButton.click();
        new SaveDialog().saveWithNextPatchVersion(SAVE_GRAPH_ALERT_TEXT);
        return new GraphPage();
    }

    @Step("Проверка, что следующая предлагаемая версия равна '{nextVersion}', и сохранение")
    public GraphPage checkNextVersionAndSave(String nextVersion) {
        saveButton.click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, SAVE_GRAPH_ALERT_TEXT);
        return new GraphPage();
    }

    @Step("Сохранение графа с указанием новой версии '{newVersion}'")
    public GraphPage saveGraphWithManualVersion(String newVersion) {
        saveButton.click();
        new SaveDialog().saveWithVersion(newVersion, SAVE_GRAPH_ALERT_TEXT);
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
        goToTab("Сравнение");
        return this;
    }

    @Step("Переход на вкладку 'Использование'")
    public GraphPage goToUsageTab() {
        goToTab("Использование");
        return this;
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
    public GraphPage checkDeleteUsedGraphUnavailable() {
        deleteButton.click();
        new DeleteDialog().submitAndCheckNotDeletable("Нельзя удалить граф, который используется другими" +
                " объектами. Отвяжите граф от объектов и повторите попытку");
        usageButton.click();
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

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public GraphPage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        graphsListLink.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        backButton.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        mainPage.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Ок")
    public GraphPage checkUnsavedChangesAlertAccept(Graph graph) {
        String newValue = "new title";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new GraphsListPage().openGraphPage(graph.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(graph.getTitle()));
        titleInput.setValue(newValue);
        graphsListLink.click();
        acceptAlert(unsavedChangesAlertText);
        new GraphsListPage().openGraphPage(graph.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(graph.getTitle()));
        titleInput.setValue(newValue);
        backButton.click();
        acceptAlert(unsavedChangesAlertText);
        new GraphsListPage().openGraphPage(graph.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(graph.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToGraphsPage().openGraphPage(graph.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(graph.getTitle()));
        return this;
    }

    @Step("Проверка, что отображается объект использования '{product.name}'")
    public GraphPage checkUsageInProduct(Product product) {
        goToUsageTab();
        checkUsageTable(product.getName(), "Продукт", product.getVersion(), product.getGraphVersion()
                , product.getGraphVersion());
        return this;
    }

    @Step("Проверка, что отображается объект использования '{action.name}'")
    public GraphPage checkUsageInAction(Action action) {
        goToUsageTab();
        checkUsageTable(action.getName(), "Действие", action.getVersion(), action.getGraphVersion()
                , action.getGraphVersion());
        return this;
    }

    @Step("Проверка, что отображается объект использования '{service.name}'")
    public GraphPage checkUsageInService(Service service) {
        goToUsageTab();
        checkUsageTable(service.getName(), "Сервис", service.getVersion(), service.getGraphVersion()
                , service.getGraphVersion());
        return this;
    }

    @Step("Переход на страницу продукта '{product.name}' с вкладки использования")
    public ProductPage goToProductByUsageLink(Product product) {
        goToUsageTab();
        new Table(nameColumn).getRowByColumnValue(nameColumn, product.getName()).get().$x(".//*[name()='svg' and @class]")
                .click();
        switchTo().window(1);
        return new ProductPage();
    }

    @Step("Переход на страницу действия '{action.name}' с вкладки использования")
    public ActionPage goToActionByUsageLink(Action action) {
        goToUsageTab();
        new Table(nameColumn).getRowByColumnValue(nameColumn, action.getName()).get()
                .$x(".//*[name()='svg' and @class]").click();
        switchTo().window(1);
        return new ActionPage();
    }

    @Step("Переход на страницу сервиса '{service.name}' с вкладки использования")
    public ServicePage goToServiceByUsageLink(Service service) {
        goToUsageTab();
        new Table(nameColumn).getRowByColumnValue(nameColumn, service.getName()).get()
                .$x(".//*[name()='svg' and @class]").click();
        switchTo().window(1);
        return new ServicePage();
    }

    @Step("Переход на страницу графа '{graph.name}' с вкладки использования")
    public GraphPage goToGraphByUsageLink(Graph graph) {
        goToUsageTab();
        new Table(nameColumn).getRowByColumnValue(nameColumn, graph.getName()).get()
                .$x(".//*[name()='svg' and @class]").click();
        switchTo().window(1);
        return new GraphPage();
    }

    @Step("Проверка, что отображается объект использования '{graph.name}'")
    public GraphPage checkUsageInGraph(Graph graph) {
        goToUsageTab();
        checkUsageTable(graph.getName(), "Граф", graph.getVersion(), "", "");
        return this;
    }

    @Step("Проверка данных в таблице 'Объекты использования'")
    private void checkUsageTable(String name, String type, String objectVersion, String graphVersion,
                                 String calculatedGraphVersion) {
        Table table = new Table(nameColumn);
        table.getRowByColumnValue(nameColumn, name);
        table.getRowByColumnValue("Тип", type);
        table.getRowByColumnValue("Версия объекта", objectVersion);
        table.getRowByColumnValue("Версия графа", graphVersion);
        table.getRowByColumnValue("Расчетная версия графа", calculatedGraphVersion);
    }

    @Step("Проверка заголовков списка зависимых объектов")
    public GraphPage checkUsageTableHeaders() {
        goToUsageTab();
        AssertUtils.assertHeaders(new Table(nameColumn),
                nameColumn, "Тип", "Версия объекта", "Версия графа", "Расчетная версия графа", "", "");
        return this;
    }
}
