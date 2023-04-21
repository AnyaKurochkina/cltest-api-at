package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.HashMap;

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
    @DisplayName("Просмотр входных и выходных параметров узлов")
    public void addNodeByTemplateTest() {
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .templateId(template.getId())
                .description("Тестовый узел")
                .input(new HashMap<String, String>() {{
                    put("input_param", "{}");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "{}");
                }})
                .timeout(1)
                .build();
        patchGraphWithGraphItem(graph, node);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .findNode(node.getInput().keySet().toArray()[0].toString(), node)
                .findNode(node.getOutput().keySet().toArray()[0].toString(), node);
    }

    @Test
    @TmsLink("1058280")
    @DisplayName("Поиск по узлам графа")
    public void findNodesTest() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .subgraphId(subgraph.getGraphId())
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
                .findNode(node.getInput().keySet().toArray()[0].toString(), node)
                .findNode(node.getOutput().keySet().toArray()[0].toString().toUpperCase(), node)
                .findNode(node.getOutput().get("output_param").toString(), node)
                .checkNodeNotFound("incorrect_param", node);
    }
}
