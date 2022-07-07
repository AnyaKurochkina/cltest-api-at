package ui.cloud.tests.productCatalog.graph;

import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import models.productCatalog.Graph;
import models.productCatalog.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.uiModels.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@DisabledIfEnv("prod")
public class GraphBaseTest extends BaseTest {

    protected final String NAME = UUID.randomUUID().toString();
    protected final String SUBGRAPH_NAME = UUID.randomUUID().toString();
    protected final String TEMPLATE_NAME = UUID.randomUUID().toString();
    protected final static String TITLE = "AT UI Graph";
    protected final static String SUBGRAPH_TITLE = "AT UI Subgraph";
    protected final static String TEMPLATE_TITLE = "AT UI Template";
    protected final static String DESCRIPTION = "description";
    protected final static String AUTHOR = "QA";

    @BeforeEach
    @DisplayName("Создание графов через API")
    public void setUpForGraphsTest() {
        createGraph(NAME, TITLE);
    }

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе")
    public void tearDownForGraphTests() {
        deleteGraph(NAME);
    }

    public void createGraph(String name, String title) {
        Graph.builder()
                .name(name)
                .title(title)
                .version("1.0.0")
                .type("creating")
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build()
                .createObject();
    }

    public void createTemplate(String name) {
        Map<String,String> value = new LinkedHashMap<>();
        Map<String,Map<String,String>> input = new LinkedHashMap<>();
        Map<String,Map<String,String>> output = new LinkedHashMap<>();
        input.put(new Node().getInputKey(),value);
        output.put(new Node().getOutputKey(),value);
        Template.builder()
                .templateName(name)
                .title(TEMPLATE_TITLE)
                .type("creating")
                .description(DESCRIPTION)
                .type("system_nodes")
                .run("internal")
                .input(input)
                .output(output)
                .timeout(100)
                .build()
                .createObject();
    }

    public void deleteGraph(String name) {
    ProductCatalogSteps steps = new ProductCatalogSteps(Graph.productName);
    steps.getDeleteObjectResponse(steps
            .getProductObjectIdByNameWithMultiSearch(name, GetGraphsListResponse.class)).assertStatus(204);
    }

    public void deleteTemplate(String name) {
        ProductCatalogSteps steps = new ProductCatalogSteps(Template.productName);
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(name, GetTemplateListResponse.class)).assertStatus(204);
    }
}
