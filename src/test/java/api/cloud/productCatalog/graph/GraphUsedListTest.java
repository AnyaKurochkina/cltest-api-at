package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.http.QueryBuilder;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import static core.helper.StringUtils.getRandomStringApi;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.GraphSteps.getObjectArrayUsedGraph;
import static steps.productCatalog.ProductSteps.getProductById;
import static steps.productCatalog.ProductSteps.partialUpdateProduct;
import static steps.productCatalog.ServiceSteps.getServiceById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphUsedListTest extends Tests {

    @DisplayName("Получение списка объектов использующих граф")
    @TmsLink("642681")
    @Test
    public void getUsedGraphList() {
        Graph usedGraphApi = createGraph("used_graph_test_api");
        String usedGraphId = usedGraphApi.getGraphId();

        Product createProductResponse = Product.builder()
                .name("product_for_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service createServiceResponse = Service.builder()
                .name("service_for_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action createActionResponse = createAction(Action.builder()
                .name("action_for_used_graph_test_api")
                .graphId(usedGraphId)
                .build());

        JsonPath jsonPath = getObjectArrayUsedGraph(usedGraphId).jsonPath();
        assertAll(
                () -> assertEquals(createProductResponse.getProductId(), jsonPath.getString("id[0]")),
                () -> assertEquals(createActionResponse.getId(), jsonPath.getString("id[1]")),
                () -> assertEquals(createServiceResponse.getId(), jsonPath.getString("id[2]"))
        );
    }

    @DisplayName("Получение списка объектов определенного типа использующих граф")
    @TmsLink("1114670")
    @Test
    public void getObjTypeUsedGraphList() {
        Graph usedGraphApi = createGraph("type_used_graph_test_api");
        String usedGraphId = usedGraphApi.getGraphId();

        Product.builder()
                .name("product_for_type_used_graph_test_api")
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service.builder()
                .name("service_for_type_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        createAction(Action.builder()
                .name("action_for_type_used_graph_test_api")
                .graphId(usedGraphId)
                .build());
        Response response = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder().add("obj_type", "product"));
        assertEquals("Product", response.jsonPath().getString("type[0]"));
        Response getActionResp = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder().add("obj_type", "action"));
        assertEquals("Action", getActionResp.jsonPath().getString("type[0]"));
        Response getServiceResp = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder().add("obj_type", "service"));
        assertEquals("Service", getServiceResp.jsonPath().getString("type[0]"));
        Response getAllTypeResp = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder()
                .add("obj_type", "service")
                .add("obj_type", "product")
                .add("obj_type", "action"));
        assertEquals("Service", getAllTypeResp.jsonPath().getString("type[0]"));
        assertEquals("Product", getAllTypeResp.jsonPath().getString("type[1]"));
        assertEquals("Action", getAllTypeResp.jsonPath().getString("type[2]"));
    }

    @DisplayName("Получение списка последних созданных объектов использующих граф")
    @TmsLink("1117823")
    @Test
    public void getLastObjUsedGraphList() {
        Graph usedGraphApi = createGraph("last_object_used_graph_api");
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
                .name("service_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service lastService = Service.builder()
                .name("last_service_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        createAction(Action.builder()
                .name("action2_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build());

        Action lastAction = createAction(Action.builder()
                .name("last_action_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build());

        Response response = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder().add("last_object", true));
        assertEquals(lastAction.getName(), response.jsonPath().getString("name[1]"));
        assertEquals(lastService.getName(), response.jsonPath().getString("name[2]"));
        assertEquals(lastProduct.getName(), response.jsonPath().getString("name[0]"));
    }

    @DisplayName("Получение последних версий объектов использующего граф")
    @TmsLink("1117927")
    @Test
    public void getLastVersionUsedGraphList() {
        Graph usedGraphApi = createGraph("last_version_used_graph_api");
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
                .name("service2_for_last_object_used_graph_test_api")
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();
        ProductCatalogSteps serviceSteps = new ProductCatalogSteps("/api/v1/services/",
                "/productCatalog/services/createServices.json");
        serviceSteps.partialUpdateObject(service.getId(), new JSONObject()
                .put("service_info", "updated_service_for_last_version_used_graph_test_api"));
        String serviceVersion = getServiceById(service.getId()).getVersion();

        Action action = createAction(Action.builder()
                .name("action_for_last_object_used_graph_test_api")
                .graphId(usedGraphId)
                .build());

        ProductCatalogSteps actionSteps = new ProductCatalogSteps("/api/v1/actions/",
                "productCatalog/actions/createAction.json");
        actionSteps.partialUpdateObject(action.getId(), new JSONObject()
                .put("priority", 1));
        String actionVersion = getActionById(action.getId()).getVersion();

        Response response = getObjectArrayUsedGraph(usedGraphId, new QueryBuilder().add("last_version", true));
        assertEquals(actionVersion, response.jsonPath().getString("version[1]"));
        assertEquals(serviceVersion, response.jsonPath().getString("version[2]"));
        assertEquals(productVersion, response.jsonPath().getString("version[0]"));
    }

    @DisplayName("Получение списка объектов использующих граф после удаления объектов")
    @TmsLink("SOUL-8280")
    @Test
    public void getUsedGraphListAfterProductDeletedTest() {
        Graph usedGraphApi = createGraph("used_graph_with_deleted_object_api");
        String usedGraphId = usedGraphApi.getGraphId();

        Product createProductResponse = Product.builder()
                .name(getRandomStringApi(6))
                .graphId(usedGraphId)
                .build()
                .createObject();

        Service createServiceResponse = Service.builder()
                .name(getRandomStringApi(6))
                .title("service_title")
                .isPublished(false)
                .graphId(usedGraphId)
                .build()
                .createObject();

        Action createActionResponse = createAction(Action.builder()
                .name(getRandomStringApi(6))
                .graphId(usedGraphId)
                .build().toJson()).extractAs(Action.class);

        assertEquals(3, getObjectArrayUsedGraph(usedGraphId).jsonPath().getList("").size());
        deleteActionById(createActionResponse.getId());
        createProductResponse.delete();
        createServiceResponse.delete();

        assertEquals(0, getObjectArrayUsedGraph(usedGraphId).jsonPath().getList("").size());
    }
}
