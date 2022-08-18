package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.Graph;

@Epic("Графы")
@Feature("Копирование графа")
public class CopyGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("486880")
    @DisplayName("Копирование графа")
    public void copyGraph() {
        String copyName = NAME + "-clone";
        new IndexPage().goToGraphsPage()
                .findGraphByName(NAME)
                .copyGraph(NAME)
                .findGraphByName(copyName)
                .findAndOpenGraphPage(copyName)
                .checkGraphAttributes(new Graph(copyName, TITLE, "1.0.0"));
        deleteGraph(copyName);
    }
}
