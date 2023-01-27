package steps.productCatalog;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class ServicePrivateSteps extends Steps {

    private static final String adminUrl = "/private/api/v1/services/";
    private static final String adminUrlV2 = "/private/api/v2/services/";

    @Step("Получение действия по Id")
    public static Service getServicePrivateById(String objectId) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrl + objectId + "/")
                .extractAs(Service.class);
    }

    @Step("Получение действия по имени {name}")
    public static Service getServicePrivateByName(String name) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrlV2 + name + "/")
                .extractAs(Service.class);
    }

    @Step("Создание действия")
    public static Service createServicePrivate(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrl)
                .assertStatus(201)
                .extractAs(Service.class);
    }

    @Step("Создание действия")
    public static Service createServicePrivateV2(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrlV2)
                .assertStatus(201)
                .extractAs(Service.class);
    }

    @Step("Удаление действия по id")
    public static void deleteServicePrivateById(String id) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление действия по имени {name}")
    public static void deleteServicePrivateByName(String name) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdatePrivateService(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrl + id + "/");
    }

    @Step("Частичное обновление действия по имени {name}")
    public static Response partialUpdateServicePrivateByName(String name, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrlV2 + name + "/");
    }
}
