package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.enums.LogLevel;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.*;

import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.TemplateSteps.deleteTemplateById;
import static steps.productCatalog.TemplateSteps.getTemplateByName;

@Epic("Конструктор.Графы")
@DisabledIfEnv("prod")
public class GraphBaseTest extends BaseTest {

    protected final static String TITLE = "AT UI Graph";
    protected final static String SUBGRAPH_TITLE = "AT UI Subgraph";
    protected final static String TEMPLATE_TITLE = "AT UI Template";
    protected final static String DESCRIPTION = "description";
    protected final static String AUTHOR = "QA";
    protected final String NAME = UUID.randomUUID().toString();
    protected final String SUBGRAPH_NAME = UUID.randomUUID().toString();
    protected final String TEMPLATE_NAME = UUID.randomUUID().toString();
    protected String nodeDescription = "Тестовый узел";
    protected String printedOutputValue = "[{\"type\":\"text\"}]";
    protected ProductCatalogSteps graphSteps = new ProductCatalogSteps("/api/v1/graphs/",
            "productCatalog/graphs/createGraph.json");
    protected ProductCatalogSteps templateSteps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");
    protected Graph graph;
    protected Template template;

    @BeforeEach
    @DisplayName("Создание графов через API")
    public void setUpForGraphsTest() {
        createGraph(NAME, TITLE);
    }

    public Graph createGraph(String name, String title) {
        graph = Graph.builder()
                .name(name)
                .title(title)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build()
                .createObject();
        return graph;
    }

    public Template createTemplate(String name) {
        Map<String, String> value = new HashMap<>();
        Map<String, Map<String, String>> input = new HashMap<>();
        Map<String, Map<String, String>> output = new HashMap<>();
        input.put("input_param", value);
        output.put("output_param", value);
        template = Template.builder()
                .name(name)
                .title(TEMPLATE_TITLE)
                .description("Template for node")
                .type("system_nodes")
                .run("internal")
                .rollback("")
                .input(input)
                .output(output)
                .printedOutput(Collections.singletonList(new HashMap<String, String>() {{
                    put("type", "text");
                }}))
                .printedOutputCanBeOverridden(true)
                .logLevel(LogLevel.SHORT.getValue())
                .logCanBeOverridden(false)
                .timeout(100)
                .build()
                .createObject();
        return template;
    }

    public void deleteGraphByApi(String name) {
        deleteGraphById(getGraphByName(name).getGraphId());
    }

    public void deleteTemplate(String name) {
        deleteTemplateById(getTemplateByName(name).getId());
    }

    protected void patchGraphWithGraphItem(Graph graph, GraphItem graphItem) {
        JSONObject graphItemsJSON = new JSONObject().put("graph", Collections.singletonList(graphItem.toJson()));
        partialUpdateGraph(graph.getGraphId(), graphItemsJSON);
    }
}
