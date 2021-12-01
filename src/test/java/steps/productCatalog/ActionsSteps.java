package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Action.patchAction.response.PatchActionResponse;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import httpModels.productCatalog.Action.getAction.response.ActionResponse;
import httpModels.productCatalog.Action.getAction.response.ListItem;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static core.helper.JsonHelper.convertResponseOnClass;

public class ActionsSteps {

    private JSONObject toJson(String pathToJsonBody, String actionName, String graphId) {
        JsonHelper jsonHelper = new JsonHelper();
        //productCatalog/actions/createAction.json
        return jsonHelper.getJsonTemplate(pathToJsonBody)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
//                .set("$.graph_version", "1.0.0")
                .set("$.graph_version_pattern", "1.")
//                .set("$.required_order_statuses[0]", "success")
//                .set("$.event_type[0]", "bm")
//                .set("$.event_provider[0]", "s3")
//                .set("$.type", "deleted")
                .build();
    }

    @SneakyThrows
    @Step("Поиск ID экшена по имени с использованием multiSearch")
    public String getActionByNameWithMultiSearch(String actionName) {
        String actionId = null;
        String object = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .get("actions/?include=total_count&page=1&per_page=10&multisearch=" + actionName)
                .assertStatus(200)
                .toString();

        ActionResponse response = convertResponseOnClass(object, ActionResponse.class);

        for (ListItem listItem : response.getList()) {
            if (listItem.getName().equals(actionName)) {
                actionId = listItem.getId();
                break;
            }
        }
        Assertions.assertNotNull(actionId, String.format("Экшен с именем: %s, с помощью multiSearch не найден", actionName));
        return actionId;
    }

    @SneakyThrows
    @Step("Получение ID экшена  по его имени: {actionName}")
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
                break;
            }
        }
        return actionId;
    }

    @SneakyThrows
    @Step("Создание экшена")
    public Http.Response createAction(JSONObject body) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("actions/", body);
    }

    @SneakyThrows
    @Step("Обновление экшена")
    public Http.Response patchActionRow(JSONObject body, String actionId) {
        return new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("actions/" + actionId + "/", body);
    }

    @SneakyThrows
    @Step("Обновление экшена")
    public PatchActionResponse patchAction(String actionName, String graphId, String actionId) {
        String response = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .patch("actions/" + actionId + "/", toJson("productCatalog/actions/createAction.json", actionName, graphId))
                .assertStatus(200)
                .toString();

        return convertResponseOnClass(response, PatchActionResponse.class);
    }

    @SneakyThrows
    @Step("Удаление экшена")
    public void deleteAction(String id) {
        new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .delete("actions/" + id + "/")
                .assertStatus(204);
    }
}
