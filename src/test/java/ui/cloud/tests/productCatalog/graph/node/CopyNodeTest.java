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

@Feature("Копирование узла графа")
public class CopyNodeTest extends GraphBaseTest {

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
    @TmsLink("831590")
    @DisplayName("Копирование узла с подграфом")
    public void copyGraphNode() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description("Тестовый узел")
                .subgraphVersion("Последняя")
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .number(1)
                .timeout(100)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .copyNodeAndSave(node);
    }

    @Test
    @TmsLink("1055632")
    @DisplayName("Копирование узла с шаблоном")
    public void copyTemplateNode() {
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .description("Тестовый узел")
                .templateVersion("Последняя")
                .input(new HashMap<String, String>() {{
                    put("input_param", "");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "");
                }})
                .number(1)
                .timeout(100)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .copyNodeAndSave(node);
    }
}
