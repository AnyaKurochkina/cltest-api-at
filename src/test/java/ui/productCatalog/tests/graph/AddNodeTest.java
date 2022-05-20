package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        new MainPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph("node_name","node_desctription", SUBGRAPH_NAME)
                .checkNodeAttributes(SUBGRAPH_NAME,"node_name","node_desctription");
    }
}
