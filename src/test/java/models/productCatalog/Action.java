package models.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.action.createAction.response.CreateActionResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import steps.productCatalog.ProductCatalogSteps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
@Builder
@Getter
public class Action extends Entity {
    private Graph graph;
    private String jsonTemplate;
    private String actionName;
    private String graphId;
    private String title;
    private String description;
    private String actionId;
    private String version;
    private String type;
    private boolean isMultiple;
    private String createDt;
    private String updateDt;
    private String locationRestriction;
    private Integer priority;
    private final String productName = "actions/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/actions/createAction.json";
        Graph graph = Graph.builder().name("graph_for_action_api_test").build().createObject();
        graphId = graph.getGraphId();
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", actionName)
                .set("$.title", title)
                .set("$.type", type)
                .set("$.description", description)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.priority", priority)
                .set("$.location_restriction", locationRestriction)
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
        assertNotNull(actionId, "Экшен с именем: " + actionName + ", не создался");
    }

    @Override
    @Step("Удаление экшена")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + actionId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        assertFalse(productCatalogSteps.isExists(actionName));
    }
}
