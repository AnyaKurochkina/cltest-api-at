package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.createAction.response.CreateActionResponse;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.ActionsSteps;
import steps.productCatalog.GraphSteps;

import static core.helper.JsonHelper.convertResponseOnClass;

@Log4j2
@Builder
@Getter
public class Action extends Entity {
    private Graph graph;
    private String jsonTemplate;
    private String actionName;
    private String graphId;
    private String actionId;
    @Builder.Default
    protected transient ActionsSteps actionsSteps = new ActionsSteps();

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/actions/createAction.json";
        GraphSteps graphSteps = new GraphSteps();
//        if(graph == null){
//            graph = Graph.builder().build().createObject();
//        }
        graphId = graphSteps.getGraphId("AtTestGraph");
        return this;
    }

    @Override
    public JSONObject toJson() {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
                .set("$.version", "1.1.1")
//                .set("$.required_order_statuses[0]", "success")
//                .set("$.event_type[0]", "bm")
//                .set("$.event_provider[0]", "s3")
//                .set("$.type", "deleted")
                .build();
    }

    @Override
    @Step("Создание экшена")
    protected void create() {
        String response = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("actions/", toJson())
                .assertStatus(201)
                .toString();

        CreateActionResponse createActionResponse = convertResponseOnClass(response, CreateActionResponse.class);
        actionId = createActionResponse.getId();
        Assertions.assertNotNull(actionId, "Экшен с именем: " + actionName + ", не создался");
    }

    @Override
    @Step("Удаление экшена")
    protected void delete() {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .delete("actions/" + actionId + "/")
                .assertStatus(204);

        ActionsSteps actionsSteps = new ActionsSteps();
        actionId = actionsSteps.getActionId(actionName);
        Assertions.assertNull(actionId, String.format("Экшен с именем: %s не удалился", actionName));
    }
}
