package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Action.existsAction.response.ExistsActionResponse;
import httpModels.productCatalog.Action.getAction.response.GetActionResponse;
import httpModels.productCatalog.Action.getActionList.response.ActionResponse;
import httpModels.productCatalog.Action.getActionList.response.ListItem;
import httpModels.productCatalog.Action.patchAction.response.PatchActionResponse;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;
import static io.restassured.RestAssured.given;

public class ActionsSteps {

    @SneakyThrows
    @Step("Поиск ID экшена по имени с использованием multiSearch")
    public String getActionIdByNameWithMultiSearch(String actionName) {
        String actionId = null;
        ActionResponse response = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/?include=total_count&page=1&per_page=10&multisearch=" + actionName)
                .assertStatus(200).extractAs(ActionResponse.class);

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
        ActionResponse response = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/")
                .assertStatus(200)
                .extractAs(ActionResponse.class);

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
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("actions/", body);
    }

    @SneakyThrows
    @Step("Обновление экшена")
    public Http.Response patchActionRow(JSONObject body, String actionId) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .patch("actions/" + actionId + "/", body);
    }

    @SneakyThrows
    @Step("Обновление экшена")
    public PatchActionResponse patchAction(String actionName, String graphId, String actionId) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .patch("actions/" + actionId + "/", toJson("actions/createAction.json", actionName, graphId))
                .assertStatus(200)
                .extractAs(PatchActionResponse.class);
    }

    @SneakyThrows
    @Step("Получение списка действий")
    public List<ListItem> getActionList() {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/")
                .assertStatus(200)
                .extractAs(ActionResponse.class)
                .getList();
    }

    @SneakyThrows
    @Step("Проверка существования действия по имени")
    public boolean isActionExists(String name) {
        String object = new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/exists/?name=" + name)
                .assertStatus(200)
                .toString();
        ExistsActionResponse response = convertResponseOnClass(object, ExistsActionResponse.class);
        return response.getExists();
    }

    @SneakyThrows
    @Step("Импорт действия")
    public void importAction(String pathName) {
        given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(pathName))
                .when()
                .post("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/product-catalog/actions/obj_import/")
                .then()
                .statusCode(200);
    }

    @SneakyThrows
    @Step("Получение действия по Id")
    public GetActionResponse getActionById(String id) {
        return new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/" + id + "/")
                .assertStatus(200)
                .extractAs(GetActionResponse.class);
    }

    @SneakyThrows
    @Step("Копирование действия по Id")
    public void copyActionById(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("actions/" + id + "/copy/")
                .assertStatus(200);
    }

    @SneakyThrows
    @Step("Экспорт действия по Id")
    public void exportActionById(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .get("actions/" + id + "/obj_export/")
                .assertStatus(200);
    }

    @SneakyThrows
    @Step("Удаление экшена")
    public void deleteAction(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .delete("actions/" + id + "/")
                .assertStatus(204);
    }

    @Step("Создание JSON объекта по действиям")
    public JSONObject createJsonObject(String name) {
        return new JsonHelper()
                .getJsonTemplate("productCatalog/actions/createAction.json")
                .set("$.name", name)
                .build();
    }

    @Step("Удаление действия по имени")
    public void deleteActionByName(String name) {
        deleteAction(getActionIdByNameWithMultiSearch(name));
    }

    private JSONObject toJson(String pathToJsonBody, String actionName, String graphId) {
        JsonHelper jsonHelper = new JsonHelper();
        return jsonHelper.getJsonTemplate(pathToJsonBody)
                .set("$.name", actionName)
                .set("$.title", actionName)
                .set("$.description", actionName)
                .set("$.graph_id", graphId)
                .set("$.graph_version_pattern", "1.")
                .build();
    }
}
