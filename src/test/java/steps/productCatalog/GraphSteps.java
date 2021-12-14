package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.Graphs.existsGraphs.response.ExistsGraphsResponse;
import httpModels.productCatalog.Graphs.getGraph.response.GetGraphResponse;
import httpModels.productCatalog.Graphs.getGraphsList.response.GetGraphsListResponse;
import httpModels.productCatalog.Graphs.getGraphsList.response.ListItem;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GraphSteps {

    public String getGraphId(String graphName) {
        String graphId = null;
        GetGraphsListResponse response = new Http(Configure.ProductCatalogURL)
                .get("graphs/?include=total_count&page=1&per_page=10")
                .assertStatus(200)
                .extractAs(GetGraphsListResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(graphName)) {
                graphId = listItem.getId();
            }
        }
        return graphId;
    }

    @SneakyThrows
    @Step("Получение списка графов")
    public List<ListItem> getGraphsList() {
        return new Http(Configure.ProductCatalogURL)
                .get("graphs/")
                .assertStatus(200).extractAs(GetGraphsListResponse.class).getList();
    }

    @SneakyThrows
    @Step("Проверка существования графа по имени")
    public boolean isExist(String name) {
        return new Http(Configure.ProductCatalogURL)
                .get("graphs/exists/?name=" + name)
                .assertStatus(200)
                .extractAs(ExistsGraphsResponse.class)
                .isExists();
    }

    @SneakyThrows
    @Step("Импорт графа")
    public void importGraph(String pathName) {
        Response response = given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(pathName))
                .when()
                .post("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/product-catalog/graphs/obj_import/");
        Assertions.assertEquals(200, response.getStatusCode());
    }

    @SneakyThrows
    @Step("Получение графа по Id")
    public GetGraphResponse getGraphById(String id) {
        return new Http(Configure.ProductCatalogURL)
                .get("graphs/" + id + "/")
                .assertStatus(200)
                .extractAs(GetGraphResponse.class);
    }

    @SneakyThrows
    @Step("Копирование графа по Id")
    public void copyGraphById(String id) {
        new Http(Configure.ProductCatalogURL)
                .post("graphs/" + id + "/copy/")
                .assertStatus(200);
    }

    @SneakyThrows
    @Step("Частичное обновление графа по Id")
    public void partialUpdateGraphById(String id, JSONObject object) {
        new Http(Configure.ProductCatalogURL)
                .body(object)
                .patch("graphs/" + id + "/")
                .assertStatus(200);
    }

    @Step("Создание JSON объекта по графам")
    public JSONObject createJsonObject(String name) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/graphs/createGraph.json")
                .set("$.name", name)
                .build();
    }

    @SneakyThrows
    @Step("Создание графа")
    public CreateGraphResponse createGraph(JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post("graphs/")
                .assertStatus(201)
                .extractAs(CreateGraphResponse.class);
    }

    @SneakyThrows
    @Step("Создание графа")
    public Http.Response createGraphResponse(JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post("graphs/");
    }

    @SneakyThrows
    @Step("Удаление графа")
    public void deleteGraph(String id) {
        new Http(Configure.ProductCatalogURL)
                .delete("graphs/" + id + "/")
                .assertStatus(200);
    }

    @SneakyThrows
    @Step("Удаление графа")
    public Http.Response deleteGraphResponse(String id) {
        return new Http(Configure.ProductCatalogURL)
                .delete("graphs/" + id + "/");
    }

    @Step("Удаление графа по имени")
    public void deleteGraphByName(String name) {
        deleteGraph(getGraphId(name));
    }
}
