package tests.productCatalog.graph;

import core.helper.http.Response;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Service;
import models.productCatalog.action.Action;
import models.productCatalog.graph.Graph;
import models.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.getActionById;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductSteps.getProductById;
import static steps.productCatalog.ProductSteps.partialUpdateProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphUsedListTest extends Tests {

    @DisplayName("Получение списка объектов использующих граф")
    @TmsLink("642681")
    @Test
    public void getUsedGraphList() {
        Graph usedGraphApi = Graph.builder()
                .name("used_graph_api")
                .build()
                .createObject();
        String usedGraphId = usedGraphApi.getGraphId();

        Product createProductResponse = Product.builder()
                .name("product_for_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service createServiceResponse = Service.builder()
                .serviceName("service_for_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action createActionResponse = Action.builder()
                .actionName("action_for_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        JsonPath jsonPath = getObjectArrayUsedGraph(usedGraphId);
        assertAll(
                () -> assertEquals(createProductResponse.getProductId(), jsonPath.getString("id[0]")),
                () -> assertEquals(createActionResponse.getActionId(), jsonPath.getString("id[1]")),
                () -> assertEquals(createServiceResponse.getServiceId(), jsonPath.getString("id[2]"))
        );
    }

    @DisplayName("Получение списка объектов определенного типа использующих граф")
    @TmsLink("1114670")
    @Test
    public void getObjTypeUsedGraphList() {
        Graph usedGraphApi = Graph.builder()
                .name("type_used_graph_api")
                .build()
                .createObject();
        String usedGraphId = usedGraphApi.getGraphId();

        Product.builder()
                .name("product_for_type_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service.builder()
                .serviceName("service_for_type_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action.builder()
                .actionName("action_for_type_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();
        Response response = getObjectTypeUsedGraph(usedGraphId, "product");
        assertEquals("Product", response.jsonPath().getString("type[0]"));
        Response getActionResp = getObjectTypeUsedGraph(usedGraphId, "action");
        assertEquals("Action", getActionResp.jsonPath().getString("type[0]"));
        Response getServiceResp = getObjectTypeUsedGraph(usedGraphId, "service");
        assertEquals("Service", getServiceResp.jsonPath().getString("type[0]"));
        Response getAllTypeResp = getObjectTypeUsedGraph(usedGraphId, "service", "product", "action");
        assertEquals("Service", getAllTypeResp.jsonPath().getString("type[0]"));
        assertEquals("Product", getAllTypeResp.jsonPath().getString("type[1]"));
        assertEquals("Action", getAllTypeResp.jsonPath().getString("type[2]"));
    }

    @DisplayName("Получение списка последних созданных объектов использующих граф")
    @TmsLink("1117823")
    @Test
    public void getLastObjUsedGraphList() {
        Graph usedGraphApi = Graph.builder()
                .name("last_object_used_graph_api")
                .build()
                .createObject();
        String usedGraphId = usedGraphApi.getGraphId();

        Product.builder()
                .name("product_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Product lastProduct = Product.builder()
                .name("last_product_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service.builder()
                .serviceName("service_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service lastService = Service.builder()
                .serviceName("last_service_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action.builder()
                .actionName("action_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action lastAction = Action.builder()
                .actionName("last_action_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Response response = getLastObjectUsedGraph(usedGraphId);
        assertEquals(lastAction.getActionName(), response.jsonPath().getString("name[1]"));
        assertEquals(lastService.getServiceName(), response.jsonPath().getString("name[2]"));
        assertEquals(lastProduct.getName(), response.jsonPath().getString("name[0]"));
    }

    @DisplayName("Получение последних версий объектов использующего граф")
    @TmsLink("1117927")
    @Test
    public void getLastVersionUsedGraphList() {
        Graph usedGraphApi = Graph.builder()
                .name("last_version_used_graph_api")
                .build()
                .createObject();
        String usedGraphId = usedGraphApi.getGraphId();

        Product product = Product.builder()
                .name("product_for_last_version_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();
        partialUpdateProduct(product.getProductId(), new JSONObject()
                .put("max_count", 2));
        String productVersion = getProductById(product.getProductId()).getVersion();


        Service service = Service.builder()
                .serviceName("service_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();
        ProductCatalogSteps serviceSteps = new ProductCatalogSteps("/api/v1/services/",
                "/productCatalog/services/createServices.json");
        serviceSteps.partialUpdateObject(service.getServiceId(), new JSONObject()
                .put("service_info", "updated_service_for_last_version_used_graph_test_api"));
        String serviceVersion = serviceSteps.getById(service.getServiceId(), GetServiceResponse.class).getVersion();

        Action action = Action.builder()
                .actionName("action_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        ProductCatalogSteps actionSteps = new ProductCatalogSteps("/api/v1/actions/",
                "productCatalog/actions/createAction.json");
        actionSteps.partialUpdateObject(action.getActionId(), new JSONObject()
                .put("priority", 1));
        String actionVersion = getActionById(action.getActionId()).getVersion();

        Response response = getLastVersionUsedGraph(usedGraphId);
        assertEquals(actionVersion, response.jsonPath().getString("version[1]"));
        assertEquals(serviceVersion, response.jsonPath().getString("version[2]"));
        assertEquals(productVersion, response.jsonPath().getString("version[0]"));
    }
}
