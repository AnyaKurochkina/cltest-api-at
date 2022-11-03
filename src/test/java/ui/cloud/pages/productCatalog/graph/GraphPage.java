package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.SaveDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.DropDown;
import ui.models.Graph;

import static com.codeborne.selenide.Selenide.$x;

public class GraphPage {
    protected static final String saveGraphAlertText = "Граф успешно сохранен";
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final SelenideElement graphVersion = $x("//div[@aria-labelledby='version']");
    private final SelenideElement saveButton = $x("//div[text()='Сохранить']/parent::button");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final SelenideElement descriptionField = $x("//textarea[@name='description']");
    private final SelenideElement viewJSONButton = $x("//div[text()='JSON']/parent::button");
    private final SelenideElement expandJSONView = $x("//button[@aria-label='fullscreen']");
    private final SelenideElement closeJSONView = $x("//button[@aria-label='close']");
    private final SelenideElement mainTab = $x("//span[text()='Общая информация']//parent::button");
    private final SelenideElement nodesTab = $x("//span[text()='Узлы']//parent::button");
    private final SelenideElement modifiersTab = $x("//span[text()='Модификаторы']//parent::button");
    private final SelenideElement orderParamsTab = $x("//span[text()='Параметры заказа']//parent::button");
    private final SelenideElement versionComparisonTab = $x("//span[text()='Сравнение версий']//parent::button");
    private final SelenideElement auditTab = $x("//span[text()='История изменений']//parent::button");
    private final SelenideElement graphNameInput = $x("//input[@name='name']");
    private final SelenideElement graphTitleInput = $x("//input[@name='title']");
    private final SelenideElement authorInput = $x("//input[@name = 'author']");
    private final SelenideElement graphsList = $x("//a[text()='Список графов']");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
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
    public GraphPage editGraph(Graph graph) {
        goToMainTab();
        descriptionField.setValue(graph.getDescription());
        authorInput.setValue(graph.getAuthor());
        return new GraphPage();
    }

    @Step("Сохранение графа со следующей патч-версией")
    public GraphPage saveGraphWithPatchVersion() {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithNextPatchVersion(saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Проверка, что следующая предлагаемая версия равна '{nextVersion}', и сохранение")
    public GraphPage checkNextVersionAndSave(String nextVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Сохранение графа с указанием новой версии '{newVersion}'")
    public GraphPage saveGraphWithManualVersion(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithVersion(newVersion, saveGraphAlertText);
        return new GraphPage();
    }

    @Step("Проверка сохранения графа с некорректно указанной версией '{newVersion}'")
    public GraphPage trySaveGraphWithIncorrectVersion(String newVersion, String currentVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return new GraphPage();
    }

    @Step("Проверка недоступности сохранения графа при достижении лимита версий")
    public GraphPage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']").shouldBe(Condition.visible);
        saveButton.shouldBe(Condition.disabled);
        return new GraphPage();
    }

    @Step("Просмотр JSON графа")
    public GraphPage viewJSON() {
        viewJSONButton.click();
        $x("//span[text()='\"id\"']").shouldBe(Condition.visible);
        expandJSONView.click();
        expandJSONView.click();
        closeJSONView.click();
        return new GraphPage();
    }

    @Step("Переход на вкладку 'Общая информация'")
    public GraphPage goToMainTab() {
        TestUtils.scrollToTheTop();
        mainTab.click();
        return this;
    }

    @Step("Переход на вкладку 'Узлы'")
    public GraphNodesPage goToNodesTab() {
        nodesTab.click();
        TestUtils.wait(600);
        return new GraphNodesPage();
    }

    @Step("Переход на вкладку 'Модификаторы'")
    public GraphModifiersPage goToModifiersTab() {
        TestUtils.scrollToTheTop();
        modifiersTab.click();
        return new GraphModifiersPage();
    }

    @Step("Переход на вкладку 'Параметры заказа'")
    public GraphOrderParamsPage goToOrderParamsTab() {
        TestUtils.scrollToTheTop();
        orderParamsTab.click();
        return new GraphOrderParamsPage();
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public VersionComparisonPage goToVersionComparisonTab() {
        TestUtils.scrollToTheTop();
        versionComparisonTab.click();
        return new VersionComparisonPage();
    }

    @Step("Переход на вкладку 'История изменений'")
    public AuditPage goToAuditTab() {
        TestUtils.scrollToTheTop();
        auditTab.click();
        return new AuditPage();
    }

    @Step("Проверка атрибутов графа '{name}'")
    public GraphPage checkGraphAttributes(Graph graph) {
        graphNameInput.shouldHave(Condition.exactValue(graph.getName()));
        graphTitleInput.shouldHave(Condition.exactValue(graph.getTitle()));
        checkGraphVersion(graph.getVersion());
        return new GraphPage();
    }

    @Step("Открытие диалога удаления графа")
    public DeleteDialog openDeleteDialog() {
        deleteButton.click();
        return new DeleteDialog();
    }

    @Step("Удаление графа")
    public void deleteGraph() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete();
    }

    @Step("Возврат в список графов")
    public GraphsListPage returnToGraphsList() {
        TestUtils.scrollToTheTop();
        graphsList.click();
        return new GraphsListPage();
    }
}
