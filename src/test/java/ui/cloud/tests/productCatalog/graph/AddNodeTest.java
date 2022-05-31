package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.SubgraphNode;

@Epic("Графы")
@Feature("Добавление узла графа")
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
    @TmsLink("489507")
    @DisplayName("Добавление узла графа (подграф)")
    public void addNodeSubgraphTest() {
        addNodeSubgraphWithRequiredParameters();
        addNodeSubgraphWithAllParameters();
        addNodeSubgraphWithoutRequiredParameters();
        addNodeSubgraphWithIncorrectParameters();
        addNodeSubgraphWithNonUniqueName();
    }

    @Step("Добавление узла графа (подграф) с указанием обязательных параметров")
    public void addNodeSubgraphWithRequiredParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .checkNodeAttributes(node)
                .deleteNode(node);
    }

    @Step("Добавление узла графа (подграф) с указанием всех параметров")
    public void addNodeSubgraphWithAllParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        node.setOutput("{\"out_param\":\"test_value\"}");
        node.setNumber("3");
        node.setTimeout("10");
        node.setCount("2");
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .checkNodeAttributes(node)
                .deleteNode(node);
    }

    @Step("Добавление узла без заполнения обязательных полей")
    public void addNodeSubgraphWithoutRequiredParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        node.setName("");
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
        node.setName("test_node");
        node.setDescription("");
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
        node.setDescription("test_description");
        node.setSubgraphName("");
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с указанием некорректных значений параметров")
    public void addNodeSubgraphWithIncorrectParameters() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        node.setNumber("0");
        node.setTimeout("0");
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с неуникальным именем")
    public void addNodeSubgraphWithNonUniqueName() {
        SubgraphNode node = new SubgraphNode(SUBGRAPH_NAME);
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .addNodeSubgraph(node)
                .checkAddNodeSubgraphWithNonUniqueNameDisabled(node);
    }
}
