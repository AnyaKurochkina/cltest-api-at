package ui.cloud.tests.productCatalog.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

public class CopyGraphTest extends GraphBaseTest {

    @Test
    @DisplayName("Копирование графа")
    public void copyGraph() {
        String copyName = NAME + "-clone";
        new IndexPage().goToGraphsPage()
                .findGraphByName(NAME)
                .copyGraph(NAME)
                .findGraphByName(copyName)
                .openGraphPage(copyName)
                .checkGraphAttributes(copyName, TITLE, "1.0.0");
        deleteGraph(copyName);
    }
}
