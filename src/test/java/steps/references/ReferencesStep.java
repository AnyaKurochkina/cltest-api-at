package steps.references;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import models.AbstractEntity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.productCatalog.ProductAudit;
import models.cloud.references.Directories;
import models.cloud.references.PageFilter;
import models.cloud.references.Pages;
import models.cloud.references.UpdateData;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static core.helper.Configure.referencesURL;
import static tests.routes.ReferencesApi.*;

public class ReferencesStep extends Steps {
    private static final String DIRECTORIES_JSON_TEMPLATE = "references/createDirectory.json";
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";
    public static final String API_V_1_PRIVATE_DIRECTORIES = "/api/v1/private/directories/";
    public static final String API_V_2_PRIVATE_DIRECTORIES = "/api/v2/private/directories/";
    public static final String API_V_1_DIRECTORIES = "/api/v1/directories/";
    public static final String API_V_1_PAGE_FILTERS = "/api/v1/page_filters/";
    public static final String API_V_1_PAGES = "/api/v1/pages/";
    public static final String API_V_1_UPDATE_DATA = "/api/v1/private/update_page";
    public static final String API_V_1_PRIVATE_PAGE_FILTERS = "/api/v1/private/page_filters/";

    @Step("Экспорт нескольких Pages")
    public static Response exportMultiPages(String directoriesName, JSONObject json) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post("/api/v1/private/directories/{}/pages/objects_export?as_file=true", directoriesName)
                .assertStatus(200);
    }

    @Deprecated
    @Step("Получение списка flavors для продукта {product}")
    public static List<Flavor> getProductFlavorsLinkedList(IProduct product) {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setProjectId(Objects.requireNonNull(product.getProjectId()), Role.ORDER_SERVICE_ADMIN)
                .get(API_V_1_PAGES + "?directory__name=flavors&tags={}", product.getProductCatalogName())
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка flavors для продукта {product}")
    public static List<Flavor> getProductFlavorsLinkedListByFilter(IProduct product) {
        String filter = product.getFilter();
        if (Objects.isNull(filter))
            return getProductFlavorsLinkedList(product);
        Project project = Project.builder().id(product.getProjectId()).build().createObject();
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setProjectId(Objects.requireNonNull(product.getProjectId()), Role.ORDER_SERVICE_ADMIN)
                .get(API_V_1_PAGES + "?page_filter_chain=flavor:{}:{}:{}", filter,
                        project.getProjectEnvironmentPrefix().getEnvType().toLowerCase(),
                        project.getProjectEnvironmentPrefix().getEnv().toLowerCase())
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка flavors по page_filter_chain {pageFilter}")
    public static List<Flavor> getFlavorsByPageFilterLinkedList(IProduct product, String pageFilter) {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setProjectId(Objects.requireNonNull(product).getProjectId(), Role.ORDER_SERVICE_ADMIN)
                .get(API_V_1_PAGES + "?page_filter_chain={}", pageFilter)
                .assertStatus(200)
                .toString();

        Type type = new TypeToken<List<Flavor>>() {
        }.getType();
        List<Flavor> list = new Gson().fromJson(jsonArray, type);

        return list.stream().sorted(Comparator.comparing(Flavor::getCpus).thenComparing(Flavor::getMemory)).collect(Collectors.toList());
    }

    @Step("Получение списка в справочнике по параметрам {attrs}")
    public static JsonPath getJsonPathList(String attrs) {
        return new Http(referencesURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get("/api/v1/pages/?{}", attrs)
                .assertStatus(200)
                .jsonPath();
    }

    @Step("Получение списка directories")
    public static List<Directories> getDirectoriesList() {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_DIRECTORIES)
                .assertStatus(200)
                .jsonPath()
                .getList("", Directories.class);
    }

    @Step("Получение directory по имени")
    public static Directories getDirectoryByName(String name) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_DIRECTORIES + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Получение списка page_filters")
    public static List<PageFilter> getPageFiltersList() {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PAGE_FILTERS)
                .assertStatus(200)
                .jsonPath()
                .getList("", PageFilter.class);
    }

    @Step("Получение page_filters по ключу")
    public static PageFilter getPageFilter(String key) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PAGE_FILTERS + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение списка Pages")
    public static List<Pages> getPagesList() {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PAGES)
                .assertStatus(200)
                .jsonPath()
                .getList("", Pages.class);
    }

    @Step("Получение списка Pages по фильтру")
    public static List<Pages> getPagesList(String... filter) {
        String filters = String.join("&", filter);
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/pages/?" + filters)
                .assertStatus(200)
                .jsonPath()
                .getList("", Pages.class);
    }

    @Step("Получение Pages по Id")
    public static Pages getPagesById(String pageId) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PAGES + pageId + "/")
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение Pages по имени {name}")
    public static Response getPagesByName(String name) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PAGES + "?name={}", name)
                .assertStatus(200);
    }

    @Step("Получение списка directories для приватных ролей")
    public static List<Directories> getPrivateDirectoriesList() {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES)
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Directories>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание directory для приватных ролей")
    public static Directories createDirectory(JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .api(apiV1PrivateDirectoriesCreate)
                .extractAs(Directories.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Создание directory для приватных ролей c недопустимыми символами")
    public static Response createDirectoryWithInvalidName(JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .post(API_V_1_PRIVATE_DIRECTORIES);
    }

    @Step("Получение directory по имени для приватных ролей")
    public static Directories getPrivateDirectoryByName(String name) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Изменение directory по имени для приватных ролей")
    public static Directories updatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .put(API_V_1_PRIVATE_DIRECTORIES + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Частичное изменение directory по имени для приватных ролей")
    public static Directories partialUpdatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .api(apiV1PrivateDirectoriesPartialUpdate, name)
                .extractAs(Directories.class);
    }

    @Step("Частичное изменение directory по имени для приватных ролей")
    public static Directories partialUpdatePrivateDirectoryByName(String name, JSONObject jsonObject, Role role) {
        return new Http(referencesURL)
                .setRole(role)
                .body(jsonObject)
                .api(apiV1PrivateDirectoriesPartialUpdate, name)
                .extractAs(Directories.class);
    }

    @Step("Получение списка Pages по имени Directory для приватных ролей")
    public static List<Pages> getPrivatePagesListByDirectoryName(String directoryName) {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение списка Pages по имени Directory и имени Page для приватных ролей")
    public static List<Pages> getPrivatePagesListByDirectoryNameAndPageName(String directoryName, String pageName) {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages?name=" + pageName)
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Обновление Page через update_data")
    public static Response uncheckedUpdateDataPageRequest(UpdateData data) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(JsonHelper.toJson(data))
                .post(API_V_1_UPDATE_DATA);
    }

    @Step("Обновление Page через update_data")
    public static void checkedUpdateDataPageRequest(UpdateData data) {
        uncheckedUpdateDataPageRequest(data)
                .assertStatus(200);
    }

    @Step("Получение Pages по Id для приватных ролей")
    public static Pages getPrivatePagesById(String directoryName, String pageId) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение Pages по name = {name} для приватных ролей")
    public static Pages getPrivatePagesByName(String directoryName, String name) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_2_PRIVATE_DIRECTORIES + directoryName + "/pages/" + name)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение ответа на запрос Pages по Id для приватных ролей")
    public static Response getPrivateResponsePagesById(String directoryName, String pageId) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + pageId)
                .assertStatus(200);
    }

    @Step("Обновление Pages по Id для приватных ролей")
    public static Pages updatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .put(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Обновление Pages по name = {name} для приватных ролей")
    public static Pages updatePrivatePagesByName(String directoryName, String name, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .put(API_V_2_PRIVATE_DIRECTORIES + directoryName + "/pages/" + name)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Частичное обновление Pages по Id для приватных ролей")
    public static Pages partialUpdatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + pageId + "/")
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Частичное обновление Pages по name = {name} для приватных ролей")
    public static Pages partialUpdatePrivatePagesByName(String directoryName, String name, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(API_V_2_PRIVATE_DIRECTORIES + directoryName + "/pages/" + name)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Удаление Pages по Id для приватных ролей")
    public static void deletePrivatePagesById(String directoryName, String pageId) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + pageId)
                .assertStatus(204);
    }

    @Step("Удаление Pages по name = {name} для приватных ролей")
    public static void deletePrivatePagesByName(String directoryName, String name) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(API_V_2_PRIVATE_DIRECTORIES + directoryName + "/pages/" + name)
                .assertStatus(204);
    }

    @Step("Создание Pages для приватных ролей")
    public static Pages createPrivatePagesAndGet(String directoryName, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .post(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages")
                .assertStatus(201)
                .extractAs(Pages.class);
    }

    @Step("Создание Pages для приватных ролей")
    public static Response createPrivatePages(String directoryName, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .post(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages")
                .assertStatus(201);
    }

    @Step("Создание Pages для приватных ролей")
    public static Response createPrivatePagesAndGetResponse(String directoryName, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .post(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages");
    }

    @Step("Удаление directory по имени для приватных ролей")
    public static void deletePrivateDirectoryByName(String name) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(API_V_1_PRIVATE_DIRECTORIES + name + "/")
                .assertStatus(204);
    }

    @Step("Получение списка page_filters для приватных ролей")
    public static List<PageFilter> getPrivatePageFiltersList() {
        String jsonArray = new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_PAGE_FILTERS)
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<PageFilter>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание page_filters для приватных ролей")
    public static PageFilter createPrivatePageFilter(JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .post(API_V_1_PRIVATE_PAGE_FILTERS)
                .assertStatus(201)
                .extractAs(PageFilter.class);
    }

    @Step("Проверка наличия page_filters для приватных ролей")
    public static boolean isPrivatePageFilterExist(String key) {
        return getPrivatePageFiltersList().stream().anyMatch(x -> x.getKey().equals(key));
    }

    @Step("Обновление page_filters для приватных ролей")
    public static PageFilter updatePrivatePageFilter(String key, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .put(API_V_1_PRIVATE_PAGE_FILTERS + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение page_filters по ключу для приватных ролей")
    public static PageFilter getPrivatePageFilter(String key) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_PAGE_FILTERS + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Частичное обновление page_filters для приватных ролей")
    public static PageFilter partialUpdatePrivatePageFilter(String key, JSONObject object) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(API_V_1_PRIVATE_PAGE_FILTERS + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Удаление page_filters по ключу для приватных ролей")
    public static void deletePrivatePageFiltersByKey(String key) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(API_V_1_PRIVATE_PAGE_FILTERS + key + "/")
                .assertStatus(204);
    }

    @Step("Импорт Directories для приватных ролей")
    public static void importPrivateDirectories(String path) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart("/api/v1/private/directories/obj_import/", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Directories для приватных ролей")
    public static void exportPrivateDirectories(String name) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Импорт Pages для приватных ролей")
    public static void importPrivatePages(String path, String directoryName) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/import", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Pages для приватных ролей")
    public static void exportPrivatePages(String directoryName, String id) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_DIRECTORIES + directoryName + "/pages/" + id + "/export")
                .assertStatus(200);
    }

    @Step("Экспорт Pages для приватных ролей по name = {name}")
    public static void exportPrivatePagesByName(String directoryName, String name) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_2_PRIVATE_DIRECTORIES + directoryName + "/pages/" + name + "/export")
                .assertStatus(200);
    }

    @Step("Импорт Page_filter для приватных ролей")
    public static void importPrivatePageFilter(String path) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart("/api/v1/private/page_filters/obj_import/", "file", new File(path))
                .assertStatus(200);
    }

    @Step("Экспорт Page_filter для приватных ролей")
    public static void exportPrivatePageFilter(String key) {
        new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(API_V_1_PRIVATE_PAGE_FILTERS + key + "/obj_export/")
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

    @Step("Получение списка audit для директории с name {name}")
    public static List<ProductAudit> getDirectoryAuditList(String name) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .api(apiV1DirectoriesAudit, name)
                .jsonPath()
                .getList("", ProductAudit.class);
    }

    @Step("Получение списка audit для справочника с id {id} и фильтром {queryBuilder}")
    public static List<ProductAudit> getDirectoryAuditListWithQuery(String directoryName, QueryBuilder queryBuilder) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .api(apiV1DirectoriesAudit, directoryName, queryBuilder)
                .jsonPath()
                .getList("", ProductAudit.class);
    }

    @Step("Получение списка audit details для справочника")
    public static Response getDirectoryAuditDetails(String auditId) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .api(apiV1DirectoriesAuditDetails, new QueryBuilder().add("audit_id", auditId));
    }

    @Step("Получение списка аудита справочника для obj_keys")
    public static List<ProductAudit> getAuditListForDirectoryKeys(String keyValue) {
        return new Http(referencesURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("obj_keys", new JSONObject().put("name", keyValue)))
                .api(apiV1PrivateDirectoriesAuditByObjectKeys)
                .jsonPath()
                .getList("", ProductAudit.class);
    }
}
