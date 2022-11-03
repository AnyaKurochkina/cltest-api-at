package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import models.cloud.productCatalog.allowedAction.GetAllowedActionList;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class AllowedActionSteps extends Steps {
    private static final String allowedUrl = "/api/v1/allowed_actions/";

    @Step("Проверка существования разрешенного действия по имени {name}")
    public static boolean isAllowedActionExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение разрешенного действия по имени {name}")
    public static AllowedAction getAllowedActionByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl + "?name={}", name)
                .assertStatus(200)
                .extractAs(GetAllowedActionList.class)
                .getList().get(0);
    }

    @Step("Получение разрешенного действия по id {id}")
    public static AllowedAction getAllowedActionById(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl + "{}/", id)
                .assertStatus(200)
                .extractAs(AllowedAction.class);
    }

    @Step("Удаление разрешенного действия по id {id}")
    public static void deleteAllowedActionById(Integer id) {
         new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(allowedUrl + "{}/", id)
                .assertStatus(204);

    }

    @Step("Удаление разрешенного действия по name {name}")
    public static void deleteAllowedActionByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(allowedUrl + "{}/", getAllowedActionByName(name).getId())
                .assertStatus(204);

    }

    @Step("Создание разрешенного действия")
    public static Response createAllowedAction(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(allowedUrl)
                .compareWithJsonSchema("jsonSchema/allowedAction/postAllowedAction.json");
    }

    @Step("Получение списка разрешенных действий")
    public static List<AllowedAction> getAllowedActionList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl)
                .assertStatus(200)
                .compareWithJsonSchema("jsonSchema/allowedAction/getAllowedActionList.json")
                .extractAs(GetAllowedActionList.class)
                .getList();
    }

    @Step("Проверка доступности у allowedAction event_type {} и event_provider {}")
    public static void checkAllowedActionEvents(Integer actionId, String eventType, String eventProvider) {
         new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(allowedUrl + "check_action/?action={}&event_type={}&event_provider={}", actionId, eventType, eventProvider)
                .assertStatus(200);
    }

    @Step("Частичное обновление разрешенного действия")
    public static AllowedAction partialUpdateAllowedAction(Integer id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(allowedUrl + id + "/")
                .assertStatus(200)
                .extractAs(AllowedAction.class);
    }

    @Step("Обновление разрешенного действия")
    public static AllowedAction updateAllowedAction(Integer id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .put(allowedUrl + id + "/")
                .assertStatus(200)
                .extractAs(AllowedAction.class);
    }

    @Step("Загрузка разрешенного действия в Gitlab")
    public static Response dumpAllowedActionToGit(Integer id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(allowedUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка разрешенного действия из Gitlab")
    public static void loadAllowedActionFromGit(JSONObject body) {
         new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(allowedUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }
}
