package ui.productCatalog.tests.graph;

import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import models.productCatalog.Graph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.productCatalog.tests.BaseTest;

public class GraphBaseTest extends BaseTest {

    protected final static String TITLE = "AT UI Graph";
    protected final static String NAME = "at_ui_graph";
    protected final static String SUBGRAPH_NAME = "at_ui_subgraph";
    protected final static String SUBGRAPH_TITLE = "AT UI Subgraph";
    protected final static String DESCRIPTION = "description";
    protected final static String AUTHOR = "QA";

    @BeforeEach
    @DisplayName("Создание графов через API")
    public void createGraphs() {
        Graph.builder()
                .name(NAME)
                .title(TITLE)
                .version("1.0.0")
                .type("creating")
                .author(AUTHOR)
                .build()
                .createObject();

        Graph.builder()
                .name(SUBGRAPH_NAME)
                .title(SUBGRAPH_TITLE)
                .version("1.0.0")
                .type("creating")
                .author(AUTHOR)
                .build()
                .createObject();
    }

    @AfterEach
    @DisplayName("Удаление графов")
    public void deleteGraph() {
        new ProductCatalogSteps(Graph.productName).deleteByName(NAME, GetGraphsListResponse.class);
        new ProductCatalogSteps(Graph.productName).deleteByName(SUBGRAPH_NAME, GetGraphsListResponse.class);
    }
}
