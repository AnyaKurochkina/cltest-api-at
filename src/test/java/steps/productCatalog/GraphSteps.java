package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class GraphSteps extends Steps {

    private static final String graphUrl = "/api/v1/graphs/";

    @Step("Получение списка Графов продуктового каталога")
    public static List<ItemImpl> getGraphList() {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl)
                .compareWithJsonSchema("jsonSchema/getGraphListSchema.json")
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class)).getItemsList();
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

    @Step("Проверка существования графа по имени")
    public static boolean isGraphExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(graphUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }
}
