package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.GraphItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.HashMap;

@Feature("Удаление узла графа")
public class DeleteNodeTest extends GraphBaseTest {

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
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
                .subgraphVersion("Последняя")
                .description("Тестовый узел")
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .timeout(100)
                .number(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .deleteNodeAndSave(node)
                .checkNodeNotFound(node);
    }
}
