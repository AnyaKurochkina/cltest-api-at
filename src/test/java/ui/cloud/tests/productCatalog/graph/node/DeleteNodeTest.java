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
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

@Feature("Удаление узла графа")
public class DeleteNodeTest extends GraphBaseTest {
    private Graph graph;
    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        graph = super.graph;
        subgraph = createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphTests() {
        deleteGraphByApi(NAME);
        deleteGraphByApi(SUBGRAPH_NAME);
    }

    @Test
    @TmsLink("490447")
    @DisplayName("Удаление узла графа")
    public void deleteGraphNode() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description(nodeDescription)
                .subgraphId(subgraph.getGraphId())
                .number(1)
                .build();
        patchGraphWithGraphItem(graph, node);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .deleteNodeAndSave(node)
                .checkNodeNotFound(node);
    }
}
