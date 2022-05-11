package steps.references;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.orderService.interfaces.IProduct;
import models.references.Directories;
import models.references.PageFilter;
import models.references.Pages;
import models.subModels.Flavor;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static core.helper.Configure.ReferencesURL;

public class ReferencesStep extends Steps {
    private static final String DIRECTORIES_JSON_TEMPLATE = "references/createDirectory.json";
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";

    @Step("Получение списка flavors для продукта {product}")
    public static List<Flavor> getProductFlavorsLinkedList(IProduct product) {
        String jsonArray = new Http(ReferencesURL)
                .setProjectId(Objects.requireNonNull(product.getProjectId()))
                .get("/api/v1/pages/?directory__name=flavors&tags={}", product.getProductId())
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка flavors по page_filter {pageFilter}")
    public static List<Flavor> getFlavorsByPageFilterLinkedList(IProduct product, String pageFilter) {
        String jsonArray = new Http(ReferencesURL)
                .setProjectId(Objects.requireNonNull(product).getProjectId())
                .get("/api/v1/pages/?page_filter={}", pageFilter)
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка в справочнике по параметрам {attrs}")
    public static JsonPath getJsonPathList(String attrs) {
        return new Http(ReferencesURL)
                .get("/api/v1/pages/?{}", attrs)
                .assertStatus(200)
                .jsonPath();
    }

    @Step("Получение списка directories")
    public static List<Directories> getDirectoriesList() {
        String jsonArray = new Http(ReferencesURL).get("/api/v1/directories/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Directories>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение directory по имени")
    public static Directories getDirectoryByName(String name) {
        return new Http(ReferencesURL)
                .get("/api/v1/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Получение списка page_filters")
    public static List<PageFilter> getPageFiltersList() {
        String jsonArray = new Http(ReferencesURL)
                .get("/api/v1/page_filters/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<PageFilter>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение page_filters по ключу")
    public static PageFilter getPageFilter(String key) {
        return new Http(ReferencesURL)
                .get("/api/v1/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение списка Pages")
    public static List<Pages> getPagesList() {
        String jsonArray = new Http(ReferencesURL).get("/api/v1/pages/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение Pages по Id")
    public static Pages getPagesById(String pageId) {
        return new Http(ReferencesURL).get("/api/v1/pages/" + pageId + "/")
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение списка directories для приватных ролей")
    public static List<Directories> getPrivateDirectoriesList() {
        String jsonArray = new Http(ReferencesURL).get("/api/v1/private/directories/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Directories>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание directory для приватных ролей")
    public static Directories createDirectory(JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/")
                .assertStatus(201)
                .extractAs(Directories.class);
    }

    @Step("Создание directory для приватных ролей c недопустимыми символами")
    public static Response createDirectoryWithInvalidName(JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/")
                .assertStatus(400);
    }

    @Step("Получение directory по имени для приватных ролей")
    public static Directories getPrivateDirectoryByName(String name) {
        return new Http(ReferencesURL)
                .get("/api/v1/private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Изменение directory по имени для приватных ролей")
    public static Directories updatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(ReferencesURL)
                .body(jsonObject)
                .put("/api/v1/private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Частичное изменение directory по имени для приватных ролей")
    public static Directories partialUpdatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(ReferencesURL)
                .body(jsonObject)
                .patch("/api/v1/private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Получение списка Pages по имени Directory для приватных ролей")
    public static List<Pages> getPrivatePagesListByDirectoryName(String directoryName) {
        String jsonArray = new Http(ReferencesURL).get("/api/v1/private/directories/" + directoryName + "/pages")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение списка Pages по имени Directory и имени Page для приватных ролей")
    public static List<Pages> getPrivatePagesListByDirectoryNameAndPageName(String directoryName, String pageName) {
        String jsonArray = new Http(ReferencesURL).get("/api/v1/private/directories/" + directoryName + "/pages?name=" + pageName)
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение Pages по Id для приватных ролей")
    public static Pages getPrivatePagesById(String directoryName, String pageId) {
        return new Http(ReferencesURL).get("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение ответа на запрос Pages по Id для приватных ролей")
    public static Response getPrivateResponsePagesById(String directoryName, String pageId) {
        return new Http(ReferencesURL).get("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200);
    }

    @Step("Обновление Pages по Id для приватных ролей")
    public static Pages updatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .put("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Обновление Data в Pages по Id для приватных ролей")
    public static void updateDataPrivatePagesById(String directoryName, String pageId, JSONObject object) {
        new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/" + directoryName + "/pages/" + pageId + "/update_data")
                .assertStatus(200);
    }

    @Step("Частичное обновление Pages по Id для приватных ролей")
    public static Pages partialUpdatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .patch("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Частичное обновление Pages по Id для приватных ролей")
    public void partUpdatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        new Http(ReferencesURL)
                .body(object)
                .patch("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200);
    }

    @Step("Удаление Pages по Id для приватных ролей")
    public static void deletePrivatePagesById(String directoryName, String pageId) {
        new Http(ReferencesURL)
                .delete("/api/v1/private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(204);
    }

    @Step("Создание Pages для приватных ролей")
    public static Pages createPrivatePagesAndGet(String directoryName, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/" + directoryName + "/pages")
                .assertStatus(201)
                .extractAs(Pages.class);
    }

    @Step("Создание Pages для приватных ролей")
    public static Response createPrivatePages(String directoryName, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/" + directoryName + "/pages")
                .assertStatus(201);
    }

    @Step("Создание Pages для приватных ролей")
    public static Response createPrivatePagesAndGetResponse(String directoryName, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/directories/" + directoryName + "/pages");
    }

    @Step("Удаление directory по имени для приватных ролей")
    public static void deletePrivateDirectoryByName(String name) {
        new Http(ReferencesURL)
                .delete("/api/v1/private/directories/" + name + "/")
                .assertStatus(204);
    }

    @Step("Получение списка page_filters для приватных ролей")
    public static List<PageFilter> getPrivatePageFiltersList() {
        String jsonArray = new Http(ReferencesURL)
                .get("/api/v1/private/page_filters/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<PageFilter>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание page_filters для приватных ролей")
    public static PageFilter createPrivatePageFilter(JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("/api/v1/private/page_filters/")
                .assertStatus(201)
                .extractAs(PageFilter.class);
    }

    @Step("Обновление page_filters для приватных ролей")
    public static PageFilter updatePrivatePageFilter(String key, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .put("/api/v1/private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение page_filters по ключу для приватных ролей")
    public static PageFilter getPrivatePageFilter(String key) {
        return new Http(ReferencesURL)
                .get("/api/v1/private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Частичное обновление page_filters для приватных ролей")
    public static PageFilter partialUpdatePrivatePageFilter(String key, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .patch("/api/v1/private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Удаление page_filters по ключу для приватных ролей")
    public static void deletePrivatePageFiltersByKey(String key) {
        new Http(ReferencesURL)
                .delete("/api/v1/private/page_filters/" + key + "/")
                .assertStatus(204);
    }

    @Step("Импорт Directories для приватных ролей")
    public static void importPrivateDirectories(String path) {
        new Http(ReferencesURL)
                .multiPart("/api/v1/private/directories/obj_import/", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Directories для приватных ролей")
    public static void exportPrivateDirectories(String name) {
        new Http(ReferencesURL)
                .get("/api/v1/private/directories/" + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Импорт Pages для приватных ролей")
    public static void importPrivatePages(String path, String directoryName) {
        new Http(ReferencesURL)
                .multiPart("/api/v1/private/directories/" + directoryName + "/pages/import", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Pages для приватных ролей")
    public static void exportPrivatePages(String directoryName, String id) {
        new Http(ReferencesURL)
                .get("/api/v1/private/directories/" + directoryName + "/pages/" + id + "/export")
                .assertStatus(200);
    }

    @Step("Импорт Page_filter для приватных ролей")
    public static void importPrivatePageFilter(String path) {
        new Http(ReferencesURL)
                .multiPart("/api/v1/private/page_filters/obj_import/", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Page_filter для приватных ролей")
    public static void exportPrivatePageFilter(String key) {
        new Http(ReferencesURL)
                .get("/api/v1/private/page_filters/" + key + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Создание JSON для Directories")
    public static JSONObject createDirectoriesJsonObject(String directoriesName, String description) {
        return JsonHelper.getJsonTemplate(DIRECTORIES_JSON_TEMPLATE)
                .set("name", directoriesName)
                .set("description", description)
                .build();
    }

    @Step("Создание JSON для Pages")
    public static JSONObject createPagesJsonObject(String pageName, String directoryId) {
        return JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", pageName)
                .set("directory", directoryId)
                .build();
    }

    @Step("Проверка существования Page")
    public static boolean isPageExist(List<Pages> list, String pageName, String directoriesId) {
        for (Pages pagesItem : list) {
            if (pagesItem.getName().equals(pageName) && pagesItem.getDirectory().equals(directoriesId)) {
                return true;
            }
        }
        return false;
    }
}
