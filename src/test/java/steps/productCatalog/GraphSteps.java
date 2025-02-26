package steps.productCatalog;

import core.enums.Role;
import core.helper.Page;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.graph.GetGraphList;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.productCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductCatalogSteps.getProductCatalogAdmin;
import static tests.routes.GraphProductCatalogApi.*;
import static tests.routes.GraphProductCatalogApiV2.*;

public class GraphSteps extends Steps {

    private static final String graphUrl = "/api/v1/graphs/";

    @Step("Получение списка Графов продуктового каталога")
    public static List<Graph> getGraphList() {
        return getProductCatalogAdmin()
                .api(apiV1GraphsList)
                .extractAs(GetGraphList.class).getList();
    }

    @Step("Получение Meta данных списка графов продуктового каталога")
    public static Page.Meta getMetaGraphList() {
        return getProductCatalogAdmin()
                .api(apiV1GraphsList)
                .extractAs(GetGraphList.class).getMeta();
    }

    @Step("Получение массива объектов использующих граф")
    public static Response getObjectArrayUsedGraph(String id) {
        return getProductCatalogAdmin()
                .api(apiV1GraphsUsedRead, id);
    }

    @Step("Получение массива объектов использующих граф c query параметром {query}")
    public static Response getObjectArrayUsedGraph(String id, QueryBuilder query) {
        return getProductCatalogAdmin()
                .api(apiV1GraphsUsedRead, id, query);
    }

    @Step("Частичное обновление графа")
    public static Response partialUpdateGraph(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(graphUrl + id + "/");
    }

    @Step("Частичное обновление графа в контексте")
    public static void partialUpdateGraphInContext(String id, JSONObject object, String projectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch("/api/v1/projects/{}/graphs/{}/", projectId, id);
    }

    @Step("Частичное обновление графа по имени {name}")
    public static void partialUpdateGraphByName(String name, JSONObject object) {
        getProductCatalogAdmin()
                .body(object)
                .api(apiV2GraphsPartialUpdate, name);
    }

    @Step("Создание графа")
    public static Response createGraph(JSONObject body) {
        return getProductCatalogAdmin()
                .body(body)
                .api(apiV1GraphsCreate);
    }

    public static Graph createGraph(String name) {
        Graph graph = Graph.builder()
                .name(name)
                .build();
        return createGraph(graph);
    }

    public static Graph createGraph(String name, String title) {
        Graph graph = Graph.builder()
                .name(name)
                .title(title)
                .build();
        return createGraph(graph);
    }

    public static Graph createGraph() {
        Graph graph = Graph.builder()
                .name(StringUtils.getRandomStringApi(7))
                .build();
        return createGraph(graph);
    }

