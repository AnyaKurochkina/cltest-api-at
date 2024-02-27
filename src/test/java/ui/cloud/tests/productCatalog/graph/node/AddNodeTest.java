package ui.cloud.tests.productCatalog.graph.node;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.enums.LogLevel;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.elements.Tooltip;

import java.time.Duration;
import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$x;
import static models.cloud.productCatalog.graph.SourceType.SUBGRAPH;
import static models.cloud.productCatalog.graph.SourceType.TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.cloud.pages.productCatalog.EntityPage.CALCULATED_VERSION_TITLE;

@Feature("Добавление узла графа")
public class AddNodeTest extends GraphBaseTest {

    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа и шаблона для узлов графа")
    public void setUpForGraphNodeTests() {
        subgraph = createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
        createTemplate(TEMPLATE_NAME);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphTests() {
        deleteGraphByApi(NAME);
        deleteGraphByApi(SUBGRAPH_NAME);
        deleteTemplate(TEMPLATE_NAME);
    }

    @Test
    @TmsLink("489507")
    @DisplayName("Добавление узла графа с подграфом")
    public void addSubgraphNodeTest() {
        addSubgraphNodeWithRequiredParameters();
        addSubgraphNodeWithAllParameters();
        addSubgraphNodeWithoutRequiredParameters();
        addSubgraphNodeWithIncorrectParameters();
        addSubgraphNodeWithNonUniqueName();
    }

    @Step("Добавление узла графа (подграф) с указанием обязательных параметров")
    public void addSubgraphNodeWithRequiredParameters() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .sourceType(SUBGRAPH.getValue())
                .sourceId(subgraph.getGraphId())
                .sourceVersion(CALCULATED_VERSION_TITLE)
                .number(1)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Step("Добавление узла графа (подграф) с указанием всех параметров")
    public void addSubgraphNodeWithAllParameters() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .sourceType(SUBGRAPH.getValue())
                .sourceId(subgraph.getGraphId())
                .sourceVersion(CALCULATED_VERSION_TITLE)
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .number(3)
                .timeout(10)
                .count("quantity")
                .condition("storage_profile == 'SSD'")
                .onPrebilling(true)
                .runOnRollback(true)
                .hold(true)
                .isSequential(true)
                .damageOrderOnError(true)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Step("Добавление узла без заполнения обязательных полей")
    public void addSubgraphNodeWithoutRequiredParameters() {
        GraphItem node = GraphItem.builder()
                .name("")
                .description("test")
                .sourceType(SUBGRAPH.getValue())
                .sourceId(subgraph.getGraphId())
                .number(1)
                .timeout(1)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
        node = GraphItem.builder()
                .name("test")
                .description("")
                .sourceId(subgraph.getGraphId())
                .sourceType("subgraph")
                .number(1)
                .timeout(1)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с указанием некорректных значений параметров")
    public void addSubgraphNodeWithIncorrectParameters() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .sourceType(SUBGRAPH.getValue())
                .sourceId(subgraph.getGraphId())
                .sourceVersion(CALCULATED_VERSION_TITLE)
                .number(0)
                .timeout(0)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с неуникальным именем")
    public void addSubgraphNodeWithNonUniqueName() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .sourceType(SUBGRAPH.getValue())
                .sourceId(subgraph.getGraphId())
                .sourceVersion(CALCULATED_VERSION_TITLE)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .checkAddNodeSubgraphWithNonUniqueNameDisabled(node);
    }

    @Test
    @TmsLink("883206")
    @DisplayName("Добавление узла графа с шаблоном")
    public void addNodeByTemplateTest() {
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .description(nodeDescription)
                .sourceId(String.valueOf(template.getId()))
                .sourceType(TEMPLATE.getValue())
                .sourceVersion(CALCULATED_VERSION_TITLE)
                .timeout(100)
                .number(1)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node);
        node.setInput(new HashMap<String, String>() {{
            put("input_param", "");
        }});
        node.setOutput(new HashMap<String, Object>() {{
            put("output_param", "");
        }});
        node.setPrintedOutput(printedOutputValue);
        new GraphNodesPage()
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Test
    @TmsLink("883403")
    @DisplayName("Добавление узла с разрешённым переопределением уровня логирования")
    public void addNodeWithAllowedLogLevelOverride() {
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("log_can_be_overridden", true));
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getSourceTypeSelect().set(TEMPLATE.getDisplayName());
        page.getSourceSelect().setContains(TEMPLATE_NAME);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.enabled);
        assertEquals(LogLevel.SHORT.getDisplayName(), page.getLogLevelSelect().getValue());
        page.getLogLevelSelect().set(LogLevel.FULL.getDisplayName());
        page.getFormAddNodeButton().click();
        page.saveGraphWithPatchVersion();
        page.openEditDialog(node);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.enabled);
        Waiting.find(() -> page.getLogLevelSelect().getValue().equals(LogLevel.FULL.getDisplayName()), Duration.ofSeconds(3));
    }

    @Test
    @TmsLink("1532218")
    @DisplayName("Добавление узла с запрещённым переопределением уровня логирования")
    public void addNodeWithForbiddenLogLevelOverride() {
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getSourceTypeSelect().set(TEMPLATE.getDisplayName());
        page.getSourceSelect().setContains(TEMPLATE_NAME);
        Waiting.find(() -> !$x("//button[text()='Дополнительное']/div[@role='alert']").isDisplayed(),
                Duration.ofSeconds(3));
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.disabled);
        page.getLogLevelTooltipIcon().hover();
        assertEquals("Поле можно редактировать только для узла с параметром " +
                        "\"Название очереди для старта задачи\" = \"internal\" и включенным переопределением уровня логирования",
                new Tooltip().getElement().getText());
        Waiting.find(() -> page.getLogLevelSelect().getValue().equals(LogLevel.SHORT.getDisplayName()), Duration.ofSeconds(3));
        page.getFormAddNodeButton().click();
        page.saveGraphWithPatchVersion();
        page.openEditDialog(node);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.disabled);
        Waiting.find(() -> page.getLogLevelSelect().getValue().equals(LogLevel.SHORT.getDisplayName()), Duration.ofSeconds(3));
    }

    @Test
    @TmsLink("1035648")
    @DisplayName("Добавление узла с запрещённым переопределением Input, Output, Printed output")
    public void addNodeWithForbiddenParamsOverride() {
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("printed_output_can_be_overridden", false));
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getSourceTypeSelect().set(TEMPLATE.getDisplayName());
        page.getSourceSelect().setContains(TEMPLATE_NAME);
        Waiting.sleep(2000);
        page.getParamsTab().click();
        page.getInputTextArea().setValue("{\"override_param_1\":\"1\"}");
        assertEquals("Свойство \"override_param_1\" отсутствует в шаблоне (переопределение запрещено)",
                page.getInputHint().getText());
        page.getOutputTextArea().setValue("{\"override_param_2\":\"1\"}");
        assertEquals("Свойство \"override_param_2\" отсутствует в шаблоне (переопределение запрещено)",
                page.getOutputHint().getText());
        $x("//*[text()='Printed output (Переопределение Printed output запрещено в шаблоне)']")
                .shouldBe(Condition.visible);
        assertEquals("{}", page.getPrintedOutputTextArea().getWhitespacesRemovedValue());
    }

    @Test
    @TmsLink("1536653")
    @DisplayName("Добавление узла с разрешенным переопределением Input, Output, Printed output")
    public void addNodeWithAllowedParamsOverride() {
        String inputValue = "{\"override_param_1\":\"1\"}";
        String outputValue = "{\"override_param_2\":\"1\"}";
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_input", true)
                .put("additional_output", true));
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getSourceTypeSelect().set(TEMPLATE.getDisplayName());
        page.getSourceSelect().setContains(TEMPLATE_NAME);
        Waiting.sleep(2000);
        page.getParamsTab().click();
        page.getInputTextArea().setValue(inputValue);
        page.getInputHint().shouldNotBe(Condition.visible);
        page.getOutputTextArea().setValue(outputValue);
        page.getOutputHint().shouldNotBe(Condition.visible);
        $x("//*[text()='Printed output ']").shouldBe(Condition.visible);
        page.getPrintedOutputTextArea().setValue("{}");
        page.getFormAddNodeButton().click();
        page.saveGraphWithPatchVersion();
        page.openEditDialog(node);
        page.getParamsTab().click();
        assertEquals(inputValue, page.getInputTextArea().getWhitespacesRemovedValue());
        assertEquals(outputValue, page.getOutputTextArea().getWhitespacesRemovedValue());
        assertEquals("{}", page.getPrintedOutputTextArea().getWhitespacesRemovedValue());
        page.getMainTab().click();
        //TODO баг, что не сразу отображается текст
        Waiting.findWithAction(() -> $x("//div[text()='Параметры в \"Input\", \"Output\" узла не совпадают с параметрами шаблона']")
                        .isDisplayed(),
                () -> {
                    page.getParamsTab().click();
                    page.getMainTab().click();
                }, Duration.ofSeconds(3));
    }
}
