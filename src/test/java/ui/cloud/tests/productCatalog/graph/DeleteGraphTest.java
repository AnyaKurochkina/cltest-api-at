package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Payment;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;

import java.util.Arrays;
import java.util.UUID;

@Feature("Удаление графа")
public class DeleteGraphTest extends GraphBaseTest {

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе (не требуется)")
    public void tearDownForGraphTests() {
    }

    @Test
    @TmsLink("1114449")
    @DisplayName("Удаление графа из списка")
    public void deleteGraphFromList() {
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

    @Test
    @TmsLink("579885")
    @DisplayName("Удаление графа, используемого в продукте")
    public void deleteGraphUsedInProduct() {
        String name = UUID.randomUUID().toString();
        Product product = Product.builder()
                .name(name)
                .title("AT UI Product")
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .category(Categories.VM.getValue())
                .categoryV2(Categories.COMPUTE)
                .maxCount(1)
                .payment(Payment.PAID)
                .author("AT UI")
                .inGeneralList(false)
                .number(51)
                .build()
                .createObject();
        new IndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .checkDeleteUsedGraphUnavailable(graph)
                .checkUsageInProduct(product)
                .checkDeleteUsedGraphUnavailable(graph);
    }

    @Test
    @TmsLink("")
    @Disabled
    @DisplayName("Удаление графа, используемого в графе")
    public void deleteGraphUsedInGraph() {
        String name = UUID.randomUUID().toString();
        Graph graph2 = Graph.builder()
                .name(name)
                .title(TITLE)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description(DESCRIPTION)
                .author(AUTHOR)
                .graph(Arrays.asList(GraphItem.builder().name("1").description("1").subgraphId(graph.getGraphId()).build()))
                .build()
                .createObject();
        new IndexPage().goToGraphsPage()
                .findGraphByValue(NAME, graph)
                .checkDeleteUsedGraphUnavailable(graph)
                .checkUsageInGraph(graph2)
                .checkDeleteUsedGraphUnavailable(graph);
    }
}
