package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.productCatalog.Meta;
import models.productCatalog.example.Example;
import models.productCatalog.example.GetExampleList;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ExampleSteps extends Steps {

    private static final String endPoint = "/api/v1/example/";

    @Step("Получение Примера продуктового каталога по Id")
    public static Example getExampleById(String exampleId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + exampleId + "/")
                .extractAs(Example.class);
    }

    @Step("Поиск ID Примера продуктового каталога по имени с использованием multiSearch")
    public static String getExampleIdByNameWithMultiSearch(String name) {
        String objectId = null;
        List<Example> list = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetExampleList.class).getList();
        for (Example example : list) {
            if (example.getName().equals(name)) {
                objectId = example.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("Обновление всего примера продуктового каталога по Id")
    public static Example putExampleById(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(endPoint + objectId + "/")
                .assertStatus(200).extractAs(Example.class);
    }

    @Step("Частичное обновление примера")
    public static Example partialUpdateExample(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(endPoint + id + "/")
                .assertStatus(200)
                .extractAs(Example.class);
    }

    @Step("Загрузка примера в Gitlab")
    public static Response dumpExampleToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(endPoint + id + "/dump_to_bitbucket/")
                .assertStatus(201);
    }

    @Step("Выгрузка объекта из Gitlab")
    public static void loadExampleFromBitbucket(String path) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("path", path))
                .post(endPoint + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Создание Примера продуктового каталога")
    public static Example createExample(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(endPoint)
                .assertStatus(201)
                .extractAs(Example.class);
    }

    @Step("Удаление Примера продуктового каталога по id")
    public static void deleteExampleById(String id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(endPoint + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление Примера продуктового каталога по имени")
    public static void deleteExampleByName(String name) {
        deleteExampleById(getExampleIdByNameWithMultiSearch(name));
    }

    @Step("Проверка существования Примера продуктового каталога по имени")
    public static boolean isExampleExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение списка Примеров продуктового каталога")
    public static List<Example> getExampleList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint)
                .assertStatus(200)
                .extractAs(GetExampleList.class).getList();
    }

    @Step("Проверка сортировки списка")
    public static boolean isExampleSorted(List<Example> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            String currentName = delNoDigOrLet(list.get(i).getName());
            String nextName = delNoDigOrLet(list.get(i + 1).getName());
            if (currentTime.isBefore(nextTime) || (currentTime.isEqual(nextTime) && currentName.compareToIgnoreCase(nextName) > 0)) {
                return false;
            }
        }
        return true;
    }

    @Step("Получение Meta данных объекта продуктового каталога")
    public static Meta getExampleMeta() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint)
                .assertStatus(200)
                .extractAs(GetExampleList.class).getMeta();
    }

    @Step("Получение списка Примеров продуктового каталога по имени")
    public static GetExampleList getExampleListByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "?name=" + name)
                .assertStatus(200)
                .extractAs(GetExampleList.class);
    }

    @Step("Получение списка Примеров продуктового каталога по именам")
    public static GetExampleList getExampleListByNames(String... name) {
        String names = String.join(",", name);
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(endPoint + "?name__in=" + names)
                .assertStatus(200)
                .extractAs(GetExampleList.class);
    }
}
