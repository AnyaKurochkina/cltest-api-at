package ui.productCatalog.tests.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.productCatalog.pages.MainPage;

public class CopyGraphTest extends GraphBaseTest {

    @Test
    @DisplayName("Копирование графа")
    public void copyGraph() {
        String copyName = NAME + "-clone";
        new MainPage().goToGraphsPage()
                .findGraphByName(NAME)
                .copyGraph()
                .findGraphByName(copyName)
                .openGraphPage(copyName)
                .checkGraphAttributes(copyName, TITLE, "1.0.0");
        deleteGraph(copyName);
    }
}
