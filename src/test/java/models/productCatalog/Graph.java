package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Graphs.createGraph.response.CreateGraphResponse;
import httpModels.productCatalog.Graphs.createGraph.response.JsonSchema;
import httpModels.productCatalog.Graphs.createGraph.response.StaticData;
import httpModels.productCatalog.Graphs.createGraph.response.UiSchema;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;

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
    protected transient GraphSteps graphSteps = new GraphSteps();

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/graphs/createGraph.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.author", author)
                .build();
    }

    @Override
    @Step("Создание графа")
    protected void create() {
        CreateGraphResponse createGraphResponse = new Http(Configure.ProductCatalogURL)
                .body(toJson())
                .post("graphs/")
                .assertStatus(201)
                .extractAs(CreateGraphResponse.class);
        graphId = createGraphResponse.getId();
        Assertions.assertNotNull(graphId, "Граф с именем: " + name + ", не создался");
    }

    @Override
    @Step("Удаление графа")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .delete("graphs/" + graphId + "/")
                .assertStatus(200);

        GraphSteps graphSteps = new GraphSteps();
        graphId = graphSteps.getGraphId(name);
        Assertions.assertNull(graphId, String.format("Граф с именем: %s не удалился", name));
    }
}
