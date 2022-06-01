package tests.productCatalog.graph;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.graphs.deleteGraph.response.DeleteGraphResponse;
import httpModels.productCatalog.graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.*;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/graphs/",
            "productCatalog/graphs/createGraph.json");

    @DisplayName("Создание графа")
    @TmsLink("642536")
    @Test
    public void createGraph() {
        Graph graph = Graph.builder()
                .name("create_graph_test_api")
                .version("1.0.0")
                .build()
                .createObject();
        GetImpl createdGraph = steps.getById(graph.getGraphId(), GetGraphResponse.class);
        assertEquals(graph.getName(), createdGraph.getName());
    }

    @DisplayName("Проверка существования графа по имени")
    @TmsLink("642540")
    @Test
    public void checkGraphExists() {
        Graph graph = Graph.builder()
                .name("graph_check_exist_test_api")
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(graph.getName()));
        Assertions.assertFalse(steps.isExists("NoExistsAction"));
    }

    @DisplayName("Импорт графа")
    @TmsLink("642628")
    @Test
    public void importGraph() {
        String data = JsonHelper.getStringFromFile("/productCatalog/graphs/importGraph.json");
        String graphName = new JsonPath(data).get("Graph.json.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json");
        Assertions.assertTrue(steps.isExists(graphName));
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(graphName, GetGraphsListResponse.class)).assertStatus(200);
        Assertions.assertFalse(steps.isExists(graphName));
    }

    @DisplayName("Получение графа по Id")
    @TmsLink("642631")
    @Test
    public void getGraphById() {
        Graph graph = Graph.builder()
                .name("graph_get_by_id_test_api")
                .build()
                .createObject();
        GetImpl getImpl = steps.getById(graph.getGraphId(), GetGraphResponse.class);
        Assertions.assertEquals(graph.getName(), getImpl.getName());
    }

    @DisplayName("Проверка сортировки по дате создания в графах")
    @TmsLink("740080")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetGraphsListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в графах")
    @TmsLink("740082")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetGraphsListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в графах")
    @TmsLink("740085")
    @Test
    public void checkAccessWithPublicToken() {
        Graph graph = Graph.builder()
                .name("graph_get_by_name_with_public_token_test_api")
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(graph.getName()).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(graph.getGraphId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(graph.getGraphId()).assertStatus(403);
    }

    @DisplayName("Копирование графа по Id")
    @TmsLink("642640")
    @Test
    public void copyGraphById() {
        Graph graph = Graph.builder()
                .name("graph_clone_test_api")
                .build()
                .createObject();
        String cloneName = graph.getName() + "-clone";
        steps.copyById(graph.getGraphId());
        Assertions.assertTrue(steps.isExists(cloneName));
        steps.getDeleteObjectResponse(
                        steps.getProductObjectIdByNameWithMultiSearch(cloneName, GetGraphsListResponse.class))
                .assertStatus(200);
        Assertions.assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Частичное обновление графа по Id")
    @TmsLink("642650")
    @Test
    public void partialUpdateGraph() {
        Graph graph = Graph.builder()
                .name("partial_update_graph_test_api")
                .version("1.0.0")
                .build()
                .createObject();
        String expectedDescription = "UpdateDescription";
        String oldGraphVersion = graph.getVersion();
        steps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getGraphResponse = steps.getById(graph.getGraphId(), GetGraphResponse.class);
        String actualDescription = getGraphResponse.getDescription();
        String newGraphVersion = getGraphResponse.getVersion();
        Assertions.assertEquals(expectedDescription, actualDescription);
        Assertions.assertNotEquals(oldGraphVersion, newGraphVersion);
    }


    @DisplayName("Обновление графа с указанием версии в граничных значениях")
    @TmsLink("642680")
    @Test
    public void updateGraphAndGetVersion() {
        Graph graphTest = Graph.builder()
                .name("update_graph_check_version_test_api")
                .version("1.0.999")
                .build()
                .createObject();
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api2"));
        String currentVersion = steps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api3")
                .put("version", "1.999.999"));
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api4"));
        currentVersion = steps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api5")
                .put("version", "999.999.999"));
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("name", "graph_version_test_api6"))
                .assertStatus(500);
    }

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

        Services createServiceResponse = Services.builder()
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

        JsonPath jsonPath = steps.getObjectArrayUsedGraph(usedGraphId);
        assertAll(
                () -> assertEquals(createProductResponse.getProductId(), jsonPath.getString("id[0]")),
                () -> assertEquals(createActionResponse.getActionId(), jsonPath.getString("id[1]")),
                () -> assertEquals(createServiceResponse.getServiceId(), jsonPath.getString("id[2]"))
        );
    }

    @DisplayName("Проверка отсутсвия ' в значениях ключя template_id")
    @TmsLink("642683")
    @Test
    public void checkKeys() {
        steps.createProductObject(steps.createJsonObject("api_test"));
        steps.partialUpdateObject(steps
                .getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class), JsonHelper.getJsonTemplate("productCatalog/graphs/patch.json")
                .build());
        String id = steps.getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class);
        JsonPath jsonPath = steps.getJsonPath(id);
        assertFalse(jsonPath.getString("graph[0].template_id").contains("'"));
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class)).assertStatus(200);
    }

    @DisplayName("Попытка удаления графа используемого в продукте, действии и сервисе")
    @TmsLink("642692")
    @Test
    public void deleteUsedGraph() {
        Graph mainGraph = Graph.builder()
                .name("main_graph")
                .build()
                .createObject();
        String mainGraphId = mainGraph.getGraphId();
        steps.partialUpdateObject(mainGraphId, new JSONObject().put("description", "updateVersion2.0")
                .put("version", "2.0.0"));
        steps.partialUpdateObject(mainGraphId, new JSONObject().put("description", "updateVersion3.0")
                .put("version", "3.0.0"));

        Product.builder()
                .name("product_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        Services.builder()
                .serviceName("service_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        Action.builder()
                .actionName("action_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        String deleteResponse = steps.getDeleteObjectResponse(mainGraphId)
                .assertStatus(400)
                .extractAs(DeleteGraphResponse.class)
                .getErr();
        String version = StringUtils.findByRegex("version: ([0-9.]+)\\)", deleteResponse);
        Assertions.assertEquals("1.0.0", version);
    }

    @DisplayName("Удаление графа")
    @TmsLink("642697")
    @Test
    public void deleteGraph() {
        Graph graph = Graph.builder()
                .name("delete_graph_test_api")
                .build()
                .createObject();
        graph.deleteObject();
    }

    @Test
    @DisplayName("Загрузка Graph в GitLab")
    @Disabled
    @TmsLink("")
    public void dumpToGitlabGraph() {
        String graphName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphName)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(graph.getGraphId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
    }
}
