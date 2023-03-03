package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.GraphItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.HashMap;

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
        deleteGraphByApi(NAME);
        deleteGraphByApi(SUBGRAPH_NAME);
        deleteTemplate(TEMPLATE_NAME);
    }

    @Test
    @TmsLink("894894")
    @DisplayName("Редактирование узла графа с подграфом")
    public void editNodeSubgraphTest() {
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
                .count("")
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
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
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .templateVersion("Последняя")
                .description("Тестовый узел")
                .timeout(100)
                .number(1)
                .count("")
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node);
        node.setInput(new HashMap<String, String>() {{
            put("input_param", "");
        }});
        node.setOutput(new HashMap<String, Object>() {{
            put("output_param", "");
        }});
        new GraphNodesPage()
                .editTemplateNode(node, "1.0.0", "edit")
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }
}
