package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.tag.GetTagList;
import models.cloud.productCatalog.tag.Tag;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.productCatalogURL;

public class TagSteps extends Steps {

    private static final String tagUrl = "/api/v1/tags/";
    private static final String tagV2 = "/api/v2/tags/";

    @Step("Получение списка Тегов")
    public static List<Tag> getTagList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagUrl)
                .assertStatus(200)
                .extractAs(GetTagList.class).getList();
    }

    @Step("Проверка существования Тега по имени")
    public static boolean isTagExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение Тега по имени {name}")
    public static Tag getTagByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagV2 + name + "/")
                .assertStatus(200)
                .extractAs(Tag.class);
    }

    @Step("Получение списка объектов использующих Тег")
    public static Response getTagUsedObjectsByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagV2 + name + "/used/");
    }

    @Step("Создание Тега")
    public static Tag createTag(String name) {
        if (isTagExists(name)) {
            deleteTagByName(name);
        }
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("name", name))
                .post(tagUrl)
                .assertStatus(201)
                .extractAs(Tag.class);
    }

    @Step("Создание Тега")
    public static Response createTagByNameResponse(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("name", name))
                .post(tagUrl);
    }

    @Step("Удаление Тега по имени {name}")
    public static void deleteTagByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(tagUrl + name + "/")
                .assertStatus(204);
    }

    @Step("Удаление Тега по имени {name}")
    public static Response deleteTagByNameResponse(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(tagUrl + name + "/");
    }

    @Step("Копирование Тега по имени {name}")
    public static Tag copyTagByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(tagUrl + name + "/copy/")
                .assertStatus(201)
                .extractAs(Tag.class);
    }

    @Step("Экспорт Тега по имени {name}")
    public static Response exportTagByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(tagUrl + name + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Импорт Тега")
    public static ImportObject importTag(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(tagUrl + "obj_import/", "file", new File(pathName))
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }
}
