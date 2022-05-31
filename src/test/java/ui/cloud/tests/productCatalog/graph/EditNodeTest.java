package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.SubgraphNode;

@Epic("Графы")
@Feature("Редактирование узла графа")
public class EditNodeTest extends GraphBaseTest {

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
    @TmsLink("894894")
    @DisplayName("Редактирование узла графа (подграф)")
    public void editNodeSubgraphTest() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .editNodeSubgraph(node, "1.0.0", "edit")
                .checkNodeAttributes(node)
                .deleteNode(node);
    }
}
