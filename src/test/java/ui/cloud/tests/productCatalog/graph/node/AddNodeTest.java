package ui.cloud.tests.productCatalog.graph.node;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
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

@Feature("Добавление узла графа")
public class AddNodeTest extends GraphBaseTest {

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
    @TmsLink("489507")
    @DisplayName("Добавление узла графа с подграфом")
    public void addNodeSubgraphTest() {
        addNodeSubgraphWithRequiredParameters();
        addNodeSubgraphWithAllParameters();
        addNodeSubgraphWithoutRequiredParameters();
        addNodeSubgraphWithIncorrectParameters();
        addNodeSubgraphWithNonUniqueName();
    }

    @Step("Добавление узла графа (подграф) с указанием обязательных параметров")
    public void addNodeSubgraphWithRequiredParameters() {
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
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Step("Добавление узла графа (подграф) с указанием всех параметров")
    public void addNodeSubgraphWithAllParameters() {
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
                .number(3)
                .timeout(10)
                .count("2")
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Step("Добавление узла без заполнения обязательных полей")
    public void addNodeSubgraphWithoutRequiredParameters() {
        GraphItem node = GraphItem.builder()
                .name("")
                .description("test")
                .number(1)
                .timeout(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
        node = GraphItem.builder()
                .name("test")
                .description("")
                .number(1)
                .timeout(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с указанием некорректных значений параметров")
    public void addNodeSubgraphWithIncorrectParameters() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description("Тестовый узел")
                .subgraphVersion("Последняя")
                .number(0)
                .timeout(0)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .checkAddNodeSubgraphDisabled(node);
    }

    @Step("Добавление узла графа (подграф) с неуникальным именем")
    public void addNodeSubgraphWithNonUniqueName() {
        GraphItem node = GraphItem.builder()
                .name(SUBGRAPH_NAME)
                .description("Тестовый узел")
                .subgraphVersion("Последняя")
                .number(1)
                .timeout(1)
                .build();
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node)
                .checkAddNodeSubgraphWithNonUniqueNameDisabled(node);
    }

    @Test
    @TmsLink("883206")
    @DisplayName("Добавление узла графа с шаблоном")
    public void addNodeByTemplateTest() {
        GraphItem node = GraphItem.builder()
                .name(TEMPLATE_NAME)
                .description("Тестовый узел")
                .templateVersion("Последняя")
                .timeout(100)
                .number(1)
                .count("1")
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
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }
}
