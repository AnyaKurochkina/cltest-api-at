package ui.cloud.tests.productCatalog.graph.node;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.enums.LogLevel;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.template.Template;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.elements.Tooltip;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void addNodeSubgraphTest() {
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
                .subgraphId(subgraph.getGraphId())
                .subgraphVersion("Последняя")
                .number(1)
                .build();
        new IndexPage().goToGraphsPage()
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
                .subgraphId(subgraph.getGraphId())
                .subgraphVersion("Последняя")
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
        new IndexPage().goToGraphsPage()
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
                .subgraphId(subgraph.getGraphId())
                .number(1)
                .timeout(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
        node = GraphItem.builder()
                .name("test")
                .description("")
                .subgraphId(subgraph.getGraphId())
                .number(1)
                .timeout(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с указанием некорректных значений параметров")
    public void addSubgraphNodeWithIncorrectParameters() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .subgraphId(subgraph.getGraphId())
                .subgraphVersion("Последняя")
                .number(0)
                .timeout(0)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с неуникальным именем")
    public void addSubgraphNodeWithNonUniqueName() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .subgraphId(subgraph.getGraphId())
                .subgraphVersion("Последняя")
                .build();
        new IndexPage().goToGraphsPage()
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
                .templateId(template.getId())
                .templateVersion("Последняя")
                .timeout(100)
                .number(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node);
        node.setInput(new HashMap<String, String>() {{
            put("input_param", "");
        }});
        node.setOutput(new HashMap<String, Object>() {{
            put("output_param", "");
        }});
        new GraphNodesPage()
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Test
    @TmsLink("883403")
    @DisplayName("Добавление узла с разрешённым переопределением уровня логирования")
    public void addNodeWithAllowedLogLevelOverride() {
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new IndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getTemplateSelect().setContains(TEMPLATE_NAME);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.enabled);
        assertEquals(LogLevel.EMPTY.getDisplayName(), page.getLogLevelSelect().getValue());
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
        String templateName = UUID.randomUUID().toString();
        Template.builder()
                .name(templateName)
                .title(TEMPLATE_TITLE)
                .type("system_nodes")
                .run("internal")
                .logLevel(LogLevel.SHORT.getValue())
                .logCanBeOverridden(false)
                .timeout(1)
                .build()
                .createObject();
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new IndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getTemplateSelect().setContains(templateName);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.disabled);
        page.getLogLevelTooltipIcon().hover();
        assertTrue(new Tooltip().getElement().getText().equals("Поле можно редактировать только для узла с параметром " +
                "\"Название очереди для старта задачи\" = \"internal\" и включенным переопределением уровня логирования"));
        Waiting.find(() -> page.getLogLevelSelect().getValue().equals(LogLevel.SHORT.getDisplayName()), Duration.ofSeconds(3));
        page.getFormAddNodeButton().click();
        page.saveGraphWithPatchVersion();
        page.openEditDialog(node);
        page.getAdditionalTab().click();
        page.getLogLevelSelect().getElement().$x(".//select").shouldBe(Condition.disabled);
        Waiting.find(() -> page.getLogLevelSelect().getValue().equals(LogLevel.SHORT.getDisplayName()), Duration.ofSeconds(3));
    }
}
