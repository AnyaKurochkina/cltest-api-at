package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.Graph;

@Feature("Копирование графа")
public class CopyGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("486880")
    @DisplayName("Копирование графа")
    public void copyGraph() {
        Graph graph = new Graph(NAME);
        String copyName = NAME + "-clone";
        Graph graphCopy = new Graph(copyName);
        new IndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .copyGraph(NAME)
                .findGraphByValue(copyName, graphCopy)
                .findAndOpenGraphPage(copyName)
                .checkGraphAttributes(new Graph(copyName, TITLE, "1.0.0"));
        deleteGraph(copyName);
    }
}
