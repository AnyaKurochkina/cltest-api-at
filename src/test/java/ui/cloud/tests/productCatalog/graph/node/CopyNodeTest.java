package ui.cloud.tests.productCatalog.graph.node;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.elements.Button;

import java.util.HashMap;
import java.util.UUID;

import static core.helper.StringUtils.$x;

@Feature("Копирование узла графа")
public class CopyNodeTest extends GraphBaseTest {
    private Graph graph;
    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        graph = super.graph;
        subgraph = createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
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
        String name = UUID.randomUUID().toString();
        GraphItem node = GraphItem.builder()
                .name(name)
                .description(nodeDescription)
                .sourceId(subgraph.getGraphId())
                .sourceType("subgraph")
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .number(1)
                .timeout(100)
                .count("1")
                .build();
        patchGraphWithGraphItem(graph, node);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .copyNodeAndSave(node);
        node.setName(name + "_clone");
        node.setDescription(nodeDescription + "_clone");
        new GraphNodesPage()
                .checkNodeAttributes(node);
    }

    @Test
    @TmsLink("1055632")
    @DisplayName("Копирование узла с шаблоном")
    public void copyTemplateNode() {
        String name = UUID.randomUUID().toString();
        GraphItem node = GraphItem.builder()
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .name(name)
                .description(nodeDescription)
                .input(new HashMap<String, String>() {{
                    put("input_param", "");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "");
                }})
                .number(1)
                .timeout(100)
                .build();
        patchGraphWithGraphItem(graph, node);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .copyNodeAndSave(node);
        node.setName(name + "_clone");
        node.setDescription(nodeDescription + "_clone");
        new GraphNodesPage()
                .checkNodeAttributes(node);
    }

    @Test
    @TmsLink("1349326")
    @DisplayName("Копирование узла из другого графа")
    public void copyNodeFromGraph() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
        String nodeName = UUID.randomUUID().toString();
        GraphItem node = GraphItem.builder()
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .name(nodeName)
                .description(nodeDescription)
                .input(new HashMap<String, String>() {{
                    put("input_param", "");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "");
                }})
                .number(1)
                .timeout(100)
                .build();
        patchGraphWithGraphItem(graph, node);
        String graph2Name = UUID.randomUUID().toString();
        createGraph(graph2Name, "AT UI Graph (copy node test)");
        new ControlPanelIndexPage().goToGraphsPage()
                .openGraphPage(graph2Name)
                .goToNodesTab()
                .getCopyNodeFromGraphButton()
                .click();
        GraphNodesPage page = new GraphNodesPage();
        page.getGraphSelect().setContains(graph.getName());
        Waiting.sleep(1000);
        page.getGraphVersionSelectV2().set("1.0.1");
        page.getNodeSelect().setContains(node.getName());
        $x("//form//span[text()='{}']", node.getDescription()).shouldBe(Condition.visible);
        Button.byText("Input").click();
        $x("//form//span[text()='\"{}\"']", "input_param").shouldBe(Condition.visible);
        page.getAddButton().click();
        node.setName(node.getName() + "_clone");
        page.checkNodeAttributes(node);
        page.saveGraphWithPatchVersion();
        deleteGraphByApi(graph2Name);
    }
}
