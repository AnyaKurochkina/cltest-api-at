package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import steps.productCatalog.TemplateSteps;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("(//div[@class='react-flow']/" +
            "div[contains(@class,'LayoutButtonsStyled')]//*[name()='svg'])[1]");
    private final SelenideElement copyNodeFromGraphButton = $x("(//div[@class='react-flow']/" +
            "div[contains(@class,'LayoutButtonsStyled')]//*[name()='svg'])[3]");
    private final SelenideElement editNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[1]");
    private final SelenideElement copyNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[2]");
    private final SelenideElement deleteNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[3]");
    private final SelenideElement nodeName = $x("//form//input[@name = 'name']");
    private final Input nodeDescription = Input.byXpath("//form//input[@name = 'description']");
    private final SelenideElement formAddNodeButton = $x("//form//div[text() = 'Добавить']//parent::button");
    private final SelenideElement formSaveNodeButton = $x("//form//div[text() = 'Сохранить']//parent::button");
    private final SelenideElement formCancelButton = $x("//form//div[text() = 'Отмена']//ancestor::button");
    private final TextArea inputTextArea = TextArea.byXPath("//form//label[text()='Input']/following::textarea[1]");
    private final TextArea outputTextArea = TextArea.byXPath("//form//label[text()='Output']/following::textarea[1]");
    private final TextArea printedOutputTextArea = TextArea.byLabelContains("Printed output");
    private final Input numberInput = Input.byName("number");
    private final Input timeoutInput = Input.byName("timeout");
    private final Input countInput = Input.byName("count");
    private final Input conditionInput = Input.byName("condition");
    private final SelenideElement forEachInput = $x("//input[@name='for_each']");
    private final SwitchV2 onPrebillingSwitch = SwitchV2.byInputName("on_prebilling");
    private final SwitchV2 runOnRollbackSwitch = SwitchV2.byInputName("run_on_rollback");
    private final SwitchV2 holdSwitch = SwitchV2.byInputName("hold");
    private final SwitchV2 isSequentialSwitch = SwitchV2.byInputName("is_sequential");
    private final SwitchV2 damageOrderOnErrorSwitch =
            SwitchV2.byXPath("//form//input[@name='damage_order_on_error']/ancestor::span[contains(@class, 'switchBase')]");
    private final SelenideElement nameRequiredFieldHint =
            $x("//label[contains(text(),'Название')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement descriptionRequiredFieldHint =
            $x("//label[contains(text(),'Описание')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement incorrectNumberHint = $x("//label[text()='Номер']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement incorrectTimeoutHint =
            $x("//label[text()='Время ожидания, сек']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement nameNonUniqueHint = $x("//div[text()='Узел с данным названием уже существует']");
    private final Select templateVersionSelect = Select.byXpath("(//label[text()='Версия'])[1]/following::div[select]");
    private final Select subgraphVersionSelect = Select.byXpath("(//label[text()='Версия'])[2]/following::div[select]");
    private final Select nodeSelect = Select.byLabel("Узел");
    private final Select graphVersionSelectV2 = Select.byLabel("Версия");
    private final SelenideElement mainTab = $x("//button[text()='Основное']");
    private final SelenideElement additionalTab = $x("//button[text()='Дополнительное']");
    private final SelenideElement paramsTab = $x("//button[text()='Параметры']");
    private final TextArea staticDataTextArea = TextArea.byLabel("Static data");
    private final Input searchNodesInput = Input.byPlaceholder("Поиск...");
    private final Button fitViewButton = Button.byAriaLabel("fit view");
    private final SearchSelect subgraphSelect = SearchSelect.byLabel("Подграф");
    private final SearchSelect templateSelect = SearchSelect.byLabel("Шаблон");
    private final Button fullScreenButton = Button.byAriaLabel("fullscreen");
    private final Button addButton = Button.byText("Добавить");
    private final Select logLevelSelect = Select.byXpath("//label[.='Уровень логирования']/following::div[1]");
    private final SelenideElement logLevelTooltipIcon = $x("//div[text()='Уровень логирования']/following::*[name()='svg'][1]");
    private final SelenideElement inputHint = $x("//label[text()='Input']/following-sibling::p");
    private final SelenideElement outputHint = $x("//label[text()='Output']/following-sibling::p");
    private final SelenideElement printedOutputHint = $x("//label[text()='Printed output ']/following::p");

    @Step("Добавление узла графа '{node.name}' и сохранение графа")
    public GraphNodesPage addNodeAndSave(GraphItem node) {
        //Сериализация, чтобы подтянулись значения из JSON шаблона
        node = JsonHelper.deserialize(node.toJson().toString(), GraphItem.class);
        addNodeButton.click();
        Waiting.sleep(1000);
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        if (!StringUtils.isNullOrEmpty(node.getSubgraphId())) {
            Graph subgraph = GraphSteps.getGraphById(node.getSubgraphId());
            subgraphSelect.setContains(subgraph.getName());
            paramsTab.click();
            Waiting.sleep(1500);
            inputTextArea.setValue(new JSONObject(node.getInput()).toString());
            outputTextArea.setValue(new JSONObject(node.getOutput()).toString());
            additionalTab.click();
            numberInput.setValue(String.valueOf(node.getNumber()));
            timeoutInput.setValue(String.valueOf(node.getTimeout()));
            logLevelSelect.getElement().$x(".//select").shouldBe(Condition.disabled);
        }
        if (!Objects.isNull(node.getTemplateId())) {
            Template template = TemplateSteps.getTemplateById(node.getTemplateId());
            templateSelect.setContains(template.getName());
        }
        additionalTab.click();
        countInput.setValue(String.valueOf(node.getCount()));
        conditionInput.setValue(node.getCondition());
        onPrebillingSwitch.setEnabled(node.getOnPrebilling());
        runOnRollbackSwitch.setEnabled(node.getRunOnRollback());
        holdSwitch.setEnabled(node.getHold());
        isSequentialSwitch.setEnabled(node.getIsSequential());
        damageOrderOnErrorSwitch.setEnabled(node.getDamageOrderOnError());
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        Waiting.sleep(1000);
        return this;
    }

    @Step("Редактирование узла '{node.name}' с подграфом")
    public GraphNodesPage editSubgraphNode(GraphItem node, String version, String description) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        editNodeButton.click();
        nodeDescription.setValue(description);
        subgraphVersionSelect.set(version);
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setSubgraphVersion(version);
        node.setDescription(description);
        Waiting.sleep(1000);
        return this;
    }

    @Step("Редактирование узла '{node.name}' с шаблоном")
    public GraphNodesPage editTemplateNode(GraphItem node, String version, String description) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        editNodeButton.click();
        nodeDescription.setValue(description);
        templateVersionSelect.set(version);
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setTemplateVersion(version);
        node.setDescription(description);
        Waiting.sleep(1000);
        return this;
    }

    @Step("Открытие диалога редактирования узла '{node.name}'")
    public GraphNodesPage openEditDialog(GraphItem node) {
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        editNodeButton.click();
        return this;
    }

    @Step("Копирование узла графа '{node.name}' и сохранение графа")
    public GraphNodesPage copyNodeAndSave(GraphItem node) {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        copyNodeButton.click();
        nodeName.shouldHave(Condition.exactValue(node.getName() + "_clone"));
        Waiting.sleep(500);
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Проверка отсутствия узла '{node.name}' в списке узлов")
    public GraphNodesPage checkNodeNotFound(GraphItem node) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]")
                .shouldBe(Condition.not(Condition.visible));
        return this;
    }

    @Step("Проверка некорректного заполнения полей при добавлении узла")
    public GraphNodesPage checkAddNodeSubgraphDisabled(GraphItem node) {
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        Graph subgraph = GraphSteps.getGraphById(node.getSubgraphId());
        subgraphSelect.setContains(subgraph.getName());
        if (node.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (node.getDescription().isEmpty()) {
            descriptionRequiredFieldHint.shouldBe(Condition.visible);
        }
        additionalTab.click();
        numberInput.setValue(String.valueOf(node.getNumber()));
        timeoutInput.setValue(String.valueOf(node.getTimeout()));
        logLevelSelect.getElement().$x(".//select").shouldBe(Condition.disabled);
        countInput.setValue(String.valueOf(node.getCount()));
        if (node.getNumber().equals(0)) {
            incorrectNumberHint.shouldBe(Condition.visible);
        }
        if (node.getTimeout().equals(0)) {
            incorrectTimeoutHint.shouldBe(Condition.visible);
        }
        formAddNodeButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени узла при добавлении узла")
    public GraphNodesPage checkAddNodeSubgraphWithNonUniqueNameDisabled(GraphItem node) {
        TestUtils.scrollToTheTop();
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nameNonUniqueHint.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка значений атрибутов узла '{node.name}'")
    public GraphNodesPage checkNodeAttributes(GraphItem node) {
        //Сериализация, чтобы подтянулись значения из JSON шаблона
        node = JsonHelper.deserialize(node.toJson().toString(), GraphItem.class);
        if (Objects.isNull(node.getNumber())) node.setNumber(1);
        if (StringUtils.isNullOrEmpty(node.getSubgraphVersion())) node.setSubgraphVersion("Последняя");
        if (StringUtils.isNullOrEmpty(node.getTemplateVersion())) node.setTemplateVersion("Последняя");
        selectNodeInGraph(node);
        editNodeButton.click();
        Waiting.sleep(1000);
        nodeName.shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.getInput().shouldHave(Condition.exactValue(node.getDescription()));
        if (!StringUtils.isNullOrEmpty(node.getSubgraphId())) {
            Graph subgraph = GraphSteps.getGraphById(node.getSubgraphId());
            assertTrue(subgraphSelect.getValue().contains(subgraph.getName()));
            assertEquals(node.getSubgraphVersion(), subgraphVersionSelect.getValue());
        }
        if (!Objects.isNull(node.getTemplateId())) {
            Template template = TemplateSteps.getTemplateById(node.getTemplateId());
            assertTrue(templateSelect.getValue().contains(template.getName()));
            assertEquals(node.getTemplateVersion(), templateVersionSelect.getValue());
        }
        paramsTab.click();
        Waiting.sleep(1500);
        assertEquals(new JSONObject(node.getInput()).toString(),
                inputTextArea.getWhitespacesRemovedValue());
        assertEquals(new JSONObject(node.getOutput()).toString(),
                outputTextArea.getWhitespacesRemovedValue());
        assertEquals(node.getPrintedOutput().toString(), printedOutputTextArea.getWhitespacesRemovedValue());
        additionalTab.click();
        numberInput.getInput().shouldHave(Condition.exactValue(node.getNumber() + ""));
        timeoutInput.getInput().shouldHave(Condition.exactValue(node.getTimeout() + ""));
        countInput.getInput().shouldHave(Condition.exactValue(node.getCount()));
        conditionInput.getInput().shouldHave(Condition.exactValue(node.getCondition()));
        assertEquals(node.getOnPrebilling(), onPrebillingSwitch.isEnabled());
        assertEquals(node.getRunOnRollback(), runOnRollbackSwitch.isEnabled());
        assertEquals(node.getHold(), holdSwitch.isEnabled());
        assertEquals(node.getIsSequential(), isSequentialSwitch.isEnabled());
        assertEquals(node.getDamageOrderOnError(), damageOrderOnErrorSwitch.isEnabled());
        formCancelButton.click();
        return this;
    }

    @Step("Проверка, что узел '{node.name}' найден при поиске '{text}'")
    public GraphNodesPage findNode(String text, GraphItem node) {
        if (Objects.isNull(node.getNumber())) {
            node.setNumber(1);
        }
        searchNodesInput.setValue(text);
        TestUtils.wait(500);
        $x("//div[text()='{}. {} ({})']/..//*[name()='svg' and @class]", node.getNumber(), node.getDescription(), node.getName())
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что узел '{node.name}' не найден при поиске '{text}'")
    public GraphNodesPage checkNodeNotFound(String text, GraphItem node) {
        searchNodesInput.setValue(text);
        Assertions.assertFalse($x("//div[text()='" + node.getNumber() + ". " + node.getDescription() + "']")
                .exists());
        return this;
    }

    @Step("Удаление узла '{node.name}' и сохранение графа")
    public GraphNodesPage deleteNodeAndSave(GraphItem node) {
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        deleteNodeButton.click();
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Задать для StaticData значение '{value}'")
    public GraphNodesPage setStaticData(String value) {
        staticDataTextArea.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    private void selectNodeInGraph(GraphItem node) {
        fitViewButton.getButton().scrollIntoView(scrollCenter).click();
        if (node.getNumber().equals("")) node.setNumber(1);
        $x("//div[@class='react-flow']//div[text()='{}']", node.getDescription()).scrollIntoView(false).click();
    }

    @Step("Открытие диалога добавления узла")
    public GraphNodesPage openAddNodeDialog() {
        addNodeButton.click();
        return this;
    }
}
