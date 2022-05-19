package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class AddNodeTest extends GraphBaseTest {

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