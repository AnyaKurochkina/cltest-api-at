package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
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
}
