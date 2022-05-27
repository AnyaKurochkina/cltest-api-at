package ui.productCatalog.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ui.productCatalog.models.SubgraphNode;
import ui.productCatalog.tests.TestUtils;

import static com.codeborne.selenide.Selenide.$x;

public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("//button[@aria-label = 'add node']");
    private final SelenideElement editNodeButton = $x("//button[@aria-label = 'edit node']");
    private final SelenideElement deleteNodesButton = $x("//button[@aria-label = 'delete items']");
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
    private final SelenideElement onPrebillingToggle = $x("//input[@name='on_prebilling']");
    private final SelenideElement runOnRollbackToggle = $x("//input[@name='run_on_rollback']");
    private final SelenideElement holdToggle = $x("//input[@name='hold']");
    private final SelenideElement loggingLevelSelect = $x("//div[text()='Уровень логирования']/ancestor::label/..//select");
    private final SelenideElement nameRequiredFieldHint = $x("//label[contains(text(),'Название')]/parent::div//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement descriptionRequiredFieldHint = $x("//label[contains(text(),'Описание')]/parent::div//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement incorrectNumberHint = $x("//label[text()='Номер']/parent::div//div[text()='Введите корректное значение']");
    private final SelenideElement incorrectTimeoutHint = $x("//label[text()='Timeout']/parent::div//div[text()='Введите корректное значение']");
    private final SelenideElement nameNonUniqueHint = $x("//div[text()='Узел с данным значением \"name\" уже существует']");
    private final SelenideElement onPrebillingToggleOn = $x("//p[text()='on_prebilling']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement runOnRollbackToggleOn = $x("//p[text()='run_on_rollback']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement holdToggleOn = $x("//p[text()='hold']/parent::div//span[contains(@class,'checked')]");

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
        onPrebillingToggle.click();
        runOnRollbackToggle.click();
        holdToggle.click();
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        TestUtils.wait(1000);
        return this;
    }

    public GraphNodesPage checkAddNodeSubgraphDisabled(SubgraphNode node) {
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        showSubgraphsButton.click();
        subgraphInput.setValue(node.getSubgraphName());
        numberInput.setValue(String.valueOf(node.getNumber()));
        timeoutInput.setValue(String.valueOf(node.getTimeout()));
        loggingLevelSelect.shouldBe(Condition.disabled);
        countInput.setValue(String.valueOf(node.getCount()));
        if (node.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (node.getDescription().isEmpty()) {
            descriptionRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (node.getNumber().equals("0")) {
            incorrectNumberHint.shouldBe(Condition.visible);
        }
        if (node.getTimeout().equals("0")) {
            incorrectTimeoutHint.shouldBe(Condition.visible);
        }
        formAddNodeButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    public GraphNodesPage checkAddNodeSubgraphWithNonUniqueNameDisabled(SubgraphNode node) {
        TestUtils.scrollToTheTop();
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nameNonUniqueHint.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    public GraphNodesPage checkNodeAttributes(SubgraphNode node) {
        if (node.getNumber().equals("")) {
            node.setNumber("1");
        }
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        nodeName.shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.shouldHave(Condition.exactValue(node.getDescription()));
        $x("//div[text() = '" + node.getSubgraphName() + "']").shouldBe(Condition.visible);
        numberInput.shouldHave(Condition.exactValue(node.getNumber()));
        timeoutInput.shouldHave(Condition.exactValue(node.getTimeout()));
        countInput.shouldHave(Condition.exactValue(node.getCount()));
        onPrebillingToggleOn.shouldBe(Condition.visible);
        runOnRollbackToggleOn.shouldBe(Condition.visible);
        holdToggleOn.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    public GraphNodesPage deleteNode(SubgraphNode node) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        deleteNodesButton.click();
        saveGraphWithPatchVersion();
        return this;
    }
}
