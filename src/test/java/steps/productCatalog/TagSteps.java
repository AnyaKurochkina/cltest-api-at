package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.tag.GetTagList;
import models.cloud.productCatalog.tag.Tag;
import org.json.JSONObject;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class TagSteps extends Steps {

    private static final String tagUrl = "/api/v1/tags/";
    private static final String tagV2 = "/api/v2/tags/";

    @Step("Получение списка Тегов")
    public static List<Tag> getServiceList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagUrl)
                .assertStatus(200)
                .extractAs(GetTagList.class).getList();
    }

    @Step("Проверка существования Тега по имени")
    public static boolean isTagExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение Тега по имени {name}")
    public static Tag getTagByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagV2 + name + "/")
                .extractAs(Tag.class);
    }

    @Step("Получение списка объектов использующих Тег")
    public static Response getTagUsedObjectsByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagV2 + name + "/used/");
    }

    @Step("Создание Тега")
    public static Tag createTagByName(String name) {
        if (isTagExists(name)) {
            deleteTagByName(name);
        }
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("name", name))
                .post(tagUrl)
                .assertStatus(201)
                .extractAs(Tag.class);
    }

    @Step("Удаление Тега по имени {name}")
    public static void deleteTagByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(tagUrl + name + "/")
                .assertStatus(204);
    }
}
