package ui.cloud.tests.productCatalog.graph;

import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import models.productCatalog.Graph;
import models.productCatalog.Template;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.UUID;

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
        createGraph(NAME);
    }

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе")
    public void tearDownForGraphTests() {
        deleteGraph(NAME);
    }

    public void createGraph(String name) {
        Graph.builder()
                .name(name)
                .title(TITLE)
                .version("1.0.0")
                .type("creating")
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build()
                .createObject();
    }

    public void createTemplate(String name) {
        Template.builder()
                .templateName(name)
                .title(TEMPLATE_TITLE)
                .type("creating")
                .description(DESCRIPTION)
                .type("system_nodes")
                .run("internal")
                .build()
                .createObject();
    }

    public void deleteGraph(String name) {
    ProductCatalogSteps steps = new ProductCatalogSteps(Graph.productName);
    steps.getDeleteObjectResponse(steps
            .getProductObjectIdByNameWithMultiSearch(name, GetGraphsListResponse.class)).assertStatus(200);
    }
}
