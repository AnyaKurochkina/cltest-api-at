package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import models.cloud.productCatalog.icon.GetIconList;
import models.cloud.productCatalog.icon.Icon;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.productCatalogURL;

public class IconSteps extends Steps {
    private static final String endPoint = "/api/v1/icons/";

    @Step("Создание иконки")
    public static Icon createIcon(JSONObject json) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post(endPoint)
                .assertStatus(201)
                .extractAs(Icon.class);
    }

    @Step("Проверка существования Иконки по имени")
    public static boolean isIconExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление иконки по id")
    public static void deleteIconById(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(endPoint + id + "/");
    }

    @Step("Поиск ID иконки продуктового каталога по имени с использованием multiSearch")
    public static String getIconIdByNameWithMultiSearch(String name) {
        String objectId = null;
        List<Icon> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetIconList.class).getList();
        for (Icon Icon : list) {
            if (Icon.getName().equals(name)) {
                objectId = Icon.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("Удаление иконки по имени")
    public static void deleteIconByName(String name) {
        deleteIconById(getIconIdByNameWithMultiSearch(name));
    }
}
