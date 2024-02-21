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

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static models.cloud.productCatalog.graph.SourceType.SUBGRAPH;
import static models.cloud.productCatalog.graph.SourceType.TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("(//div[@class='react-flow']/" +
            "div[contains(@class,'LayoutButtonsStyled')]//*[name()='svg'])[1]");
    private final SelenideElement copyNodeFromGraphButton = $x("(//div[@class='react-flow']/" +
            "div[contains(@class,'LayoutButtonsStyled')]//*[name()='svg'])[3]");
    private final Button deleteSelectedButton = Button.byDataTestId("delete-node");
    private final SelenideElement editNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[1]");
    private final SelenideElement copyNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[2]");
    private final SelenideElement deleteNodeButton = $x("(//div[@class='react-flow']//" +
            "div[contains(@class,'ContextMenuIconStyled')]/*[name()='svg'])[3]");
    private final Input nodeName = Input.byXpath("//div[@role='dialog']//input[@name = 'name']");
    private final Input nodeDescription = Input.byXpath("//div[@role='dialog']//input[@name = 'description']");
    private final Button formAddNodeButton = Button.byXpath("//div[@role='dialog']//button[.='Добавить']");
    private final Button formSaveNodeButton = Button.byXpath("//div[@role='dialog']//button[.='Сохранить']");
    private final Button formCancelButton = Button.byXpath("//div[@role='dialog']//button[.='Отмена']");
    private final TextArea inputTextArea = TextArea.byXPath("//div[@role='dialog']//*[text()='Input']/following::textarea[1]");
    private final TextArea outputTextArea = TextArea.byXPath("//div[@role='dialog']//*[text()='Output']/following::textarea[1]");
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
            SwitchV2.byXPath("//div[@role='dialog']//input[@name='damage_order_on_error']/ancestor::span[contains(@class, 'switchBase')]");
    private final SelenideElement nameRequiredFieldHint =
            $x("//label[contains(text(),'Название')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement descriptionRequiredFieldHint =
            $x("//label[contains(text(),'Описание')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement incorrectNumberHint = $x("//label[text()='Номер']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement incorrectTimeoutHint =
            $x("//label[text()='Время ожидания, сек']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement nameNonUniqueHint = $x("//div[text()='Узел с данным названием уже существует']");
    private final Select nodeSelect = Select.byLabel("Узел");
    private final Select graphVersionSelectV2 = Select.byLabel("Версия");
    private final SelenideElement mainTab = $x("//button[text()='Основное']");
    private final SelenideElement additionalTab = $x("//button[text()='Дополнительное']");
    private final SelenideElement paramsTab = $x("//button[text()='Параметры']");
    private final TextArea staticDataTextArea = TextArea.byLabel("Static data");
    private final Input searchNodesInput = Input.byPlaceholder("Поиск...");
    private final Button fitViewButton = Button.byAriaLabel("fit view");
    private final Button fullScreenButton = Button.byAriaLabel("fullscreen");
    private final Button addButton = Button.byText("Добавить");
    private final Select logLevelSelect = Select.byXpath("//label[.='Уровень логирования']/following::div[1]");
    private final SelenideElement logLevelTooltipIcon = $x("//div[text()='Уровень логирования']/following::*[name()='svg'][1]");
    private final SelenideElement inputHint =
            $x("//*[text()='Input']/following::div[contains(@class,'ErrorTextStyled')][1]");
    private final SelenideElement outputHint =
            $x("//*[text()='Output']/following::div[contains(@class,'ErrorTextStyled')][1]");
    private final SelenideElement printedOutputHint =
            $x("//*[text()='Printed output ']/following::div[contains(@class,'ErrorTextStyled')][1]");
    private final Select sourceTypeSelect = Select.byLabel("Тип объекта");
    private final SearchSelect sourceSelect = SearchSelect.byLabel("Объект");
    private final Select sourceVersionSelect = Select.byLabel("Версия");

    public GraphNodesPage() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Step("Добавление узла графа '{node.name}' и сохранение графа")
    public GraphNodesPage addNodeAndSave(GraphItem node) {
        //Сериализация, чтобы подтянулись значения из JSON шаблона
        node = JsonHelper.deserialize(node.toJson().toString(), GraphItem.class);
        addNodeButton.click();
        Waiting.sleep(1000);
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        if (node.getSourceType().equals(SUBGRAPH.getValue())) {
            Graph subgraph = GraphSteps.getGraphById(node.getSourceId());
            sourceTypeSelect.set(SUBGRAPH.getDisplayName());
            sourceSelect.setContains(subgraph.getName());
            paramsTab.click();
            Waiting.sleep(1500);
            inputTextArea.setValue(new JSONObject(node.getInput()).toString());
            outputTextArea.setValue(new JSONObject(node.getOutput()).toString());
            additionalTab.click();
            numberInput.setValue(String.valueOf(node.getNumber()));
            timeoutInput.setValue(String.valueOf(node.getTimeout()));
            logLevelSelect.getElement().$x(".//select").shouldBe(Condition.disabled);
        }
        if (node.getSourceType().equals(TEMPLATE.getValue())) {
            Template template = TemplateSteps.getTemplateById(Integer.parseInt(node.getSourceId()));
            sourceTypeSelect.set(TEMPLATE.getDisplayName());
            sourceSelect.setContains(template.getName());
            Waiting.sleep(2000);
            additionalTab.click();
            runOnRollbackSwitch.setEnabled(node.getRunOnRollback());
        }
        countInput.setValue(String.valueOf(node.getCount()));
        conditionInput.setValue(node.getCondition());
        onPrebillingSwitch.setEnabled(node.getOnPrebilling());
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
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        editNodeButton.click();
        nodeDescription.setValue(description);
        sourceVersionSelect.set(version);
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setSourceVersion(version);
        node.setDescription(description);
        Waiting.sleep(1000);
        return this;
    }

    @Step("Редактирование узла '{node.name}' с шаблоном")
    public GraphNodesPage editTemplateNode(GraphItem node, String version, String description) {
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        editNodeButton.click();
        nodeDescription.setValue(description);
        sourceVersionSelect.set(version);
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setSourceVersion(version);
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
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        copyNodeButton.click();
        nodeName.getInput().shouldHave(Condition.exactValue(node.getName() + "_clone"));
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
        Graph subgraph = GraphSteps.getGraphById(node.getSourceId());
        sourceTypeSelect.setContains(SUBGRAPH.getDisplayName());
        sourceSelect.setContains(subgraph.getName());
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
        formAddNodeButton.getButton().shouldBe(Condition.disabled);
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
        if (StringUtils.isNullOrEmpty(node.getSourceVersion())) node.setSourceVersion(CALCULATED_VERSION_TITLE);
        selectNodeInGraph(node);
        editNodeButton.click();
        Waiting.sleep(1000);
        nodeName.getInput().shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.getInput().shouldHave(Condition.exactValue(node.getDescription()));
        if (node.getSourceType().equals(SUBGRAPH.getValue())) {
            Graph subgraph = GraphSteps.getGraphById(node.getSourceId());
            assertEquals(SUBGRAPH.getDisplayName(), sourceTypeSelect.getValue());
            Waiting.find(() -> sourceSelect.getValue().contains(subgraph.getName()), Duration.ofSeconds(3));
            assertEquals(node.getSourceVersion(), sourceVersionSelect.getValue());
        }
        if (node.getSourceType().equals(TEMPLATE.getValue())) {
            Template template = TemplateSteps.getTemplateById(Integer.parseInt(node.getSourceId()));
            assertEquals(TEMPLATE.getDisplayName(), sourceTypeSelect.getValue());
            Waiting.find(() -> sourceSelect.getValue().contains(template.getName()), Duration.ofSeconds(3));
            assertEquals(node.getSourceVersion(), sourceVersionSelect.getValue());
        }
        paramsTab.click();
        Waiting.sleep(1500);
        assertEquals(new JSONObject(node.getInput()).toString(),
                inputTextArea.getWhitespacesRemovedValue());
        assertEquals(new JSONObject(node.getOutput()).toString(),
                outputTextArea.getWhitespacesRemovedValue());
        assertEquals(node.getPrintedOutput().toString(), printedOutputTextArea.getWhitespacesRemovedValue());
        additionalTab.click();
        numberInput.getInput().shouldHave(Condition.exactValue(String.valueOf(node.getNumber())));
        timeoutInput.getInput().shouldHave(Condition.exactValue(String.valueOf(node.getTimeout())));
        countInput.getInput().shouldHave(Condition.exactValue(node.getCount()));
        conditionInput.getInput().shouldHave(Condition.exactValue(node.getCondition()));
        assertEquals(node.getOnPrebilling(), onPrebillingSwitch.isEnabled());
        if (node.getSourceType().equals(TEMPLATE.getValue()))
            assertEquals(node.getRunOnRollback(), runOnRollbackSwitch.isEnabled());
        assertEquals(node.getHold(), holdSwitch.isEnabled());
        assertEquals(node.getIsSequential(), isSequentialSwitch.isEnabled());
        assertEquals(node.getDamageOrderOnError(), damageOrderOnErrorSwitch.isEnabled());
        formCancelButton.click();
        return this;
    }

    @Step("Проверка, что узел '{node.name}' найден при поиске по параметру '{param}'")
    public GraphNodesPage findNodeByParam(String param, GraphItem node) {
        findNode(param, node);
        Waiting.find(
                () -> $x("//span[text()='\"{}\"']", param.toLowerCase()).isDisplayed()
                , Duration.ofSeconds(3),
                "Параметр " + param + " не найден");
        return this;
    }

    @Step("Проверка, что узел '{node.name}' найден при поиске по '{value}'")
    public GraphNodesPage findNode(String value, GraphItem node) {
        if (Objects.isNull(node.getNumber())) node.setNumber(1);
        searchNodesInput.setValue(value);
        Waiting.find(
                () -> $x("//div[text()='{}. {} ({})']/..//*[name()='svg' and @class]", node.getNumber(),
                        node.getDescription(), node.getName()).isDisplayed()
                , Duration.ofSeconds(3),
                "Узел " + node.getName() + " не найден");
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

    @Step("Удаление узла '{node.name}' по кнопке 'Удалить выделенные' и сохранение графа")
    public GraphNodesPage deleteSelectedNodeAndSave(GraphItem node) {
        generalInfoTab.getElement().scrollIntoView(true);
        fitViewButton.click();
        selectNodeInGraph(node);
        deleteSelectedButton.click();
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
