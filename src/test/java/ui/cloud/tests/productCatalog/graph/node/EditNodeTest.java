package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.uiModels.SubgraphNode;
import ui.uiModels.TemplateNode;

@Epic("Графы")
@Feature("Редактирование узла графа")
public class EditNodeTest extends GraphBaseTest {

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
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
    @TmsLink("894894")
    @DisplayName("Редактирование узла графа с подграфом")
    public void editNodeSubgraphTest() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .editSubgraphNode(node, "1.0.0", "edit")
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Test
    @TmsLink("490080")
    @DisplayName("Редактирование узла графа с шаблоном")
    public void editTemplateNodeTest() {
        TemplateNode node = new TemplateNode(TEMPLATE_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .editTemplateNode(node, "1.0.0", "edit")
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }
}
