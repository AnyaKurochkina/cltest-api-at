package ui.productCatalog.tests.graph;

import models.productCatalog.Graph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import ui.productCatalog.pages.MainPage;
import ui.productCatalog.tests.BaseTest;

public class GraphBaseTest extends BaseTest {

    protected final static String TITLE = "title_at_ui_test";
    protected final static String NAME = "at_ui-test:1.";
    protected final static String DESCRIPTION = "description";
    protected final static String AUTHOR = "AT UI";

    @BeforeEach
    @DisplayName("Создание графа через API")
    public void createGraph() {
        Graph.builder()
                .name(NAME)
                .version("1.0.0")
                .type("creating")
                .build()
                .createObject();
    }

    @AfterEach
    @DisplayName("Удаление графа")
    public void deleteGraph() {
        new MainPage().goToGraphsPage()
                .findGraphByName(NAME)
                .deleteGraph();
    }
}
