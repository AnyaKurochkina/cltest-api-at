package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class CopyGraphTest extends GraphBaseTest {

    @Test
    @DisplayName("Копирование графа")
    public void copyGraph() {
        new MainPage().goToGraphsPage()
                .findGraphByName(NAME)
                .copyGraph()
                .findGraphByName(NAME + "-clone");
        deleteGraph();
    }
}
