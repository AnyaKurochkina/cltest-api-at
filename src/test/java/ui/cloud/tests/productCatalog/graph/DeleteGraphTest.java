package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

@Epic("Графы")
@Feature("Удаление графа")
public class DeleteGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("540702")
    @DisplayName("Удаление графа из списка")
    public void deleteGraphFromList() {
        new IndexPage().goToGraphsPage()
                .findGraphByName(NAME)
                .deleteGraph(NAME)
                .checkGraphNotFound(NAME);
    }

    @Test
    @DisplayName("Удаление графа со страницы графа")
    public void deleteGraphFromPage() {
        new IndexPage().goToGraphsPage()
                .openGraphPage(NAME)
                .deleteGraph()
                .checkGraphNotFound(NAME);
    }

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе")
    public void tearDownForGraphTests() {}
}
