package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.jinja2.GetJinja2List;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.jinja2.UsedJinja2ObjectList;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.productCatalogURL;

public class Jinja2Steps extends Steps {
    private static final String jinjaUrl = "/api/v1/jinja2_templates/";
    private static final String jinjaUrl2 = "/api/v2/jinja2_templates/";

    @Step("Получение списка jinja2")
    public static List<Jinja2Template> getJinja2List() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getList();
    }

    @Step("Получение списка объектов использующих Jinja2_template по id - {id}")
    public static List<UsedJinja2ObjectList> getObjectListUsedJinja2Template(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + id + "/used/")
                .assertStatus(200)
                .jsonPath()
                .getList("", UsedJinja2ObjectList.class);
    }

    @Step("Получение списка объектов использующих Jinja2_template по имени {name}")
    public static List<UsedJinja2ObjectList> getObjectListUsedJinja2TemplateByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl2 + name + "/used/")
                .assertStatus(200)
                .jsonPath()
                .getList("", UsedJinja2ObjectList.class);
    }

    @Step("Получение jinja2 по Id")
    public static Jinja2Template getJinja2ById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/")
                .extractAs(Jinja2Template.class);
    }

    @Step("Проверка существования jinja2 по имени")
    public static boolean isJinja2Exists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Удаление jinja2 по имени {name}")
    public static void deleteJinjaByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(jinjaUrl2 + name + "/")
                .assertStatus(204);
    }

    @Step("Удаление jinja2 по id {id}")
    public static void deleteJinjaById(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(jinjaUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Создание шаблона Jinja2")
    public static Jinja2Template createJinja(String name) {
        return Jinja2Template.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Создание шаблона Jinja2")
    public static Jinja2Template createJinja(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(jinjaUrl)
                .assertStatus(201)
                .extractAs(Jinja2Template.class);
    }

    @Step("Создание шаблона Jinja2")
    public static Response createJinjaResponse(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(jinjaUrl);
    }

    @Step("Создание шаблона Jinja2")
    public static Response createJinja(Role role, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(role)
                .body(body)
                .post(jinjaUrl);
    }

    @Step("Экспорт jinja2 по Id")
    public static Response exportJinjaById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Получение Meta данных списка jinja2 продуктового каталога")
    public static Meta getMetaJinja2List() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl)
                .assertStatus(200)
                .extractAs(GetJinja2List.class).getMeta();
    }

    @Step("Сортировка jinja2 по дате создания")
    public static GetJinja2List orderingJinja2ByCreateData() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetJinja2List.class);
    }

    @Step("Сортировка jinja2 по дате обновления")
    public static GetJinja2List orderingJinja2ByUpDateData() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetJinja2List.class);
    }

    @Step("Получение jinja2 по имени с публичным токеном")
    public static Response getJinja2ByNameWithPublicToken(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(jinjaUrl + "?name=" + name);
    }

    @Step("Обновление jinja2 с публичным токеном")
    public static Response partialUpdateJinja2WithPublicToken(Role role, String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(role)
                .body(object)
                .patch(jinjaUrl + id + "/");
    }

    @Step("Частичное обновление jinja2")
    public static Response partialUpdateJinja2(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(jinjaUrl + id + "/");
    }

    @Step("Обновление всего jinja2 по Id")
    public static void putJinja2ById(String objectId, JSONObject body) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(jinjaUrl + objectId + "/")
                .assertStatus(200);
    }

    @Step("Обновление всего jinja2 по Id с публичным токеном")
    public static Response putJinja2ByIdWithPublicToken(Role role, String objectId, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(role)
                .body(body)
                .put(jinjaUrl + objectId + "/");
    }

    @Step("Удаление jinja2 с публичным токеном")
    public static Response deleteJinja2WithPublicToken(Role role, String id) {
        return new Http(productCatalogURL)
                .setRole(role)
                .delete(jinjaUrl + id + "/");
    }

    @Step("Копирование jinja2 по Id")
    public static void copyJinja2ById(String objectId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(jinjaUrl + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование jinja2 по Id без ключа")
    public static Response copyJinja2ByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(jinjaUrl + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Получение jinja2 по Id без токена")
    public static Response getJinja2ByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(jinjaUrl + objectId + "/")
                .assertStatus(401);
    }

    @Step("Удаление jinja2 по Id без токена")
    public static Response deleteJinja2ByIdWithOutToken(String id) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .delete(jinjaUrl + id + "/")
                .assertStatus(401);
    }

    @Step("Частичное обновление jinja2 без токена")
    public static Response partialUpdateJinja2WithOutToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(jinjaUrl + id + "/")
                .assertStatus(401);
    }

    @Step("Загрузка jinja2 в Gitlab")
    public static Response dumpJinja2ToBitbucket(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(jinjaUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка jinja2 из Gitlab")
    public static void loadJinja2FromBitbucket(JSONObject body) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(jinjaUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Импорт jinja2")
    public static ImportObject importJinja2(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(jinjaUrl + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }
}