    @Step("Создание графа c именем {graph.name}")
    public static Graph createGraph(Graph graph) {
        return getProductCatalogAdmin()
                .body(graph.toJson())
                .api(apiV1GraphsCreate)
                .extractAs(Graph.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Получение графа по Id {graphId}")
    public static Graph getGraphById(String graphId) {
        return getProductCatalogAdmin()
                .api(apiV1GraphsRead, graphId)
                .assertStatus(200)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по Id {objectId}")
    public static Response getGraphByIdResponse(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + objectId + "/");
    }

    @Step("Получение графа по имени {name}")
    public static Graph getGraphByName(String name) {
        return getProductCatalogAdmin()
                .api(apiV2GraphsRead, name)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по Id и query параметрами {queryBuilder}")
    public static Graph getGraphByIdWithQueryParams(String graphId, QueryBuilder queryBuilder) {
        return getProductCatalogAdmin()
                .api(apiV1GraphsRead, graphId, queryBuilder)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по имени {name}")
    public static Graph getGraphByNameFilter(String name) {
        List<Graph> graphList = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?name={}", name)
                .extractAs(GetGraphList.class).getList();
        assertEquals(1, graphList.size(), "Размер списка должен быть 1");
        return graphList.get(0);
    }

    @Step("Проверка существования графа по имени {name}")
    public static boolean isGraphExists(String name) {
        return getProductCatalogAdmin()
                .api(apiV1GraphsExists, new QueryBuilder().add("name", name))
                .jsonPath().get("exists");
    }

    @Step("Удаление графа по Id {graphId}")
    public static void deleteGraphById(String graphId) {
        getProductCatalogAdmin()
                .api(apiV1GraphsDelete, graphId);
    }

    @Step("Удаление графа по имени {name}")
    public static void deleteGraphByName(String name) {
        getProductCatalogAdmin()
                .api(apiV2GraphsDelete, name);
    }

    @Step("Удаление графа по Id в контексте")
    public static void deleteGraphByIdInContext(String objectId, String projectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete("/api/v1/projects/{}/graphs/{}/", projectId, objectId)
                .assertStatus(204);
    }

    @Step("Удаление графа по Id")
    public static Response getDeleteResponse(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(graphUrl + objectId + "/");
    }

    @Step("Импорт графа")
    public static ImportObject importGraph(String pathName) {
        return getProductCatalogAdmin()
                .multiPart(graphUrl + "obj_import/", "file", new File(pathName))
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Экспорт графа по Id")
    public static Response exportGraphById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Получение графа по Id и Env")
    public static Graph getGraphByIdAndEnv(String objectId, String env) {
        return new Http(productCatalogURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(graphUrl + objectId + "/?env={}", env)
                .extractAs(Graph.class);
    }

    @Step("Получение списка графов отсортированного по дате создания")
    public static List<Graph> getGraphListOrdering(String filter) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?ordering={}", filter)
                .assertStatus(200)
                .extractAs(GetGraphList.class)
                .getList();
    }

    @Step("Получение графа по имени с публичным токеном")
    public static Response getGraphByNameWithPublicToken(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(graphUrl + "?name=" + name)
                .assertStatus(200);
    }

    @Step("Загрузка графа в Gitlab")
    public static Response dumpGraphToBitbucket(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(graphUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка графа из Gitlab")
    public static Response loadGraphFromBitbucket(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(graphUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Создание графа с публичным токеном")
    public static Response createGraphWithPublicToken(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(graphUrl);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public static Response partialUpdateGraphWithPublicToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(graphUrl + id + "/");
    }

    @Step("Обновление всего графа по Id с публичным токеном")
    public static Response putGraphByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(graphUrl + objectId + "/");
    }

    @Step("Удаление графа с публичным токеном")
    public static Response deleteGraphWithPublicToken(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(graphUrl + id + "/");
    }

    @Step("Копирование графа по Id")
    public static Graph copyGraphById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(graphUrl + objectId + "/copy/")
                .assertStatus(201)
                .extractAs(Graph.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Копирование графа по имени {name}")
    public static Graph copyGraphByName(String name) {
        return getProductCatalogAdmin()
                .api(apiV2GraphsCopy, name)
                .extractAs(Graph.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Копирование графа по Id в контексте")
    public static void copyGraphByIdInContext(String objectId, String projectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post("/api/v1/projects/{}/graphs/{}", projectId, objectId + "/copy/")
                .assertStatus(201);
    }

    @Step("Получение графа по Id и контексту")
    public static Graph getGraphByIdContext(String projectId, String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/graphs/{}/", projectId, objectId)
                .assertStatus(200)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по Id и контексту")
    public static Response getResponseGraphByIdContext(String projectId, String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/graphs/{}/", projectId, objectId)
                .assertStatus(400);
    }

    @Step("Удаление графа по Id без токена")
    public static Response deleteGraphByIdWithOutToken(String id) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .delete(graphUrl + id + "/").assertStatus(401);
    }

    @Step("Получение графа по Id без токена")
    public static Response getGraphByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + objectId + "/").assertStatus(401);
    }

    @Step("Копирование графа по Id без ключа")
    public static String copyGraphByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(graphUrl + objectId + "/copy/")
                .assertStatus(401).jsonPath().getString("error.message");
    }

    @Step("Частичное обновление графа без токена")
    public static String partialUpdateGraphWithOutToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(graphUrl + id + "/")
                .assertStatus(401).jsonPath().getString("error.message");
    }

    @Step("Получение списка графов по Id")
    public static Response getGraphListById(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?id=" + id);
    }

    @Step("Получение списка графов по нескольким Id")
    public static List<Graph> getGraphListByIds(String... id) {
        String ids = String.join(",", id);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?id__in=" + ids)
                .assertStatus(200)
                .extractAs(GetGraphList.class).getList();
    }

    @Step("Получение списка графов по фильтру Id содержит")
    public static List<Graph> getGraphListByContainsId(String value) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?id__contains=" + value)
                .assertStatus(200)
                .extractAs(GetGraphList.class).getList();
    }

    @Step("Получение списка графов используя multisearch")
    public static List<Graph> getGraphListWithMultiSearch(String str) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?multisearch=" + str)
                .assertStatus(200)
                .extractAs(GetGraphList.class).getList();
    }

    @Step("Получение списка версий графов")
    public static Response getGraphVersionList(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + id + "/version_list/")
                .assertStatus(200);
    }

    @Step("Добавление списка Тегов графам")
    public static void addTagListToGraph(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(graphUrl + "add_tag_list/?name__in=" + names)
                .assertStatus(201);
    }

    @Step("Удаление списка Тегов графов")
    public static void removeTagListFromGraph(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(graphUrl + "remove_tag_list/?name__in=" + names)
                .assertStatus(204);
    }

    @Step("Получение списка графов по фильтру")
    public static List<Graph> getGraphListByFilter(String filter, Object value) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?{}={}", filter, value)
                .assertStatus(200)
                .extractAs(GetGraphList.class)
                .getList();
    }

    @Step("Получение списка графов по фильтрам")
    public static List<Graph> getGraphListByFilters(String... filter) {
        String filters = String.join("&", filter);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?" + filters)
                .assertStatus(200)
                .extractAs(GetGraphList.class)
                .getList();
    }

    @Step("Получение списков Input Output графа")
    public static List<Graph> getGraphInputOutputs(String graphName) {
        return getProductCatalogAdmin()
                .api(apiV2GraphsInputVars, graphName)
                .extractAs(GetGraphList.class)
                .getList();
    }
}
