package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.template.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.models.Node;

import java.util.*;

import static steps.productCatalog.GraphSteps.deleteGraphById;
import static steps.productCatalog.GraphSteps.getGraphByName;
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
    protected ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/graphs/",
            "productCatalog/graphs/createGraph.json");
    protected Graph graph;

    @BeforeEach
    @DisplayName("Создание графов через API")
    public void setUpForGraphsTest() {
        createGraph(NAME, TITLE);
    }

    public void createGraph(String name, String title) {
        graph = Graph.builder()
                .name(name)
                .title(title)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build()
                .createObject();
    }

    public void createTemplate(String name) {
        Map<String, String> value = new LinkedHashMap<>();
        Map<String, Map<String, String>> input = new LinkedHashMap<>();
        Map<String, Map<String, String>> output = new LinkedHashMap<>();
        input.put(new Node().getInputKey(), value);
        output.put(new Node().getOutputKey(), value);
        Template.builder()
                .name(name)
                .title(TEMPLATE_TITLE)
                .description("Template for node")
                .type("system_nodes")
                .run("internal")
                .rollback("")
                .input(input)
                .output(output)
                .printedOutput(new HashMap<String, String>() {{
                    put("type", "text");
                }})
                .timeout(100)
                .build()
                .createObject();
    }

    public void deleteGraphByApi(String name) {
        deleteGraphById(getGraphByName(name).getGraphId());
    }

    public void deleteTemplate(String name) {
        deleteTemplateById(getTemplateByName(name).getId());
    }
}
