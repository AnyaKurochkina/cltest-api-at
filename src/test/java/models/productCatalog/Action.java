package models.productCatalog;

import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Http;
import httpModels.productCatalog.action.createAction.response.CreateActionResponse;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.productCatalog.graph.Graph;
import org.json.JSONObject;
import steps.productCatalog.ProductCatalogSteps;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
@Builder
@Getter
public class Action extends Entity {
    private String icon;
    private String iconStoreId;
    private String iconUrl;
    private Graph graph;
    private String currentVersion;
    private String jsonTemplate;
    private String actionName;
    private String graphId;
    private String title;
    private String description;
    private String actionId;
    private String version;
    private String type;
    private Boolean isMultiple;
    private String createDt;
    private String updateDt;
    private String locationRestriction;
    private Integer priority;
    private Map<String, String> extraData;
    private final String productName = "/api/v1/actions/";
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("/api/v1/actions/",
            "productCatalog/actions/createAction.json");

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/actions/createAction.json";
        if (graphId == null) {
            Graph graph = Graph.builder().name("graph_for_action_api_test").build().createObject();
            graphId = graph.getGraphId();
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.icon", icon)
                .set("$.icon_url", iconUrl)
                .set("$.icon_store_id", iconStoreId)
                .set("$.name", actionName)
                .set("$.title", title)
                .set("$.type", type)
                .set("$.current_version", currentVersion)
                .set("$.description", description)
                .set("$.graph_id", graphId)
                .set("$.version", version)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.priority", priority)
                .set("$.extra_data", extraData)
                .set("$.location_restriction", locationRestriction)
                .build();
    }

    @Override
    @Step("Создание экшена")
    protected void create() {
        if (productCatalogSteps.isExists(actionName)) {
            productCatalogSteps.deleteByName(actionName, GetActionsListResponse.class);
        }
        actionId = new Http(Configure.ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productName + actionId + "/")
                .assertStatus(204);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        assertFalse(productCatalogSteps.isExists(actionName));
    }
}
