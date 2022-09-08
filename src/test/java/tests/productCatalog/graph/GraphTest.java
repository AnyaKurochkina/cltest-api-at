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
import models.productCatalog.action.Action;
import models.productCatalog.Env;
import models.productCatalog.Services;
import models.productCatalog.graph.Graph;
import models.productCatalog.graph.Modification;
import models.productCatalog.graph.RootPath;
import models.productCatalog.graph.UpdateType;
import models.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Tag("Graphs")
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
        String graphName = new JsonPath(data).get("Graph.name");
        if (steps.isExists(graphName)) {
            steps.deleteByName(graphName, GetGraphsListResponse.class);
        }
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/graphs/importGraph.json");
        Assertions.assertTrue(steps.isExists(graphName));
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(graphName, GetGraphsListResponse.class)).assertStatus(204);
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

    @DisplayName("Получение значения поля lock_order_on_error и проверка версии при изменении")
    @TmsLink("1022491")
    @Test
    public void getLockOrderOnErrorAndUpdate() {
        Graph graph = Graph.builder()
                .name("get_lock_order_on_error")
                .lockOrderOnError(false)
                .build()
                .createObject();
        String id = graph.getGraphId();
        GetGraphResponse getGraph = (GetGraphResponse) steps.getById(id, GetGraphResponse.class);
        assertFalse(getGraph.getLockOrderOnError());
        steps.partialUpdateObject(id, new JSONObject().put("lock_order_on_error", true));
        GetGraphResponse getUpdatedGraph = (GetGraphResponse) steps.getById(id, GetGraphResponse.class);
        assertTrue(getUpdatedGraph.getLockOrderOnError());
        String newVersion = getUpdatedGraph.getVersion();
        assertEquals("1.0.1", newVersion);
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
                .assertStatus(204);
        Assertions.assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Частичное обновление графа по Id")
    @TmsLink("642650")
    @Test
    public void partialUpdateGraph() {
        Graph graph = Graph.builder()
                .name("partial_update_graph_test_api")
                .version("1.0.0")
                .damageOrderOnError(false)
                .build()
                .createObject();
        String oldGraphVersion = graph.getVersion();
        steps.partialUpdateObject(graph.getGraphId(), new JSONObject()
                .put("damage_order_on_error", true)).assertStatus(200);
        GetGraphResponse getGraphResponse = (GetGraphResponse) steps.getById(graph.getGraphId(), GetGraphResponse.class);
        Boolean damageOrderOnError = getGraphResponse.getDamageOrderOnError();
        String newGraphVersion = getGraphResponse.getVersion();
        Assertions.assertEquals(true, damageOrderOnError);
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
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true));
        String currentVersion = steps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", false)
                .put("version", "1.999.999"));
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true));
        currentVersion = steps.getById(graphTest.getGraphId(), GetGraphResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", false)
                .put("version", "999.999.999"));
        steps.partialUpdateObject(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true))
                .assertStatus(500);
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
                .getProductObjectIdByNameWithMultiSearch("api_test", GetGraphsListResponse.class)).assertStatus(204);
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
    @TmsLink("821972")
    public void dumpToGitlabGraph() {
        String graphName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "graph_" + graphName + "_" + graph.getVersion();
        Response response = steps.dumpToBitbucket(graph.getGraphId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка Graph из GitLab")
    @TmsLink("1028898")
    public void loadFromGitlabGraph() {
        String graphName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Graph.builder()
                .name(graphName)
                .title(graphName)
                .version("1.0.0")
                .build()
                .init().toJson();
        GetGraphResponse graph = steps.createProductObject(jsonObject).extractAs(GetGraphResponse.class);
        Response response = steps.dumpToBitbucket(graph.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(graphName, GetGraphsListResponse.class);
        String path = "graph_" + graphName + "_" + graph.getVersion();
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(graphName));
        steps.deleteByName(graphName, GetGraphsListResponse.class);
        assertFalse(steps.isExists(graphName));
    }

    @DisplayName("Передача и получение значений allowed_developers")
    @TmsLink("1027311")
    @Test
    public void getAllowedDevelopers() {
        List<String> allowedDevelopersList = Arrays.asList("allowed_developer1", "allowed_developer2");
        Graph graph = Graph.builder()
                .name("graph_get_allowed_developers_test_api")
                .allowedDevelopers(allowedDevelopersList)
                .build()
                .createObject();
        GetGraphResponse createdGraph = (GetGraphResponse) steps.getById(graph.getGraphId(), GetGraphResponse.class);
        assertEquals(allowedDevelopersList, createdGraph.getAllowedDevelopers());
    }

    @DisplayName("Передача и получение значений restricted_developers")
    @TmsLink("1027313")
    @Test
    public void getRestrictedDevelopers() {
        List<String> restrictedDevelopersList = Arrays.asList("restricted_developer1", "restricted_developer2");
        Graph graph = Graph.builder()
                .name("graph_get_restricted_developers_test_api")
                .restrictedDevelopers(restrictedDevelopersList)
                .build()
                .createObject();
        GetGraphResponse createdGraph = (GetGraphResponse) steps.getById(graph.getGraphId(), GetGraphResponse.class);
        assertEquals(restrictedDevelopersList, createdGraph.getRestrictedDevelopers());
    }

    @DisplayName("Создание графа с модификацией и способом изменения delete")
    @TmsLink("1050995")
    @Test
    public void createGraphWithUpdateTypeDelete() {
        Modification mod = Modification.builder()
                .name("mod1")
                .data(new LinkedHashMap<String, Object>() {{
                    put("test", "test");
                }})
                .envs(Arrays.asList(Env.TEST, Env.DEV))
                .order(1)
                .path("")
                .updateType(UpdateType.DELETE)
                .rootPath(RootPath.UI_SCHEMA)
                .build();
        JsonHelper.toJson(mod);
        Graph graph = Graph.builder()
                .name("create_graph_with_update_type_delete")
                .modifications(Collections.singletonList(mod))
                .build()
                .createObject();
        GetGraphResponse actualGraph = (GetGraphResponse) steps.getById(graph.getGraphId(), GetGraphResponse.class);
        assertEquals(UpdateType.DELETE, actualGraph.getModifications().get(0).getUpdateType());
    }

    @DisplayName("Копирование графа с модификацией")
    @TmsLink("1050996")
    @Test
    public void copyGraphWithModification() {
        String modName = "mod1";
        Modification mod = Modification.builder()
                .name(modName)
                .data(new LinkedHashMap<String, Object>() {{
                    put("test", "test");
                }})
                .envs(Arrays.asList(Env.TEST, Env.DEV))
                .order(1)
                .path("")
                .updateType(UpdateType.DELETE)
                .rootPath(RootPath.UI_SCHEMA)
                .build();
        String name = "copy_graph_with_modification";
        Graph graph = Graph.builder()
                .name("copy_graph_with_modification")
                .modifications(Collections.singletonList(mod))
                .build()
                .createObject();
        steps.copyById(graph.getGraphId());
        String copyGraphId = steps.getProductObjectIdByNameWithMultiSearch(name + "-clone", GetGraphsListResponse.class);
        GetGraphResponse copyGraph = (GetGraphResponse) steps.getById(copyGraphId, GetGraphResponse.class);
        assertEquals(modName, copyGraph.getModifications().get(0).getName());
        steps.deleteById(copyGraphId);
    }
}
