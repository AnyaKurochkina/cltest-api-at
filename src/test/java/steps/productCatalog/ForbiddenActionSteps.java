package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.forbiddenAction.ForbiddenAction;
import models.productCatalog.forbiddenAction.GetForbiddenActionList;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ForbiddenActionSteps extends Steps {
    private static final String endPoint = "/api/v1/forbidden_actions/";

    @Step("Проверка существования запрещенного действия продуктового каталога по имени")
    public static boolean isForbiddenActionExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
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
}
