package steps.references;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import tests.Tests;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static core.helper.Configure.ReferencesURL;

public class ReferencesStep extends Tests {

    @Step("Получение списка flavors для продукта {product}")
    public static List<Flavor> getProductFlavorsLinkedList(IProduct product) {
        String jsonArray = new Http(ReferencesURL)
                .setProjectId(Objects.requireNonNull(product.getProjectId()))
                .get("pages/?directory__name=flavors&tags={}", product.getProductId())
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
                .get("pages/?page_filter={}", pageFilter)
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
                .get("pages/?{}", attrs)
                .assertStatus(200)
                .jsonPath();
    }

    @Step("Получение списка directories")
    public List<Directories> getDirectoriesList() {
        String jsonArray = new Http(ReferencesURL).get("directories/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Directories>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение directory по имени")
    public Directories getDirectoryByName(String name) {
        return new Http(ReferencesURL)
                .get("directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Получение списка page_filters")
    public List<PageFilter> getPageFiltersList() {
        String jsonArray = new Http(ReferencesURL)
                .get("page_filters/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<PageFilter>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение page_filters по ключу")
    public PageFilter getPageFilter(String key) {
        return new Http(ReferencesURL)
                .get("page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение списка Pages")
    public List<Pages> getPagesList() {
        String jsonArray = new Http(ReferencesURL).get("/pages/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение Pages по Id")
    public Pages getPagesById(String pageId) {
        return new Http(ReferencesURL).get("/pages/" + pageId + "/")
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение списка directories для приватных ролей")
    public List<Directories> getPrivateDirectoriesList() {
        String jsonArray = new Http(ReferencesURL).get("private/directories/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Directories>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание directory для приватных ролей")
    public Directories createDirectory(JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("private/directories/")
                .assertStatus(201)
                .extractAs(Directories.class);
    }

    @Step("Получение directory по имени для приватных ролей")
    public Directories getPrivateDirectoryByName(String name) {
        return new Http(ReferencesURL)
                .get("private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Изменение directory по имени для приватных ролей")
    public Directories updatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(ReferencesURL)
                .body(jsonObject)
                .put("private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Частичное изменение directory по имени для приватных ролей")
    public Directories partialUpdatePrivateDirectoryByName(String name, JSONObject jsonObject) {
        return new Http(ReferencesURL)
                .body(jsonObject)
                .patch("private/directories/" + name + "/")
                .assertStatus(200)
                .extractAs(Directories.class);
    }

    @Step("Получение списка Pages по имени Directory для приватных ролей")
    public List<Pages> getPrivatePagesListByDirectoryName(String directoryName) {
        String jsonArray = new Http(ReferencesURL).get("private/directories/" + directoryName + "/pages")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<Pages>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Получение Pages по Id для приватных ролей")
    public Pages getPrivatePagesById(String directoryName, String pageId) {
        return new Http(ReferencesURL).get("private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Получение ответа на запрос Pages по Id для приватных ролей")
    public Response getPrivateResponsePagesById(String directoryName, String pageId) {
        return new Http(ReferencesURL).get("private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200);
    }

    @Step("Обновление Pages по Id для приватных ролей")
    public Pages updatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .put("private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Обновление Data в Pages по Id для приватных ролей")
    public void updateDataPrivatePagesById(String directoryName, String pageId, JSONObject object) {
        new Http(ReferencesURL)
                .body(object)
                .post("private/directories/" + directoryName + "/pages/" + pageId + "/update_data")
                .assertStatus(200);
    }

    @Step("Частичное обновление Pages по Id для приватных ролей")
    public Pages partialUpdatePrivatePagesById(String directoryName, String pageId, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .patch("private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(200)
                .extractAs(Pages.class);
    }

    @Step("Удаление Pages по Id для приватных ролей")
    public void deletePrivatePagesById(String directoryName, String pageId) {
        new Http(ReferencesURL)
                .delete("private/directories/" + directoryName + "/pages/" + pageId)
                .assertStatus(204);
    }

    @Step("Создание Pages для приватных ролей")
    public Pages createPrivatePagesAndGet(String directoryName, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("private/directories/" + directoryName + "/pages")
                .assertStatus(201)
                .extractAs(Pages.class);
    }

    @Step("Создание Pages для приватных ролей")
    public Response createPrivatePages(String directoryName, JSONObject object) {
         return new Http(ReferencesURL)
                .body(object)
                .post("private/directories/" + directoryName + "/pages")
                .assertStatus(201);
    }

    @Step("Удаление directory по имени для приватных ролей")
    public void deletePrivateDirectoryByName(String name) {
        new Http(ReferencesURL)
                .delete("private/directories/" + name + "/")
                .assertStatus(204);
    }

    @Step("Получение списка page_filters для приватных ролей")
    public List<PageFilter> getPrivatePageFiltersList() {
        String jsonArray = new Http(ReferencesURL)
                .get("private/page_filters/")
                .assertStatus(200)
                .toString();
        Type type = new TypeToken<List<PageFilter>>() {
        }.getType();
        return new Gson().fromJson(jsonArray, type);
    }

    @Step("Создание page_filters для приватных ролей")
    public PageFilter createPrivatePageFilter(JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .post("private/page_filters/")
                .assertStatus(201)
                .extractAs(PageFilter.class);
    }

    @Step("Обновление page_filters для приватных ролей")
    public PageFilter updatePrivatePageFilter(String key, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .put("private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Получение page_filters по ключу для приватных ролей")
    public PageFilter getPrivatePageFilter(String key) {
        return new Http(ReferencesURL)
                .get("private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Частичное обновление page_filters для приватных ролей")
    public PageFilter partialUpdatePrivatePageFilter(String key, JSONObject object) {
        return new Http(ReferencesURL)
                .body(object)
                .patch("private/page_filters/" + key + "/")
                .assertStatus(200)
                .extractAs(PageFilter.class);
    }

    @Step("Удаление page_filters по ключу для приватных ролей")
    public void deletePrivatePageFiltersByKey(String key) {
        new Http(ReferencesURL)
                .delete("private/page_filters/" + key + "/")
                .assertStatus(204);
    }
}
