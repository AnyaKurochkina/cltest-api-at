package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import models.cloud.productCatalog.forbiddenAction.GetForbiddenActionList;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ForbiddenActionSteps extends Steps {
    private static final String endPoint = "/api/v1/forbidden_actions/";
    private static final String endPointV2 = "/api/v2/forbidden_actions/";

    @Step("Проверка существования запрещенного действия продуктового каталога по имени")
    public static boolean isForbiddenActionExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Создание запрещенного действия")
    public static ForbiddenAction createForbiddenAction(String name) {
        return ForbiddenAction.builder()
                .name(name)
                .title(name)
                .build()
                .createObject();
    }

    @Step("Создание запрещенного действия")
    public static Response createForbiddenAction(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(endPoint);
    }

    @Step("Поиск ID запрещенного действия продуктового каталога по имени с использованием multiSearch")
    public static Integer getForbiddenActionIdByNameWithMultiSearch(String name) {
        Integer objectId = null;
        List<ForbiddenAction> list = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetForbiddenActionList.class).getList();
        for (ForbiddenAction forbiddenAction : list) {
            if (forbiddenAction.getName().equals(name)) {
                objectId = forbiddenAction.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("Удаление запрещенного действия по id")
    public static void deleteForbiddenActionById(Integer id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(endPoint + id + "/")
                .assertStatus(204);
    }

    public static void deleteForbiddenActionByName(String name) {
        deleteForbiddenActionById(getForbiddenActionIdByNameWithMultiSearch(name));
    }

    @Step("Получение запрещенного действия по Id")
    public static ForbiddenAction getForbiddenActionById(Integer objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(endPoint + objectId + "/")
                .extractAs(ForbiddenAction.class);
    }

    @Step("Импорт запрещенного действия")
    public static Response importForbiddenAction(String pathName) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(endPoint + "obj_import/", "file", new File(pathName));
    }

    @Step("Частичное обновление запрещенного действия")
    public static Response partialUpdateForbiddenAction(Integer id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(endPoint + id + "/");
    }

    @Step("Экспорт запрещенного действия по Id")
    public static Response exportForbiddenActionById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }
}
