package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.action.createAction.response.CreateActionResponse;
import httpModels.productCatalog.graphs.getGraphsList.response.GetGraphsListResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ProductCatalogSteps;

@Log4j2
@Builder
@Getter
public class Action extends Entity {
    private Graph graph;
    private String jsonTemplate;
    private String actionName;
    private String graphId;
    private String actionId;
    private String version;
    private final String productName = "actions/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/actions/createAction.json";
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("graphs/", "productCatalog/graphs/createGraph.json");
        graphId = productCatalogSteps
                .getProductObjectIdByNameWithMultiSearch("graph_for_api_test", GetGraphsListResponse.class);
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .build();
    }

    @Override
    @Step("Создание экшена")
    protected void create() {
        actionId = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateActionResponse.class)
                .getId();
        Assertions.assertNotNull(actionId, "Экшен с именем: " + actionName + ", не создался");
    }

    @Override
    @Step("Удаление экшена")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + actionId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(actionName));
    }
}
