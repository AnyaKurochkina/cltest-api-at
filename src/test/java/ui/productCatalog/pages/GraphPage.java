package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$x;

public class GraphPage {
    private final SelenideElement graphsListLink = $x("//a[text() = 'Список графов']");
    private final SelenideElement graphVersion = $x("//div[@aria-labelledby='version']");
    private final SelenideElement saveButton = $x("//span[text()='Сохранить']/parent::button");
    private final SelenideElement dialogCancelButton = $x("//div[@role='dialog']//span[text()='Отмена']/parent::button");
    private final SelenideElement dialogSaveButton = $x("//div[@role='dialog']//span[text()='Сохранить']/parent::button");
    private final SelenideElement saveNextPatchVersionCheckbox = $x("//input[@name='saveAsNextVersion']");
    private final SelenideElement newVersionField = $x("//input[@name='newVersion']");
    private final SelenideElement descriptionField = $x("//textarea[@name='description']");
    private final SelenideElement viewJSONButton = $x("//span[text()='JSON']/ancestor::button");
    private final SelenideElement expandJSONView = $x("//button[@aria-label='fullscreen']");
    private final SelenideElement closeJSONView = $x("//button[@aria-label='close']");
    private final SelenideElement nodesTab = $x("//span[text()='Узлы']//ancestor::button");

    public GraphPage() {
        graphsListLink.shouldBe(Condition.visible);
    }

    public GraphPage checkGraphVersion(String version) {
        graphVersion.shouldBe(Condition.visible).shouldHave(Condition.exactText(version));
        return new GraphPage();
    }

    public GraphPage editGraph(String description) {
        descriptionField.setValue(description);
        return new GraphPage();
    }

    public GraphPage saveGraphWithPatchVersion() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        saveButton.shouldBe(Condition.enabled).click();
        dialogSaveButton.click();
        return new GraphPage();
    }

    public GraphPage saveGraphWithManualVersion(String newVersion) {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionField.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
        newVersionField.setValue(newVersion);
        dialogSaveButton.click();
        return new GraphPage();
    }

    public GraphPage trySaveGraphWithIncorrectVersion(String newVersion) {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        saveButton.shouldBe(Condition.enabled).click();
        saveNextPatchVersionCheckbox.click();
        newVersionField.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
        newVersionField.setValue(newVersion);
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
}
