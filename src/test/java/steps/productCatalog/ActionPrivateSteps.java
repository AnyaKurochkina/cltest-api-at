package steps.productCatalog;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.productCatalogURL;

public class ActionPrivateSteps extends Steps {

    private static final String adminUrl = "/private/api/v1/actions/";
    private static final String adminUrlV2 = "/private/api/v2/actions/";

    @Step("Получение действия по Id")
    public static Action getActionPrivateById(String objectId) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .get(adminUrl + objectId + "/")
                .extractAs(Action.class);
    }

    @Step("Получение действия по имени {name}")
    public static Action getActionPrivateByName(String name) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .get(adminUrlV2 + name + "/")
                .extractAs(Action.class);
    }

    @Step("Создание действия")
    public static Action createActionPrivate(JSONObject body) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrl)
                .assertStatus(201)
                .extractAs(Action.class);
    }

    @Step("Создание действия")
    public static Action createActionPrivateV2(JSONObject body) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrlV2)
                .assertStatus(201)
                .extractAs(Action.class);
    }

    @Step("Удаление действия по id")
    public static void deleteActionPrivateById(String id) {
        new Http(productCatalogURL)
                .withServiceToken()
                .delete(adminUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление действия по имени {name}")
    public static void deleteActionPrivateByName(String name) {
        new Http(productCatalogURL)
                .withServiceToken()
                .delete(adminUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdatePrivateAction(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrl + id + "/");
    }

    @Step("Частичное обновление действия по имени {name}")
    public static Response partialUpdateActionPrivateByName(String name, JSONObject object) {
        return new Http(productCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrlV2 + name + "/");
    }
}
