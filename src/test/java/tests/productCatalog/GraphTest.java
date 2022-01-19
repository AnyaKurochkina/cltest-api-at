package tests.productCatalog;

import core.helper.JsonHelper;
import core.helper.MarkDelete;
import core.helper.StringUtils;
import httpModels.productCatalog.Action.createAction.response.CreateActionResponse;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.Graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.Graphs.deleteGraph.response.DeleteGraphResponse;
import httpModels.productCatalog.Graphs.existsGraphs.response.ExistsGraphsResponse;
import httpModels.productCatalog.Graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.Graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.Product.createProduct.response.CreateProductResponse;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
public class GraphTest {


    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    private final String productName = "graphs/";
    Graph graph;


    @Order(1)
    @DisplayName("Создание графа")
    @Test
    public void createGraph() {
        graph = Graph.builder().name("at_test_graph_api").version("1.0.0").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка графа")
    @Test
    public void getGraphsList() {
        Assertions.assertTrue(productCatalogSteps
                .getProductObjectList(productName, GetGraphsListResponse.class).size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования графа по имени")
    @Test
    public void checkGraphExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(productName, graph.getName(), ExistsGraphsResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists(productName, "NoExistsAction", ExistsGraphsResponse.class));
    }

//    @Order(4)
//    @DisplayName("Импорт графа")
//    @Test
//    public void importGraph() {
//        String data = new JsonHelper().getStringFromFile("/productCatalog/graphs/importGraph.json");
//        String graphName = new JsonPath(data).get("Graphs.json.name");
//        graphSteps.importGraph(Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json");
//        Assertions.assertTrue(graphSteps.isExist(graphName));
//        //   graphSteps.deleteActionByName(actionName);
//     //   Assertions.assertFalse(graphSteps.isExist(graphName));
//    }

    @Order(5)
    @DisplayName("Получение графа по Id")
    @Test
    public void getGraphById() {
        GetImpl getImpl = productCatalogSteps.getById(productName, graph.getGraphId(), GetGraphResponse.class);
        Assertions.assertEquals(graph.getName(), getImpl.getName());
    }

    @Order(6)
    @DisplayName("Негатичный тест на получение графа по Id без токена")
    @Test
    public void getGraphByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, graph.getGraphId(), GetGraphResponse.class);
    }

