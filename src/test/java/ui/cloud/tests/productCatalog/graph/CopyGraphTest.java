package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;

@Feature("Копирование графа")
public class CopyGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("486880")
    @DisplayName("Копирование графа")
    public void copyGraph() {
        String copyName = NAME + "-clone";
        new IndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .copyGraph(NAME);
        graph.setName(copyName);
        new GraphsListPage()
                .findAndOpenGraphPage(copyName)
                .checkAttributes(graph);
        deleteGraphByApi(copyName);
    }
}
