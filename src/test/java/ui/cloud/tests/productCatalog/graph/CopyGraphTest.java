package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Копирование графа")
public class CopyGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("486880")
    @DisplayName("Копирование графа")
    public void copyGraph() {
        String copyName = NAME + "-clone";
        new ControlPanelIndexPage().goToGraphsPage()
                .checkGraphFoundByValue(NAME, graph)
                .copyGraph(NAME);
        graph.setName(copyName);
        new ControlPanelIndexPage()
                .goToGraphsPage()
                .findAndOpenGraphPage(copyName)
                .checkAttributes(graph);
        deleteGraphByApi(copyName);
    }
}
