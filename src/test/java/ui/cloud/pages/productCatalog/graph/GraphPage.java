package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.DeleteDialog;
import ui.elements.DropDown;
import ui.uiModels.Graph;

import static com.codeborne.selenide.Selenide.$x;

public class GraphPage {
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final SelenideElement graphVersion = $x("//div[@aria-labelledby='version']");
    private final SelenideElement saveButton = $x("//span[text()='Сохранить']/parent::button");
    private final SelenideElement deleteButton = $x("//span[text()='Удалить']/parent::button");
    private final SelenideElement dialogCancelButton = $x("//div[@role='dialog']//span[text()='Отмена']/parent::button");
    private final SelenideElement dialogSaveButton = $x("//div[@role='dialog']//span[text()='Сохранить']/parent::button");
    private final SelenideElement saveNextPatchVersionCheckbox = $x("//input[@name='saveAsNextVersion']");
    private final SelenideElement newVersionInput = $x("//input[@name='newVersion']");
    private final SelenideElement descriptionField = $x("//textarea[@name='description']");
    private final SelenideElement viewJSONButton = $x("//span[text()='JSON']/parent::button");
    private final SelenideElement expandJSONView = $x("//button[@aria-label='fullscreen']");
    private final SelenideElement closeJSONView = $x("//button[@aria-label='close']");
    private final SelenideElement nodesTab = $x("//span[text()='Узлы']//parent::button");
    private final SelenideElement modifiersTab = $x("//span[text()='Модификаторы']//parent::button");
    private final SelenideElement orderParamsTab = $x("//span[text()='Параметры заказа']//parent::button");
    private final SelenideElement versionComparisonTab = $x("//span[text()='Сравнение версий']//parent::button");
    private final SelenideElement graphNameInput = $x("//input[@name='name']");
    private final SelenideElement graphTitleInput = $x("//input[@name='title']");
    private final SelenideElement authorInput = $x("//input[@name = 'author']");
    private final SelenideElement saveGraphSuccessNotification = $x("//div[text()='Граф успешно сохранен']");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
    }

    public GraphPage checkGraphVersion(String version) {
        graphVersion.shouldBe(Condition.visible).shouldHave(Condition.exactText(version));
        return new GraphPage();
    }

    public GraphPage selectGraphVersion(String version) {
        DropDown graphVersion = DropDown.byLabel("Выберите версию");
        graphVersion.selectByValue(version);
        return this;
    }

    public GraphPage editGraph(Graph graph) {
        descriptionField.setValue(graph.getDescription());
        authorInput.setValue(graph.getAuthor());
        return new GraphPage();
    }

    public GraphPage saveGraphWithPatchVersion() {
        saveButton.shouldBe(Condition.enabled).click();
        dialogSaveButton.click();
        new Alert().checkText("Граф успешно сохранен").checkColor(Alert.Color.GREEN).close();
        return new GraphPage();
    }

    public GraphPage checkAndSaveNextManualVersion(String version) {
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.shouldHave(Condition.exactValue(version));
        dialogSaveButton.click();
        saveGraphSuccessNotification.shouldBe(Condition.visible);
        return new GraphPage();
    }

    public GraphPage saveGraphWithManualVersion(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        newVersionInput.setValue(newVersion);
        dialogSaveButton.click();
        saveGraphSuccessNotification.shouldBe(Condition.visible);
        return new GraphPage();
    }

    public GraphPage trySaveGraphWithIncorrectVersion(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        newVersionInput.setValue(newVersion);
        dialogSaveButton.shouldBe(Condition.disabled);
        $x("//p[text()[contains(., 'Версия должна быть выше, чем')]]").shouldBe(Condition.visible);
        dialogCancelButton.click();
        return new GraphPage();
    }

    public GraphPage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']").shouldBe(Condition.visible);
        saveButton.shouldBe(Condition.disabled);
        return new GraphPage();
    }

    public GraphPage viewJSON() {
        viewJSONButton.click();
        $x("//span[text()='\"id\"']").shouldBe(Condition.visible);
        expandJSONView.click();
        expandJSONView.click();
        closeJSONView.click();
        return new GraphPage();
    }

    public GraphNodesPage goToNodesTab() {
        nodesTab.click();
        return new GraphNodesPage();
    }

    public GraphModifiersPage goToModifiersTab() {
        TestUtils.scrollToTheTop();
        modifiersTab.click();
        return new GraphModifiersPage();
    }

    public GraphOrderParamsPage goToOrderParamsTab() {
        TestUtils.scrollToTheTop();
        orderParamsTab.click();
        return new GraphOrderParamsPage();
    }

    public VersionComparisonPage goToVersionComparisonTab() {
        TestUtils.scrollToTheTop();
        versionComparisonTab.click();
        return new VersionComparisonPage();
    }

    public GraphPage checkGraphAttributes(String name, String title, String version) {
        graphNameInput.shouldHave(Condition.exactValue(name));
        graphTitleInput.shouldHave(Condition.exactValue(title));
        checkGraphVersion(version);
        return new GraphPage();
    }

    public DeleteDialog deleteGraph() {
        deleteButton.click();
        return new DeleteDialog();
    }
}
