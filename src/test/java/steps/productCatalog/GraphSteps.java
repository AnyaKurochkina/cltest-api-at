package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.graph.GetGraphList;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSteps extends Steps {

    private static final String graphUrl = "/api/v1/graphs/";

    @Step("Получение списка Графов продуктового каталога")
    public static List<Graph> getGraphList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl)
                .compareWithJsonSchema("jsonSchema/getGraphListSchema.json")
                .assertStatus(200)
                .extractAs(GetGraphList.class).getList();
    }

    @Step("Получение массива объектов использующих граф")
    public static JsonPath getObjectArrayUsedGraph(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + id + "/used/")
                .compareWithJsonSchema("jsonSchema/usedGraphListSchema.json")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение массива объектов определенного типа использующих граф")
    public static Response getObjectTypeUsedGraph(String id, String... objType) {
        String types = String.join(",", objType);
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + id + "/used/?obj_type=" + types)
                .compareWithJsonSchema("jsonSchema/usedGraphListSchema.json")
                .assertStatus(200);
    }

    @Step("Получение списка последних созданных объектов использующих граф")
    public static Response getLastObjectUsedGraph(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + id + "/used/?last_object=true")
                .compareWithJsonSchema("jsonSchema/usedGraphListSchema.json")
                .assertStatus(200);
    }

    @Step("Получение списка последних версий объектов использующих граф")
    public static Response getLastVersionUsedGraph(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + id + "/used/?last_version=true")
                .compareWithJsonSchema("jsonSchema/usedGraphListSchema.json")
                .assertStatus(200);
    }

    @Step("Частичное обновление графа")
    public static Response partialUpdateGraph(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(graphUrl + id + "/");
    }

    @Step("Создание графа")
    public static Graph createGraph(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(graphUrl)
                .assertStatus(201)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по Id")
    public static Graph getGraphById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + objectId + "/")
                .extractAs(Graph.class);
    }

    @Step("Получение графа по Id и фильтру {filter}")
    public static Graph getGraphByIdAndFilter(String objectId, String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + objectId + "/?{}", filter)
                .extractAs(Graph.class);
    }

    @Step("Получение графа по имени {name}")
    public static Graph getGraphByName(String name) {
        List<Graph> graphList = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?name={}", name)
                .extractAs(GetGraphList.class).getList();
        assertEquals(1, graphList.size(), "Размер списка должен быть 1");
        return graphList.get(0);
    }

    @Step("Проверка существования графа по имени")
    public static boolean isGraphExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление графа по Id")
    public static void deleteGraphById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(graphUrl + objectId + "/")
                .assertStatus(204);
    }

    @Step("Удаление графа по Id")
    public static Response getDeleteResponse(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(graphUrl + objectId + "/");
    }

    @Step("Импорт графа")
    public static void importGraph(String pathName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(graphUrl + "obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Получение графа по Id и Env")
    public static Graph getGraphByIdAndEnv(String objectId, String env) {
        return new Http(ProductCatalogURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(graphUrl + objectId + "/?env={}", env)
                .extractAs(Graph.class);
    }

    @Step("Получение списка графов отсортированного по дате создания")
    public static List<Graph> getGraphListOrdering(String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "?ordering={}", filter)
                .assertStatus(200)
                .extractAs(GetGraphList.class)
                .getList();
    }

    @Step("Получение графа по имени с публичным токеном")
    public static Response getGraphByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(graphUrl + "?name=" + name)
                .assertStatus(200);
    }

    @Step("Загрузка графа в Gitlab")
    public static Response dumpGraphToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(graphUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка графа из Gitlab")
    public static Response loadGraphFromBitbucket(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(graphUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Создание графа с публичным токеном")
    public static Response createGraphWithPublicToken(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(graphUrl);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public static Response partialUpdateGraphWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(graphUrl + id + "/");
    }

    @Step("Обновление всего графа по Id с публичным токеном")
    public static Response putGraphByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(graphUrl + objectId + "/");
    }

    @Step("Удаление графа с публичным токеном")
    public static Response deleteGraphWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(graphUrl + id + "/");
    }

    @Step("Копирование графа по Id")
    public static void copyGraphById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(graphUrl + objectId + "/copy/")
                .assertStatus(200);
    }
}
