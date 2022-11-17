package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.models.SubgraphNode;
import ui.models.TemplateNode;

@Feature("Добавление узла графа")
public class NodesListTest extends GraphBaseTest {

    @BeforeEach
    @DisplayName("Создание подграфа и шаблона для узлов графа")
    public void setUpForGraphNodeTests() {
        createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
        createTemplate(TEMPLATE_NAME);
    }

    @AfterEach
    @DisplayName("Удаление подграфа")
    public void tearDownForGraphTests() {
        deleteGraph(NAME);
        deleteGraph(SUBGRAPH_NAME);
        deleteTemplate(TEMPLATE_NAME);
    }

    @Test
    @TmsLink("802404")
    @DisplayName("Просмотр входных и выходных параметров узлов")
    public void addNodeByTemplateTest() {
        TemplateNode node = new TemplateNode(TEMPLATE_NAME);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .findNode(node.getInputKey(), node)
                .findNode(node.getOutputKey(), node);
    }

    @Test
    @TmsLink("1058280")
    @DisplayName("Поиск по узлам графа")
    public void findNodesTest() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .findNode(node.getInputKey(), node)
                .findNode(node.getOutputKey().toUpperCase(), node)
                .findNode(node.getInputValue(), node)
                .checkNodeNotFound("incorrect_param", node);
    }
}
