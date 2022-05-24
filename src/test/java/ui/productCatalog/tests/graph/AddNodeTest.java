package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.models.SubgraphNode;
import ui.productCatalog.pages.MainPage;

public class AddNodeTest extends GraphBaseTest {

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        createGraph(SUBGRAPH_NAME);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphsTest() {
        deleteGraph(NAME);
        deleteGraph(SUBGRAPH_NAME);
    }

    @Test
    @DisplayName("Добавление узла графа (подграф) с указанием обязательных параметров")
    public void addNodeSubgraphWithRequiredParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .checkNodeAttributes(node);
    }

    @Test
    @DisplayName("Добавление узла графа (подграф) с указанием всех параметров")
    public void addNodeSubgraphWithAllParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        node.setOutput("{\"out_param\":\"test_value\"}");
        node.setNumber("3");
        node.setTimeout("10");
        node.setCount("2");
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .checkNodeAttributes(node);
    }
}
