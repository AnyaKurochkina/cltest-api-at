package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;
import ui.models.Graph;

@Feature("Удаление графа")
public class DeleteGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("1114449")
    @DisplayName("Удаление графа из списка")
    public void deleteGraphFromList() {
        Graph graph = new Graph(NAME);
        new IndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .deleteGraph(NAME)
                .checkGraphNotFound(NAME);
    }

    @Test
    @TmsLink("540702")
    @DisplayName("Удаление графа со страницы графа")
    public void deleteGraphFromPage() {
        new IndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .openDeleteDialog()
                .inputInvalidId("test")
                .inputValidIdAndDelete();
        new GraphsListPage()
                .checkGraphNotFound(NAME);
    }

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе")
    public void tearDownForGraphTests() {
    }
}
