package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ui.cloud.tests.productCatalog.TestUtils;

import static com.codeborne.selenide.Selenide.$x;

public class GraphPage {
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final SelenideElement graphVersion = $x("//div[@aria-labelledby='version']");
    private final SelenideElement saveButton = $x("//span[text()='Сохранить']/parent::button");
    private final SelenideElement deleteButton = $x("//span[text()='Удалить']/parent::button");
    private final SelenideElement confirmDeleteButton = $x("//form//span[text()='Удалить']/parent::button");
    private final SelenideElement dialogCancelButton = $x("//div[@role='dialog']//span[text()='Отмена']/parent::button");
    private final SelenideElement dialogSaveButton = $x("//div[@role='dialog']//span[text()='Сохранить']/parent::button");
    private final SelenideElement saveNextPatchVersionCheckbox = $x("//input[@name='saveAsNextVersion']");
    private final SelenideElement newVersionInput = $x("//input[@name='newVersion']");
    private final SelenideElement descriptionField = $x("//textarea[@name='description']");
    private final SelenideElement viewJSONButton = $x("//span[text()='JSON']/parent::button");
    private final SelenideElement expandJSONView = $x("//button[@aria-label='fullscreen']");
    private final SelenideElement closeJSONView = $x("//button[@aria-label='close']");
    private final SelenideElement nodesTab = $x("//span[text()='Узлы']//ancestor::button");
    private final SelenideElement graphNameInput = $x("//input[@name='name']");
    private final SelenideElement graphTitleInput = $x("//input[@name='title']");
    private final SelenideElement graphId = $x("//form//p//b");
    private final SelenideElement idInput = $x("//input[@name = 'id']");
    private final SelenideElement authorInput = $x("//input[@name = 'author']");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
    }

    public GraphPage checkGraphVersion(String version) {
        graphVersion.shouldBe(Condition.visible).shouldHave(Condition.exactText(version));
        return new GraphPage();
    }

    public GraphPage editGraph(String description) {
        descriptionField.setValue(description);
        //TODO временно для ПК 2.0 чтобы изменилась версия
        authorInput.setValue(description);
        return new GraphPage();
    }

    public GraphPage saveGraphWithPatchVersion() {
        TestUtils.scrollToTheBottom();
        saveButton.shouldBe(Condition.enabled).click();
        dialogSaveButton.click();
        return new GraphPage();
    }

    public GraphPage checkAndSaveNextManualVersion(String version) {
        TestUtils.scrollToTheBottom();
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.shouldHave(Condition.exactValue(version));
        dialogSaveButton.click();
        return new GraphPage();
    }

    public GraphPage saveGraphWithManualVersion(String newVersion) {
        TestUtils.scrollToTheBottom();
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
        newVersionInput.setValue(newVersion);
        dialogSaveButton.click();
        return new GraphPage();
    }

    public GraphPage trySaveGraphWithIncorrectVersion(String newVersion) {
        TestUtils.scrollToTheBottom();
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionInput.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
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

    public GraphPage checkGraphAttributes(String name, String title, String version) {
        graphNameInput.shouldHave(Condition.exactValue(name));
        graphTitleInput.shouldHave(Condition.exactValue(title));
        checkGraphVersion(version);
        return new GraphPage();
    }

    public GraphsListPage deleteGraph() {
        deleteButton.click();
        String id = graphId.getText();
        idInput.setValue(id);
        confirmDeleteButton.click();
        return new GraphsListPage();
    }
}
