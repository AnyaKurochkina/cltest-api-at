package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.pages.productCatalog.graph.GraphsListPage;

import java.util.Collections;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.partialUpdateGraph;

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
        new ControlPanelIndexPage().goToGraphsPage()
                .checkGraphFoundByValue(NAME, graph)
                .deleteGraph(NAME)
                .checkGraphNotFound(NAME);
    }

    @Test
    @TmsLink("540702")
    @DisplayName("Удаление графа со страницы графа")
    public void deleteGraphFromPage() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .openDeleteDialog()
                .submitAndDelete("Удаление выполнено успешно");
        new GraphsListPage()
                .checkGraphNotFound(NAME);
    }

    @Test
    @TmsLink("540777")
    @DisplayName("Удаление графа, используемого в продукте")
    public void deleteGraphUsedInProduct() {
        String name = UUID.randomUUID().toString();
        Product.builder()
                .name(name)
                .title("AT UI Product")
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .category(Categories.VM.getValue())
                .categoryV2(Categories.COMPUTE)
                .maxCount(1)
                .author("AT UI")
                .inGeneralList(false)
                .number(51)
                .build()
                .createObject();
        new ControlPanelIndexPage().goToGraphsPage()
                .checkGraphFoundByValue(NAME, graph)
                .checkDeleteUsedGraphUnavailable(graph)
                .checkDeleteUsedGraphUnavailable();
    }

    @Test
    @TmsLink("1095969")
    @DisplayName("Удаление графа, используемого в другом графе")
    public void deleteGraphUsedInGraph() {
        String name = UUID.randomUUID().toString();
        Graph superGraph = GraphSteps.createGraph(Graph.builder()
                .name(name)
                .title(TITLE)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build());
        JSONObject graphItem = GraphItem.builder()
                .name("1")
                .description("1")
                .sourceId(graph.getGraphId())
                .sourceType("subgraph")
                .build()
                .toJson();
        JSONObject graphJSON = new JSONObject().put("graph", Collections.singletonList(graphItem));
        partialUpdateGraph(superGraph.getGraphId(), graphJSON);
        superGraph.setVersion("1.0.1");
        new ControlPanelIndexPage().goToGraphsPage()
                .checkGraphFoundByValue(NAME, graph)
                .checkDeleteUsedGraphUnavailable(graph)
                .checkDeleteUsedGraphUnavailable();
    }
}
