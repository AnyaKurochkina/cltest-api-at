package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import ui.uiModels.Node;
import ui.uiModels.SubgraphNode;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.uiModels.TemplateNode;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.actions;

public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("//button[@aria-label = 'add node']");
    private final SelenideElement editNodeButton = $x("//button[@aria-label = 'edit node']");
    private final SelenideElement copyNode = $x("//li[@id='clone_node']");
    private final SelenideElement deleteNodesButton = $x("//button[@aria-label = 'delete items']");
    private final SelenideElement nodeName = $x("//form//input[@name = 'name']");
    private final SelenideElement nodeDescription = $x("//form//input[@name = 'description']");
    private final SelenideElement subgraphInput = $x("//label[text() = 'Подграф']/..//input");
    private final SelenideElement templateInput = $x("//label[text() = 'Шаблон']/..//input");
    private final SelenideElement formAddNodeButton = $x("//form//span[text() = 'Добавить']//ancestor::button");
    private final SelenideElement formSaveNodeButton = $x("//form//span[text() = 'Сохранить']//ancestor::button");
    private final SelenideElement formCancelButton = $x("//form//span[text() = 'Отмена']//ancestor::button");
    private final SelenideElement showSubgraphsButton = $x("(//label[text() = 'Подграф']/..//*[name()='svg'])[2]");
    private final SelenideElement showTemplatesButton = $x("(//label[text() = 'Шаблон']/..//*[name()='svg'])[2]");
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
    private final SelenideElement isSequentialToggle = $x("//input[@name='is_sequential']");
    private final SelenideElement damageOrderOnErrorToggle = $x("//form//input[@name='damage_order_on_error']");
    private final SelenideElement loggingLevelSelect = $x("//div[text()='Уровень логирования']/ancestor::label/..//select");
    private final SelenideElement nameRequiredFieldHint = $x("//label[contains(text(),'Название')]/parent::div//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement descriptionRequiredFieldHint = $x("//label[contains(text(),'Описание')]/parent::div//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement incorrectNumberHint = $x("//label[text()='Номер']/parent::div//div[text()='Введите корректное значение']");
    private final SelenideElement incorrectTimeoutHint = $x("//label[text()='Timeout']/parent::div//div[text()='Введите корректное значение']");
    private final SelenideElement nameNonUniqueHint = $x("//div[text()='Узел с данным значением \"name\" уже существует']");
    private final SelenideElement onPrebillingToggleOn = $x("//p[text()='on_prebilling']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement runOnRollbackToggleOn = $x("//p[text()='run_on_rollback']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement holdToggleOn = $x("//p[text()='hold']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement isSequentialToggleOn = $x("//p[text()='Is sequential']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement damageOrderOnErrorToggleOn = $x("//p[text()='damage_order_on_error']/ancestor::div[2]//span[contains(@class,'checked')]");
    private final SelenideElement subgraphVersionSelect = $x("(//label[text()='Версия']/parent::div//select)[2]");
    private final SelenideElement showSubgraphVersions = $x("(//label[text()='Версия']/parent::div//*[name()='svg'])[2]");

    @Step("Добавление узла графа и сохранение")
    public GraphNodesPage addNodeAndSave(Node node) {
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        if (node instanceof SubgraphNode) {
            showSubgraphsButton.click();
            subgraphInput.setValue(((SubgraphNode) node).getSubgraphName());
            $x("//div[contains(@title,'" + ((SubgraphNode) node).getSubgraphName() + "')]").shouldBe(Condition.enabled).click();
            inputJSONField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputJSONField.setValue(node.getInput());
            outputJSONField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            outputJSONField.setValue(node.getOutput());
            numberInput.setValue(String.valueOf(node.getNumber()));
            timeoutInput.setValue(String.valueOf(node.getTimeout()));
            loggingLevelSelect.shouldBe(Condition.disabled);
            countInput.setValue(String.valueOf(node.getCount()));
        }
        if (node instanceof TemplateNode) {
            showTemplatesButton.click();
            templateInput.setValue(((TemplateNode) node).getTemplateName());
            $x("//div[contains(@title,'" + ((TemplateNode) node).getTemplateName() + "')]").shouldBe(Condition.enabled).click();
        }
        onPrebillingToggle.click();
        runOnRollbackToggle.click();
        holdToggle.click();
        isSequentialToggle.click();
        damageOrderOnErrorToggle.click();
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        TestUtils.wait(1000);
        return this;
    }

    public GraphNodesPage editNodeSubgraph(SubgraphNode node, String version, String description) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        nodeDescription.setValue(description);
        showSubgraphVersions.click();
        $x("//div[@title='1.0.0']").shouldBe(Condition.enabled).click();
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setSubgraphVersion(version);
        node.setDescription(description);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Копирование узла графа и сохранение")
    public GraphNodesPage copyNodeAndSave(SubgraphNode node) {
        String cloneName = node.getName()+"_clone";
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        actions().pause(1000)
                .moveToElement($x("//div[@class='g6-grid-container']/following-sibling::canvas"))
                .moveByOffset(0, 60)
                .contextClick()
                .perform();
        copyNode.click();
        nodeName.shouldHave(Condition.exactValue(cloneName));
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Проверка отсутствия узла графа в списке узлов")
    public GraphNodesPage checkNodeNotFound(SubgraphNode node) {
        $x("//div[text()='"+node.getDescription()+"']/..//*[name()='svg' and @class]").shouldBe(Condition.not(Condition.visible));
        return this;
    }

    @Step("Проверка недоступности добавления узла графа")
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

    @Step("Проверка значений атрибутов узла графа")
    public GraphNodesPage checkNodeAttributes(Node node) {
        if (node.getNumber().equals("")) {
            node.setNumber("1");
        }
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        nodeName.shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.shouldHave(Condition.exactValue(node.getDescription()));
        if (node instanceof SubgraphNode) {
            $x("//div[text() = '" + ((SubgraphNode) node).getSubgraphName() + "']").shouldBe(Condition.visible);
        }
        numberInput.shouldHave(Condition.exactValue(node.getNumber()));
        timeoutInput.shouldHave(Condition.exactValue(node.getTimeout()));
        countInput.shouldHave(Condition.exactValue(node.getCount()));
        onPrebillingToggleOn.shouldBe(Condition.visible);
        runOnRollbackToggleOn.shouldBe(Condition.visible);
        holdToggleOn.shouldBe(Condition.visible);
        isSequentialToggleOn.shouldBe(Condition.visible);
        damageOrderOnErrorToggleOn.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    public GraphNodesPage deleteNodeAndSave(Node node) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").click();
        TestUtils.scrollToTheTop();
        deleteNodesButton.click();
        saveGraphWithPatchVersion();
        return this;
    }
}
