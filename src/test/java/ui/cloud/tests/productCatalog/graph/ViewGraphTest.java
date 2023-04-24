package ui.cloud.tests.productCatalog.graph;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Payment;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static steps.productCatalog.GraphSteps.partialUpdateGraph;

@Feature("Просмотр графа")
public class ViewGraphTest extends GraphBaseTest {

    @Test
    @TmsLink("489318")
    @DisplayName("Просмотр JSON графа")
    public void viewJSON() {
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkJSONcontains(graph.getGraphId());
    }

    @Test
    @TmsLink("579885")
    @DisplayName("Переход к продукту по ссылке в использовании")
    public void goToProductTest() {
        String name = UUID.randomUUID().toString();
        Product product = Product.builder()
                .name(name)
                .title("AT UI Product")
                .version("1.0.1")
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
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkUsageTableHeaders()
                .checkUsageInProduct(product)
                .goToProductByUsageLink(product)
                .checkAttributes(product);
    }

    @Test
    @TmsLink("579923")
    @DisplayName("Переход к действию по ссылке в использовании")
    public void goToActionTest() {
        String name = UUID.randomUUID().toString();
        Action action = Action.builder()
                .name(name)
                .title("AT UI Action")
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .number(0)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkUsageInAction(action)
                .goToActionByUsageLink(action)
                .checkAttributes(action);
    }

    @Test
    @TmsLink("579965")
    @DisplayName("Переход к сервису по ссылке в использовании")
    public void goToServiceTest() {
        String name = UUID.randomUUID().toString();
        OrgDirection orgDirection = OrgDirection.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Direction")
                .build()
                .createObject();
        Service service = Service.builder()
                .directionId(orgDirection.getId())
                .name(name)
                .title("AT UI Service")
                .description(DESCRIPTION)
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .build()
                .createObject();
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkUsageInService(service)
                .goToServiceByUsageLink(service)
                .checkAttributes(service);
    }

    @Test
    @TmsLink("1086265")
    @DisplayName("Переход к графу по ссылке в использовании")
    public void goToGraphTest() {
        String name = UUID.randomUUID().toString();
        Graph superGraph = Graph.builder()
                .name(name)
                .title(TITLE)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .description(DESCRIPTION)
                .author(AUTHOR)
                .build()
                .createObject();
        JSONObject graphItem = GraphItem.builder()
                .name("1")
                .description("1")
                .subgraphId(graph.getGraphId())
                .build()
                .toJson();
        JSONObject graphJSON = new JSONObject().put("graph", Collections.singletonList(graphItem));
        partialUpdateGraph(superGraph.getGraphId(), graphJSON);
        superGraph.setVersion("1.0.1");
        new ControlPanelIndexPage().goToGraphsPage()
                .findAndOpenGraphPage(NAME)
                .checkUsageInGraph(superGraph)
                .goToGraphByUsageLink(superGraph)
                .checkAttributes(superGraph);
    }
}
