package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import ui.productCatalog.models.SubgraphNode;

import static com.codeborne.selenide.Selenide.$x;

public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("//button[@aria-label = 'add node']");
    private final SelenideElement editNodeButton = $x("//button[@aria-label = 'edit node']");
    private final SelenideElement nodeName = $x("//form//input[@name = 'name']");
    private final SelenideElement nodeDescription = $x("//form//input[@name = 'description']");
    private final SelenideElement subgraphInput = $x("//label[text() = 'Подграф']/..//input");
    private final SelenideElement formAddNodeButton = $x("//form//span[text() = 'Добавить']//ancestor::button");
    private final SelenideElement formCancelButton = $x("//form//span[text() = 'Отмена']//ancestor::button");
    private final SelenideElement showSubgraphsButton = $x("(//label[text() = 'Подграф']/..//*[name()='svg'])[2]");
    private final SelenideElement inputJSONField = $x("//label[text()='Input']/../..//textarea");
    private final SelenideElement outputJSONField = $x("//label[text()='Output']/../..//textarea");
    private final SelenideElement printedOutputJSONField = $x("//label[text()='Printed output']/../..//textarea");
    private final SelenideElement numberInput = $x("//input[@name='number']");
    private final SelenideElement timeoutInput = $x("//input[@name='timeout']");
    private final SelenideElement countInput = $x("//input[@name='count']");
    private final SelenideElement forEachInput = $x("//input[@name='for_each']");
    private final SelenideElement conditionInput = $x("//input[@name='condition']");
    private final SelenideElement onPrebillingCheckbox = $x("//input[@name='on_prebilling']");
    private final SelenideElement runOnRollbackCheckbox = $x("//input[@name='run_on_rollback']");
    private final SelenideElement holdCheckbox = $x("//input[@name='hold']");
    private final SelenideElement loggingLevelSelect = $x("//div[text()='Уровень логирования']/ancestor::label/..//select");

    public GraphNodesPage addNodeSubgraph(SubgraphNode node) {
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        showSubgraphsButton.click();
        subgraphInput.setValue(node.getSubgraphName());
        $x("//div[contains(@title,'" + node.getSubgraphName() + "')]").shouldBe(Condition.enabled).click();
        inputJSONField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        inputJSONField.setValue(node.getInput());
        outputJSONField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        outputJSONField.setValue(node.getOutput());
        numberInput.setValue(String.valueOf(node.getNumber()));
        timeoutInput.setValue(String.valueOf(node.getTimeout()));
        loggingLevelSelect.shouldBe(Condition.disabled);
        countInput.setValue(String.valueOf(node.getCount()));
        onPrebillingCheckbox.click();
        runOnRollbackCheckbox.click();
        holdCheckbox.click();
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        return new GraphNodesPage();
    }

    public GraphNodesPage checkNodeAttributes(SubgraphNode node) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        editNodeButton.click();
        nodeName.shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.shouldHave(Condition.exactValue(node.getDescription()));
        $x("//div[text() = '" + node.getSubgraphName() + "']").shouldBe(Condition.visible);
        numberInput.shouldHave(Condition.exactValue(node.getNumber()));
        timeoutInput.shouldHave(Condition.exactValue(node.getTimeout()));
        countInput.shouldHave(Condition.exactValue(node.getCount()));
        formCancelButton.click();
        return new GraphNodesPage();
    }
}
