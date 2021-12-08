package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
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

import static core.helper.JsonHelper.convertResponseOnClass;
import static io.restassured.RestAssured.given;

public class GraphSteps {

    private JSONObject toJson(String pathToJsonBody, String graphName) {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(pathToJsonBody)
                .set("$.name", graphName)
                .build();
    }

    public void createGraph(String graphName) {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("graphs/?save_as_next_version=true", toJson("/productCatalog/graphs/createGraph.json", graphName))
                .assertStatus(201)
                .toString();
    }

    public String getGraphId(String graphName){
        String graphId = null;
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("graphs/?include=total_count&page=1&per_page=10")
                .assertStatus(200)
                .toString();

        GetGraphsListResponse response = convertResponseOnClass(object, GetGraphsListResponse.class);

        for(ListItem listItem: response.getList()){
            if (listItem.getName().equals(graphName)){
                graphId = listItem.getId();
            }
        }
        return graphId;
    }

    @SneakyThrows
    @Step("Получение списка графов")
    public List<ListItem> getGraphsList() {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("graphs/")
                .assertStatus(200).toString();
        GetGraphsListResponse getGraphsListResponse = convertResponseOnClass(object, GetGraphsListResponse.class);
        return getGraphsListResponse.getList();
    }
    @SneakyThrows
    @Step("Проверка существования графа по имени")
    public boolean isExist(String name) {
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("graphs/exists/?name=" + name)
                .assertStatus(200)
                .toString();
        ExistsGraphsResponse response = convertResponseOnClass(object, ExistsGraphsResponse.class);
        return response.isExists();
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
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("graphs/" + id + "/")
                .assertStatus(200)
                .toString();
        return convertResponseOnClass(object, GetGraphResponse.class);
    }

    @SneakyThrows
    @Step("Копирование графа по Id")
    public void copyGraphById(String id) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("graphs/" + id + "/copy/")
                .assertStatus(200);
    }
    @SneakyThrows
    @Step("Частичное обновление графа по Id")
    public void partialUpdateGraphById(String id, String key, String value) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("graphs/" + id + "/", new JSONObject().put(key, value))
                .assertStatus(200);
    }
}
