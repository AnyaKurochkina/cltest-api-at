package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.graphs.createGraph.response.JsonSchema;
import httpModels.productCatalog.graphs.createGraph.response.StaticData;
import httpModels.productCatalog.graphs.createGraph.response.UiSchema;
import httpModels.productCatalog.graphs.existsGraphs.response.ExistsGraphsResponse;
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
public class Graph extends Entity {

    private String author;
    private JsonSchema jsonSchema;
    private String name;
    private String description;
    private StaticData staticData;
    private String graphId;
    private String title;
    private String type;
    private UiSchema uiSchema;
    private String version;
    private String jsonTemplate;
    @Builder.Default
    protected transient ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("graphs/", "productCatalog/graphs/createGraph.json");

    private final String productName = "graphs/";

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/graphs/createGraph.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.author", author)
                .set("$.version", version)
                .build();
    }

    @Override
    @Step("Создание графа")
    protected void create() {
        graphId = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post(productName)
                .assertStatus(201)
                .extractAs(CreateGraphResponse.class)
                .getId();
        Assertions.assertNotNull(graphId, "Граф с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление графа")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .delete(productName + graphId + "/")
                .assertStatus(200);
        ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps(productName, jsonTemplate);
        Assertions.assertFalse(productCatalogSteps.isExists(name, ExistsGraphsResponse.class));
    }
}
