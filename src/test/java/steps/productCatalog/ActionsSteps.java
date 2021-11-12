package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.createAction.response.CreateActionResponse;
import lombok.SneakyThrows;
import httpModels.productCatalog.getActions.response.ActionResponse;
import httpModels.productCatalog.getActions.response.ListItem;
import org.json.JSONObject;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ActionsSteps {

    private JSONObject toJson(String pathToJsonBody, String actionName, String graphId) {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(pathToJsonBody)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
//                .set("$.required_order_statuses[0]", "success")
//                .set("$.event_type[0]", "bm")
//                .set("$.event_provider[0]", "s3")
//                .set("$.type", "deleted")
                .build();
    }

    @SneakyThrows
    public String getActionId(String actionName) {
        String actionId = null;
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("actions/")
                .assertStatus(200)
                .toString();

        ActionResponse response = convertResponseOnClass(object, ActionResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(actionName)) {
                actionId = listItem.getId();
            }
        }
        return actionId;
    }

    @SneakyThrows
    public void createAction(String actionName, String graphId) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("actions/", toJson("productCatalog/actions/createAction.json", actionName, graphId))
                .assertStatus(201);
    }

    @SneakyThrows
    public void deleteAction(String id) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .delete("actions/" + id + "/")
                .assertStatus(204);
    }
}
