package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class DeleteGraphTest extends GraphBaseTest {
    private static final String name = "at_ui_delete_graph_test";

    @Test
    @DisplayName("Удаление графа из списка")
    public void deleteGraphFromList() {
        new MainPage().goToGraphsPage()
                .createGraph(TITLE, name, "service", DESCRIPTION, AUTHOR)
                .findGraphByName(name)
                .deleteFirstGraphInList()
                .checkGraphNotFound(name);
    }

    @Test
    @DisplayName("Удаление графа со страницы графа")
    public void deleteGraphFromPage() {
        new MainPage().goToGraphsPage()
                .createGraph(TITLE, name, "service", DESCRIPTION, AUTHOR)
                .openGraphPage(name)
                .deleteGraph()
                .checkGraphNotFound(name);
    }
}
