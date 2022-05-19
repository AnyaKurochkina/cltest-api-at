package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

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

    public GraphNodesPage addNodeSubgraph(String name, String description, String subgraphName) {
        addNodeButton.click();
        nodeName.setValue(name);
        nodeDescription.setValue(description);
        showSubgraphsButton.click();
        subgraphInput.setValue(subgraphName);
        $x("//div[contains(@title,'"+subgraphName+"')]").shouldBe(Condition.enabled).click();
        inputJSONField.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
        inputJSONField.setValue("{\"test_param\":\"test_value\"}");
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        return new GraphNodesPage();
    }

    public GraphNodesPage checkNodeAttributes(String subgraphName, String name, String description) {
        $x("//div[text()='"+description+"']/..//*[name()='svg' and @class]").click();
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        editNodeButton.click();
        nodeName.shouldHave(Condition.exactValue(name));
        nodeDescription.shouldHave(Condition.exactValue(description));
        formCancelButton.click();
        return new GraphNodesPage();
    }
}
