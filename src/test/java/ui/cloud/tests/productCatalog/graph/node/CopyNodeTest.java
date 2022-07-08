package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.uiModels.SubgraphNode;

@Epic("Графы")
@Feature("Копирование узла графа")
public class CopyNodeTest extends GraphBaseTest {

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphTests() {
        deleteGraph(NAME);
        deleteGraph(SUBGRAPH_NAME);
    }

    @Test
    @TmsLink("831590")
    @DisplayName("Копирование узла графа")
    @Disabled
    public void copyGraphNode() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .copyNodeAndSave(node);
    }
}
