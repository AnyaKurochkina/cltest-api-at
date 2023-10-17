package ui.cloud.tests.productCatalog.graph.node;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphNodesPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;
import ui.elements.Alert;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Редактирование узла графа")
public class EditNodeTest extends GraphBaseTest {
    private Graph graph;
    private Graph subgraph;

    @BeforeEach
    @DisplayName("Создание подграфа для узла графа")
    public void setUpForGraphNodesTest() {
        graph = super.graph;
        subgraph = createGraph(SUBGRAPH_NAME, SUBGRAPH_TITLE);
        template = createTemplate(TEMPLATE_NAME);
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
                .description("Тестовый узел")
                .sourceId(subgraph.getGraphId())
                .sourceType("subgraph")
                .input(new HashMap<String, String>() {{
                    put("input_param", "test_value_1");
                }})
                .output(new HashMap<String, Object>() {{
                    put("output_param", "test_value_2");
                }})
                .timeout(100)
                .number(1)
                .build();
        patchGraphWithGraphItem(graph, node);
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
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
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .description("Тестовый узел")
                .timeout(100)
                .number(1)
                .build();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .goToNodesTab()
                .addNodeAndSave(node);
        node.setInput(new HashMap<String, String>() {{
            put("input_param", "");
        }});
        node.setOutput(new HashMap<String, Object>() {{
            put("output_param", "");
        }});
        node.setPrintedOutput(printedOutputValue);
        new GraphNodesPage()
                .editTemplateNode(node, "1.0.0", "edit")
                .checkNodeAttributes(node)
                .deleteNodeAndSave(node);
    }

