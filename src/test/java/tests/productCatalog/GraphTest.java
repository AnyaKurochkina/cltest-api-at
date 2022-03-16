package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.action.createAction.response.CreateActionResponse;
import httpModels.productCatalog.graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.graphs.deleteGraph.response.DeleteGraphResponse;
import httpModels.productCatalog.graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.product.createProduct.response.CreateProductResponse;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
public class GraphTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("graphs/", "productCatalog/graphs/createGraph.json");
    Graph graph;

    @Order(1)
    @DisplayName("Создание графа")
    @TmsLink("642536")
    @Test
    public void createGraph() {
        graph = Graph.builder().name("at_test_graph_api").version("1.0.0").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка графа")
    @TmsLink("642539")
    @Test
    public void getGraphsList() {
        Assertions.assertTrue(productCatalogSteps
                .getProductObjectList(GetGraphsListResponse.class).size() > 0);
    }

    @Order(2)
    @DisplayName("Проверка значения next в запросе на получение списка графа")
    @TmsLink("679029")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetGraphsListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @Order(3)
    @DisplayName("Проверка существования графа по имени")
    @TmsLink("642540")
    @Test
    public void checkGraphExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(graph.getName()));
        Assertions.assertFalse(productCatalogSteps.isExists("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Импорт графа")
    @TmsLink("642628")
    @Test
    public void importGraph() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.json.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json");
        Assertions.assertTrue(productCatalogSteps.isExists(graphName));
        productCatalogSteps.getDeleteObjectResponse(productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch(graphName, GetGraphsListResponse.class)).assertStatus(200);
        Assertions.assertFalse(productCatalogSteps.isExists(graphName));
    }

    @Order(5)
    @DisplayName("Получение графа по Id")
    @TmsLink("642631")
    @Test
    public void getGraphById() {
        GetImpl getImpl = productCatalogSteps.getById(graph.getGraphId(), GetGraphResponse.class);
        Assertions.assertEquals(graph.getName(), getImpl.getName());
    }

    @Order(6)
    @DisplayName("Негативный тест на получение графа по Id без токена")
    @TmsLink("642636")
    @Test
    public void getGraphByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(graph.getGraphId());
    }

    @Order(9)
    @DisplayName("Проверка сортировки по дате создания в графах")
    @TmsLink("740080")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByCreateData(GetGraphsListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(10)
    @DisplayName("Проверка сортировки по дате обновления в графах")
    @TmsLink("740082")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByUpDateData(GetGraphsListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(11)
    @DisplayName("Проверка доступа для методов с публичным ключом в графах")
    @TmsLink("740085")
    @Test
    public void checkAccessWithPublicToken() {
        productCatalogSteps.getObjectByNameWithPublicToken(graph.getName()).assertStatus(200);
        productCatalogSteps.createProductObjectWithPublicToken(productCatalogSteps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.partialUpdateObjectWithPublicToken(graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        productCatalogSteps.putObjectByIdWithPublicToken(graph.getGraphId(), productCatalogSteps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.deleteObjectWithPublicToken(graph.getGraphId()).assertStatus(403);
    }

    @Order(50)
    @DisplayName("Копирование графа по Id")
    @TmsLink("642640")
    @Test
    public void copyGraphById() {
        String cloneName = graph.getName() + "-clone";
        productCatalogSteps.copyById(graph.getGraphId());
        Assertions.assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.getDeleteObjectResponse(
                        productCatalogSteps.getProductObjectIdByNameWithMultiSearch(cloneName, GetGraphsListResponse.class))
                .assertStatus(200);
        Assertions.assertFalse(productCatalogSteps.isExists(cloneName));
    }

    @Order(51)
    @DisplayName("Негативный тест на копирование графа по Id без токена")
    @TmsLink("642645")
    @Test
    public void copyGraphByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(graph.getGraphId());
    }

    @Order(60)
    @DisplayName("Частичное обновление графа по Id")
    @TmsLink("642650")
    @Test
    public void partialUpdateGraph() {
        String expectedDescription = "UpdateDescription";
        String oldGraphVersion = graph.getVersion();
        productCatalogSteps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getGraphResponse = productCatalogSteps.getById(graph.getGraphId(), GetGraphResponse.class);
        String actualDescription = getGraphResponse.getDescription();
        String newGraphVersion = getGraphResponse.getVersion();
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertNotEquals(oldGraphVersion, newGraphVersion);
    }

    @Order(61)
    @DisplayName("Негативный тест на обновление графа по Id без токена")
    @TmsLink("642662")
    @Test
    public void updateGraphByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(70)
    @DisplayName("Негативный тест на попытку обновления графа до текущей версии")
    @TmsLink("642668")
    @Test
    public void partialUpdateForCurrentVersion() {
        String currentVersion = graph.getVersion();
        productCatalogSteps.partialUpdateObject(graph.getGraphId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(80)
    @DisplayName("Негативный тест на создание графа с существующим именем")
    @TmsLink("642678")
    @Test
    public void createGraphWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps
                .createJsonObject(graph.getName())).assertStatus(400);
    }

    @Order(89)
    @DisplayName("Обновление графа с указанием версии в граничных значениях")
    @TmsLink("642680")
    @Test
    public void updateGraphAndGetVersion() {
        Graph graphTest = Graph.builder().name("graph_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api4"));
        currentVersion = productCatalogSteps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api6"))
                .assertStatus(500);
    }

    @Order(90)
    @DisplayName("Получение списка объектов использующих граф")
    @TmsLink("642681")
    @Test
    public void getUsedGraphList() {
        CreateGraphResponse usedGraphApi = productCatalogSteps.createProductObject(productCatalogSteps
                .createJsonObject("used_graph_api")).extractAs(CreateGraphResponse.class);
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
        productCatalogSteps.getDeleteObjectResponse(usedGraphId).assertStatus(200);
    }

    @Order(97)
    @DisplayName("Проверка отсутсвия ' в значениях ключя template_id")
    @TmsLink("642683")
    @Test
    public void checkKeys() {
        productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("api_test"));
        productCatalogSteps.partialUpdateObject(productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class), JsonHelper.getJsonTemplate("productCatalog/graphs/patch.json")
                .build());
        String id = productCatalogSteps.getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class);
        JsonPath jsonPath = productCatalogSteps.getJsonPath(id);
        assertFalse(jsonPath.getString("graph[0].template_id").contains("'"));
        productCatalogSteps.getDeleteObjectResponse(productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class)).assertStatus(200);
    }

    @Order(98)
    @DisplayName("Попытка удаления графа используемого в продукте, действии и сервисе")
    @TmsLink("642692")
    @Test
    public void deleteUsedGraph() {
        CreateGraphResponse mainGraph = productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("main_graph"))
                .extractAs(CreateGraphResponse.class);
        String mainGraphId = mainGraph.getId();

        CreateGraphResponse secondGraph = productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("second_graph"))
                .extractAs(CreateGraphResponse.class);
        String secondGraphId = secondGraph.getId();

        productCatalogSteps.partialUpdateObject(mainGraphId, new JSONObject().put("description", "updateVersion2.0")
                .put("version", "2.0.0"));
        productCatalogSteps.partialUpdateObject(mainGraphId, new JSONObject().put("description", "updateVersion3.0")
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

        String deleteResponse = productCatalogSteps.getDeleteObjectResponse(mainGraphId)
                .assertStatus(400)
                .extractAs(DeleteGraphResponse.class)
                .getErr();
        String version = StringUtils.findByRegex("version: ([0-9.]+)\\)", deleteResponse);
        Assertions.assertEquals("1.0.0", version);
        productCatalogSteps.deleteById("products/", createProductResponse.getId());
        productCatalogSteps.deleteById("services/", createServiceResponse.getId());
        productCatalogSteps.deleteById("actions/", createActionResponse.getId());
        productCatalogSteps.getDeleteObjectResponse(mainGraphId).assertStatus(200);
        productCatalogSteps.getDeleteObjectResponse(secondGraphId).assertStatus(200);
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление графа без токена")
    @TmsLink("642695")
    @Test
    public void deleteGraphWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(graph.getGraphId());
    }

    @Order(100)
    @DisplayName("Удаление графа")
    @TmsLink("642697")
    @Test
    public void deleteGraph() {
        graph.deleteObject();
    }
}
