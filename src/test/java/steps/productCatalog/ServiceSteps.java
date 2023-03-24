package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.service.GetServiceList;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ServiceSteps extends Steps {

    private static final String serviceUrl = "/api/v1/services/";
    private static final String serviceUrlV2 = "/api/v2/services/";

    @Step("Получение списка Сервисов продуктового каталога")
    public static List<Service> getServiceList() {
        return  new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl)
                .compareWithJsonSchema("jsonSchema/getServiceListSchema.json")
                .assertStatus(200)
                .extractAs(GetServiceList.class).getList();
    }

    @Step("Создание сервиса")
    public static Response createService(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(serviceUrl);
    }

    @Step("Создание сервиса")
    public static Service createService(String name) {
        return Service.builder()
                .name(name)
                .build()
                .createObject();
    }

    @Step("Частичное обновление сервиса по id")
    public static Response partialUpdateServiceById(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(serviceUrl + id + "/");
    }

    @Step("Получение сервиса по Id")
    public static Service getServiceById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + objectId + "/")
                .assertStatus(200)
                .extractAs(Service.class);
    }

    @Step("Получение сервиса по имени {name}")
    public static Service getServiceByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrlV2  + name + "/")
                .extractAs(Service.class);
    }

    @Step("Получение сервиса по Id и фильтру {filter}")
    public static Service getServiceByIdAndFilter(String objectId, String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + objectId + "/?{}", filter)
                .extractAs(Service.class);
    }

    @Step("Получение сервиса по Id под ролью Viewer")
    public static Response getServiceViewerById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(serviceUrl + objectId + "/");
    }

    @Step("Удаление сервиса по id")
    public static void deleteServiceById(String id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(serviceUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление сервиса по имени {name}")
    public static void deleteServiceByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(serviceUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Проверка существования сервиса по имени")
    public static boolean isServiceExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Частичное обновление сервиса по имени {name}")
    public static Response partialUpdateServiceByName(String name, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(serviceUrlV2 + name + "/");
    }

    @Step("Копирование сервиса по имени {name}")
    public static Service copyServiceByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(serviceUrlV2 + name + "/copy/")
                .assertStatus(200)
                .extractAs(Service.class);
    }

    @Step("Загрузка сервиса в Gitlab по имени {name}")
    public static Response dumpServiceToGitByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(serviceUrlV2 + name + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Экспорт сервиса по имени {name}")
    public static void exportServiceByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Экспорт сервиса по Id {id}")
    public static Response exportServiceById(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(serviceUrl + id + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Импорт сервиса")
    public static ImportObject importService(String pathName) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(serviceUrl + "obj_import/", "file", new File(pathName))
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }
}
