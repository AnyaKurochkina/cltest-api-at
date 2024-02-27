package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.Arrays;
import java.util.HashMap;

import static models.cloud.productCatalog.graph.SourceType.TEMPLATE;
import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static steps.productCatalog.TemplateSteps.partialUpdateTemplate;

@Feature("Добавление узла графа")
public class NodesListTest extends GraphBaseTest {
    private Graph graph;
    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа и шаблона для узлов графа")
    public void setUpForGraphNodeTests() {
        graph = super.graph;
        subgraph = createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
        template = createTemplate(TEMPLATE_NAME);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphTests() {
        deleteGraphByApi(NAME);
        deleteGraphByApi(SUBGRAPH_NAME);
        deleteTemplate(TEMPLATE_NAME);
    }

    @Test
    @TmsLink("802404")
    @DisplayName("Просмотр входных и выходных параметров узлов, поиск в узлах графа")
    public void viewNodeParamsTest() {
        partialUpdateTemplate(template.getId(), new JSONObject().put("additional_input", true)
                .put("additional_output", true));
        GraphItem node1 = GraphItem.builder()
                .name("node_1")
                .sourceType(TEMPLATE.getValue())
                .sourceId(String.valueOf(template.getId()))
                .description("node_1")
                .input(new HashMap<String, String>() {{
                    put("input_param", "{}");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "{}");
                }})
                .timeout(1)
                .build();
        GraphItem node2 = GraphItem.builder()
                .name("node_2")
                .sourceType(TEMPLATE.getValue())
                .sourceId(String.valueOf(template.getId()))
                .description("node_2")
                .input(new HashMap<String, String>() {{
                    put("input_param_2", "{}");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param_2", "{}");
                }})
                .timeout(1)
                .build();
        JSONObject graphItemsJSON = new JSONObject().put("graph", Arrays.asList(node1.toJson(), node2.toJson()));
        partialUpdateGraph(graph.getGraphId(), graphItemsJSON);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .findNode(node2.getInput().keySet().toArray()[0].toString(), node2)
                .findNode(node2.getOutput().keySet().toArray()[0].toString(), node2);
    }

    @Test
    @TmsLink("1058280")
    @DisplayName("Поиск по узлам графа")
    public void findNodesTest() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .sourceId(subgraph.getGraphId())
                .sourceType("subgraph")
                .description(nodeDescription)
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .timeout(1)
                .number(1)
                .build();
        patchGraphWithGraphItem(graph, node);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .findNode(node.getName().toUpperCase(), node)
                .findNode(node.getDescription(), node)
                .findNodeByParam(node.getInput().keySet().toArray()[0].toString(), node)
                .findNodeByParam(node.getOutput().keySet().toArray()[0].toString().toUpperCase(), node)
                .findNodeByParam(node.getOutput().get("output_param").toString(), node)
                .checkNodeNotFound("incorrect_param", node);
    }
}