    @Test
    @TmsLink("1035795")
    @DisplayName("Выключение переопределения Printed output в используемом шаблоне узла")
    public void saveNodeWithForbiddenPrintedOutputOverride() {
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .sourceVersion("")
                .printedOutput(Collections.singletonList(new HashMap<String, String>() {{
                    put("type", "text");
                }}))
                .number(1)
                .build();
        graphSteps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("graph", Collections.singletonList(node.toJson())));
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab();
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("printed_output_can_be_overridden", false));
        GraphNodesPage page = new GraphNodesPage();
        page.getSaveButton().click();
        Alert.red("Переопределение printed_output в ноде (1) запрещено шаблоном");
        page.openEditDialog(node);
        //TODO баг, что не отображается текст $x("//div[text()='Проверьте корректность заполнения полей']").shouldBe(Condition.visible);
        page.getParamsTab().click();
        page.getPrintedOutputHint().shouldHave(Condition
                .exactText("Переопределение запрещено в шаблоне. Очистите поле или включите переопределение Printed output в шаблоне узлов"));
        page.getPrintedOutputTextArea().clear();
        page.getFormSaveNodeButton().click();
        page.saveGraphWithPatchVersion();
    }

    @Test
    @TmsLink("1035865")
    @DisplayName("Выключение переопределения Input в используемом шаблоне узла")
    public void saveNodeWithForbiddenInputOverride() {
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_input", true));
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .sourceVersion("")
                .number(1)
                .input(new HashMap<String, String>() {{
                    put("override_param_1", "1");
                }})
                .build();
        graphSteps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("graph", Collections.singletonList(node.toJson())));
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab();
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_input", false));
        GraphNodesPage page = new GraphNodesPage();
        page.getSaveButton().click();
        Alert.red("Node \"input\" key(s) \"override_param_1\" not exists in Template");
        page.openEditDialog(node);
        Waiting.findWithAction(() -> $x("//div[text()='Проверьте корректность заполнения полей']").isDisplayed(),
                () -> {
                    page.getAdditionalTab().click();
                    page.getMainTab().click();
                }, Duration.ofSeconds(3));
        page.getParamsTab().click();
        Waiting.sleep(1500);
        page.getInputHint().shouldHave(Condition
                .exactText("Свойство \"override_param_1\" отсутствует в шаблоне (переопределение запрещено)"));
        node.setInput(new HashMap<String, String>() {{
            put("input_param", "");
        }});
        page.getInputTextArea().setValue(new JSONObject(node.getInput()).toString());
        page.getFormSaveNodeButton().click();
        page.saveGraphWithPatchVersion();
    }

    @Test
    @TmsLink("1539903")
    @DisplayName("Выключение переопределения Output в используемом шаблоне узла")
    public void saveNodeWithForbiddenOutputOverride() {
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_output", true));
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .sourceId(String.valueOf(template.getId()))
                .sourceType("template")
                .sourceVersion("")
                .number(1)
                .output(new HashMap<String, Object>() {{
                    put("override_param_1", "1");
                }})
                .build();
        graphSteps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("graph", Collections.singletonList(node.toJson())));
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab();
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_output", false));
        GraphNodesPage page = new GraphNodesPage();
        page.getSaveButton().click();
        Alert.red("Node \"output\" key(s) \"override_param_1\" not exists in Template");
        page.openEditDialog(node);
        Waiting.findWithAction(() -> $x("//div[text()='Проверьте корректность заполнения полей']").isDisplayed(),
                () -> {
                    page.getAdditionalTab().click();
                    page.getMainTab().click();
                }, Duration.ofSeconds(3));
        page.getParamsTab().click();
        Waiting.sleep(1500);
        page.getOutputHint().shouldHave(Condition
                .exactText("Свойство \"override_param_1\" отсутствует в шаблоне (переопределение запрещено)"));
        node.setOutput(new HashMap<String, Object>() {{
            put("output_param", "");
        }});
        page.getOutputTextArea().setValue(new JSONObject(node.getOutput()).toString());
        page.getFormSaveNodeButton().click();
        page.saveGraphWithPatchVersion();
    }

    @Test
    @TmsLink("1117763")
    @DisplayName("Переключение на версию шаблона с запрещенным переопределением Input, Output, Printed output")
    public void editNodeWithForbiddenParamsOverride() {
        String inputValue = "{\"override_param_1\":\"1\"}";
        String outputValue = "{\"override_param_2\":\"1\"}";
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_input", true)
                .put("additional_output", true));
        templateSteps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("additional_input", false)
                .put("additional_output", false)
                .put("printed_output_can_be_overridden", false));
        Template template2 = createTemplate(UUID.randomUUID().toString());
        GraphItem node = GraphItem.builder()
                .name("1")
                .description("1")
                .number(1)
                .build();
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .openGraphPage(NAME)
                .goToNodesTab()
                .openAddNodeDialog();
        GraphNodesPage page = new GraphNodesPage();
        page.getNodeName().setValue(node.getName());
        page.getNodeDescription().setValue(node.getDescription());
        page.getTemplateSelect().setContains(TEMPLATE_NAME);
        page.getTemplateVersionSelect().set("1.0.1");
        Waiting.sleep(2000);
        page.getParamsTab().click();
        page.getInputTextArea().setValue(inputValue);
        page.getOutputTextArea().setValue(outputValue);
        page.getFormAddNodeButton().click();
        page.saveGraphWithPatchVersion();
        page.openEditDialog(node);
        page.getTemplateVersionSelect().set("1.0.2");
        page.getParamsTab().click();
        Waiting.findWithAction(() -> page.getInputHint().exists(),
                () -> {
                    page.getMainTab().click();
                    page.getParamsTab().click();
                }, Duration.ofSeconds(3));
        assertEquals("Свойство \"override_param_1\" отсутствует в шаблоне (переопределение запрещено)", page.getInputHint().getText());
        assertEquals("Свойство \"override_param_2\" отсутствует в шаблоне (переопределение запрещено)", page.getOutputHint().getText());
        assertEquals("Переопределение запрещено в шаблоне. Очистите поле или включите переопределение Printed output в шаблоне узлов", page.getPrintedOutputHint().getText());
        page.getMainTab().click();
        page.getTemplateSelect().setContains(template2.getName());
        Waiting.sleep(2000);
        page.getParamsTab().click();
        assertEquals("{\"input_param\":\"\"}",
                page.getInputTextArea().getWhitespacesRemovedValue());
        assertEquals("{\"output_param\":\"\"}",
                page.getOutputTextArea().getWhitespacesRemovedValue());
        assertEquals("[{\"type\":\"text\"}]", page.getPrintedOutputTextArea().getWhitespacesRemovedValue());
    }
}
