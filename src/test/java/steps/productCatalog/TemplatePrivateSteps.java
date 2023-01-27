package steps.productCatalog;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class TemplatePrivateSteps extends Steps {

    private static final String adminUrl = "/private/api/v1/templates/";
    private static final String adminUrlV2 = "/private/api/v2/templates/";

    @Step("Получение шаблона по Id")
    public static Template getTemplatePrivateById(Integer objectId) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrl + objectId + "/")
                .extractAs(Template.class);
    }

    @Step("Получение шаблона по имени {name}")
    public static Template getTemplatePrivateByName(String name) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrlV2 + name + "/")
                .extractAs(Template.class);
    }

    @Step("Создание шаблона")
    public static Template createTemplatePrivate(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrl)
                .assertStatus(201)
                .extractAs(Template.class);
    }

    @Step("Создание шаблона")
    public static Template createTemplatePrivateV2(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrl)
                .assertStatus(201)
                .extractAs(Template.class);
    }

    @Step("Удаление шаблона по id")
    public static void deleteTemplatePrivateById(Integer id) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление шаблона по имени {name}")
    public static void deleteTemplatePrivateByName(String name) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Частичное обновление шаблона")
    public static Response partialUpdatePrivateTemplate(Integer id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrl + id + "/");
    }

    @Step("Частичное обновление шаблона по имени {name}")
    public static Response partialUpdateTemplatePrivateByName(String name, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrlV2 + name + "/");
    }
}
