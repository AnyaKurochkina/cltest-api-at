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
        deleteGraphByApi(NAME);
        deleteGraphByApi(SUBGRAPH_NAME);
        deleteTemplate(TEMPLATE_NAME);
    }

    @Test
    @TmsLink("802404")
    @DisplayName("Просмотр входных и выходных параметров узлов")
    public void addNodeByTemplateTest() {
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .templateVersion("Последняя")
                .description("Тестовый узел")
                .input(new HashMap<String, String>() {{
                    put("input_param", "{}");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "{}");
                }})
                .timeout(100)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .findNode(node.getInput().keySet().toArray()[0].toString(), node)
                .findNode(node.getOutput().keySet().toArray()[0].toString(), node);
    }

    @Test
    @TmsLink("1058280")
    @DisplayName("Поиск по узлам графа")
    public void findNodesTest() {
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
                .findNode(node.getName().toUpperCase(), node)
                .findNode(node.getDescription(), node)
                .findNode(node.getInput().keySet().toArray()[0].toString(), node)
                .findNode(node.getInput().keySet().toArray()[0].toString().toUpperCase(), node)
                .findNode(node.getInput().get("output_param"), node)
                .checkNodeNotFound("incorrect_param", node);
    }
}
