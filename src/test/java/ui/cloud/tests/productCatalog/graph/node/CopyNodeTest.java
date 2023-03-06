package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.HashMap;
import java.util.UUID;

@Feature("Копирование узла графа")
public class CopyNodeTest extends GraphBaseTest {
    private Graph graph;
    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        graph = super.graph;
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
    @TmsLink("831590")
    @DisplayName("Копирование узла с подграфом")
    public void copyGraphNode() {
        String name = UUID.randomUUID().toString();
        GraphItem node = GraphItem.builder()
                .name(name)
                .description(nodeDescription)
                .subgraphId(subgraph.getGraphId())
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .number(1)
                .timeout(100)
                .count("1")
                .build();
        patchGraphWithGraphItem(graph, node);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .copyNodeAndSave(node);
        node.setName(name + "_clone");
        node.setDescription(nodeDescription + "_clone");
        new GraphNodesPage()
                .checkNodeAttributes(node);
    }

    @Test
    @TmsLink("1055632")
    @DisplayName("Копирование узла с шаблоном")
    public void copyTemplateNode() {
        String name = UUID.randomUUID().toString();
        GraphItem node = GraphItem.builder()
                .templateId(template.getId())
                .name(name)
                .description(nodeDescription)
                .input(new HashMap<String, String>() {{
                    put("input_param", "");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "");
                }})
                .number(1)
                .timeout(100)
                .build();
        patchGraphWithGraphItem(graph, node);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .copyNodeAndSave(node);
        node.setName(name + "_clone");
        node.setDescription(nodeDescription + "_clone");
        new GraphNodesPage()
                .checkNodeAttributes(node);
    }
}