    @Order(50)
    @DisplayName("Копирование графа по Id")
    @Test
    public void copyGraphById() {
        String cloneName = graph.getName() + "-clone";
        productCatalogSteps.copyById(productName, graph.getGraphId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistsGraphsResponse.class));
        productCatalogSteps.getDeleteObjectResponse(productName,
                        productCatalogSteps.getProductObjectIdByNameWithMultiSearch(productName, cloneName, GetGraphsListResponse.class))
                .assertStatus(200);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistsGraphsResponse.class));
    }

    @Order(50)
    @DisplayName("Негативный тест на копирование графа по Id без токена")
    @Test
    public void copyGraphByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(productName, graph.getGraphId());
    }

    @Order(60)
    @DisplayName("Частичное обновление графа по Id")
    @Test
    public void partialUpdateGraph() {
        String expectedDescription = "UpdateDescription";
        String oldGraphVersion = graph.getVersion();
        productCatalogSteps.partialUpdateObject(productName, graph.getGraphId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getGraphResponse = productCatalogSteps.getById(productName, graph.getGraphId(), GetGraphResponse.class);
        String actualDescription = getGraphResponse.getDescription();
        String newGraphVersion = getGraphResponse.getVersion();
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertNotEquals(oldGraphVersion, newGraphVersion);
    }

    @Order(61)
    @DisplayName("Негативный тест на обновление графа по Id без токена")
    @Test
    public void updateGraphByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(productName, graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(70)
    @DisplayName("Негативный тест на попытку обновления графа до текущей версии")
    @Test
    public void partialUpdateForCurrentVersion() {
        String currentVersion = graph.getVersion();
        productCatalogSteps.partialUpdateObject(productName, graph.getGraphId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(80)
    @DisplayName("Негативный тест на создание графа с существующим именем")
    @Test
    public void createGraphWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                .createJsonObject(graph.getName(), "productCatalog/graphs/createGraph.json")).assertStatus(400);
    }

    @Order(90)
    @DisplayName("Получение списка объектов использующих граф")
    @Test
    public void getUsedGraphList() {
        CreateGraphResponse usedGraphApi = productCatalogSteps.createProductObject(productName, productCatalogSteps
                .createJsonObject("used_graph_api", "productCatalog/graphs/createGraph.json")).extractAs(CreateGraphResponse.class);
        String usedGraphId = usedGraphApi.getId();

        CreateProductResponse createProductResponse = productCatalogSteps
                .createProductObject("products/", JsonHelper.getJsonTemplate("productCatalog/products/createProduct.json")
                        .set("name", "product_for_used_graph_test_api")
                        .set("graph_id", usedGraphId)
                        .build()).extractAs(CreateProductResponse.class);

        CreateServiceResponse createServiceResponse = productCatalogSteps
                .createProductObject("services/", JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "service_for_used_graph_test_api")
                        .set("title", "service_title")
                        .set("graph_id", usedGraphId)
                        .build()).extractAs(CreateServiceResponse.class);

        CreateActionResponse createActionResponse = productCatalogSteps
                .createProductObject("actions/", JsonHelper.getJsonTemplate("productCatalog/actions/createAction.json")
                        .set("name", "action_for_used_graph_test_api")
                        .set("graph_id", usedGraphId)
                        .build()).extractAs(CreateActionResponse.class);

        JsonPath jsonPath = productCatalogSteps.getObjectArrayUsedGraph(usedGraphId);
        assertAll(
                () -> assertEquals(createProductResponse.getId(), jsonPath.getString("id[0]")),
                () -> assertEquals(createActionResponse.getId(), jsonPath.getString("id[1]")),
                () -> assertEquals(createServiceResponse.getId(), jsonPath.getString("id[2]"))
        );
        productCatalogSteps.deleteById("products/", createProductResponse.getId());
        productCatalogSteps.deleteById("services/", createServiceResponse.getId());
        productCatalogSteps.deleteById("actions/", createActionResponse.getId());
        productCatalogSteps.getDeleteObjectResponse(productName, usedGraphId).assertStatus(200);
    }

    @Order(97)
    @Test
    @DisplayName("Проверка отсутсвия ' в значениях ключя template_id")
    public void checkKeys() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps.createJsonObject("api_test", "productCatalog/graphs/createGraph.json"));
        productCatalogSteps.partialUpdateObject(productName, productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch(productName, "api_test", GetGraphsListResponse.class), JsonHelper.getJsonTemplate("productCatalog/graphs/patch.json")
                .build());
        String id = productCatalogSteps.getProductObjectIdByNameWithMultiSearch(productName, "api_test", GetGraphsListResponse.class);
        JsonPath jsonPath = productCatalogSteps.getJsonPath(productName, id);
        assertFalse(jsonPath.getString("graph[0].template_id").contains("'"));
        productCatalogSteps.getDeleteObjectResponse(productName, productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch(productName, "api_test", GetGraphsListResponse.class )).assertStatus(200);
    }

    @Order(98)
    @Test
    @DisplayName("Попытка удаления графа используемого в продукте, действии и сервисе")
    public void deleteUsedGraph() {
        CreateGraphResponse mainGraph = productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("main_graph", "productCatalog/graphs/createGraph.json"))
                .extractAs(CreateGraphResponse.class);
        String mainGraphId = mainGraph.getId();

        CreateGraphResponse secondGraph = productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("second_graph", "productCatalog/graphs/createGraph.json"))
                .extractAs(CreateGraphResponse.class);
        String secondGraphId = secondGraph.getId();

        productCatalogSteps.partialUpdateObject(productName, mainGraphId, new JSONObject().put("description", "updateVersion2.0")
                .put("version", "2.0.0"));
        productCatalogSteps.partialUpdateObject(productName, mainGraphId, new JSONObject().put("description", "updateVersion3.0")
                .put("version", "3.0.0"));
        CreateProductResponse createProductResponse = productCatalogSteps
                .createProductObject("products/", JsonHelper.getJsonTemplate("productCatalog/products/createProduct.json")
                        .set("name", "product_for_graph_test_api")
                        .set("graph_id", mainGraphId)
                        .build()).extractAs(CreateProductResponse.class);

        CreateServiceResponse createServiceResponse = productCatalogSteps
                .createProductObject("services/", JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "service_for_graph_test_api")
                        .set("graph_id", mainGraphId)
                        .build()).extractAs(CreateServiceResponse.class);

        CreateActionResponse createActionResponse = productCatalogSteps
                .createProductObject("actions/", JsonHelper.getJsonTemplate("productCatalog/actions/createAction.json")
                        .set("name", "action_for_graph_test_api")
                        .set("graph_id", mainGraphId)
                        .build()).extractAs(CreateActionResponse.class);

        String deleteResponse = productCatalogSteps.getDeleteObjectResponse(productName, mainGraphId)
                .assertStatus(400)
                .extractAs(DeleteGraphResponse.class)
                .getErr();
        String version = StringUtils.findByRegex("version: ([0-9.]+)\\)", deleteResponse);
        Assertions.assertEquals("1.0.0", version);
        productCatalogSteps.deleteById("products/", createProductResponse.getId());
        productCatalogSteps.deleteById("services/", createServiceResponse.getId());
        productCatalogSteps.deleteById("actions/", createActionResponse.getId());
        productCatalogSteps.getDeleteObjectResponse(productName, mainGraphId).assertStatus(200);
        productCatalogSteps.getDeleteObjectResponse(productName, secondGraphId).assertStatus(200);
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление графа без токена")
    @Test
    public void deleteGraphWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(productName, graph.getGraphId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление графа")
    @MarkDelete
    public void deleteGraph() {
        try (Graph graph = Graph.builder().name("at_test_graph_api").build().createObjectExclusiveAccess()) {
            graph.deleteObject();
        }
    }
}
