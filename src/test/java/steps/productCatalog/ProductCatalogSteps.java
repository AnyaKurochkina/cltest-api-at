package steps.productCatalog;

import api.cloud.productCatalog.IProductCatalog;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.MetaImpl;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static core.helper.Configure.ProductCatalogURL;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static steps.keyCloak.KeyCloakSteps.getNewUserToken;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCatalogSteps {
    String entityName;
    String templatePath;

    public ProductCatalogSteps(String entityName) {
        this.entityName = entityName;
    }

    @Step("Импорт нескольких {entityName}")
    public static io.restassured.response.Response importObjects(String entityName, String pathName, String pathName2) {
        return given().log().all()
                .baseUri(ProductCatalogURL)
                .config(RestAssured.config().sslConfig(Http.sslConfig))
                .header("authorization", "bearer " + getNewUserToken(Role.PRODUCT_CATALOG_ADMIN))
                .multiPart(new File(pathName))
                .multiPart(new File(pathName2))
                .when()
                .post("/api/v1/{entityName}/obj_import/", entityName)
                .then().log().all()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("jsonSchema/importResponseSchema.json"))
                .statusCode(200)
                .extract()
                .response();
    }

    @Step("Экспорт нескольких {entityName} по Id")
    public static Response exportObjectsById(String entityName, JSONObject json) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .post("/api/v1/{}/objects_export/", entityName)
                .assertStatus(200);
    }

    @Step("Получение версии продуктового каталога")
    public static Response getProductCatalogVersion() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/version/")
                .assertStatus(200);
    }

    @Step("Получение статуса health")
    public static String getHealthStatusProductCatalog() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/health/")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Загрузка объекта в Gitlab")
    public Response dumpToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(entityName + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка объекта из Gitlab")
    public Response loadFromBitbucket(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(entityName + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Получение Meta данных объекта продуктового каталога")
    public MetaImpl getMeta(Class<?> clazz) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName)
                .assertStatus(200)
                .extractAs(clazz)).getMeta();
    }

    @Step("Создание объекта продуктового каталога")
    public Response createProductObject(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(entityName);
    }

    @Step("Проверка существования объекта продуктового каталога по имени")
    public boolean isExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Получение объекта продуктового каталога по Id")
    public GetImpl getById(String objectId, Class<?> clazz) {
        return (GetImpl) new Http(ProductCatalogURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get(entityName + objectId + "/")
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по Id без токена")
    public void getByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + objectId + "/").assertStatus(401);
    }

    @Step("Копирование объекта продуктового каталога по Id")
    public void copyById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(entityName + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(entityName + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Экспорт объекта продуктового каталога по Id")
    public void exportById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + objectId + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по имени")
    public void deleteByName(String name, Class<?> clazz) {
        deleteById(getProductObjectIdByNameWithMultiSearch(name, clazz));
    }

    @Step("Обновление всего объекта продуктового каталога по Id")
    public void putObjectById(String objectId, JSONObject body) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(entityName + objectId + "/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по Id")
    public void deleteById(String objectId) {
        getDeleteObjectResponse(objectId).assertStatus(204);
    }

    @Step("Удаление объекта продуктового каталога по Id без токена")
    public void deleteObjectByIdWithOutToken(String id) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .delete(entityName + id + "/").assertStatus(401);
    }

    @Step("Поиск ID объекта продуктового каталога по имени с использованием multiSearch")
    public String getProductObjectIdByNameWithMultiSearch(String name, Class<?> clazz) {
        String objectId = null;
        List<ItemImpl> list = ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(clazz)).getItemsList();
        for (ItemImpl item : list) {
            if (item.getName().equals(name)) {
                objectId = item.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @SneakyThrows
    @Step("Поиск ID объекта продуктового каталога по Title")
    public String getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(String title, String parameters) {
//        String productNameWithEncode = title.replaceAll("Разработка", "%D0%A0%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0");
        String productNameWithEncode = URLEncoder.encode(title, StandardCharsets.UTF_8.name());
        return Objects.requireNonNull(new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("{}?multisearch={}&{}", entityName, productNameWithEncode, parameters)
                .assertStatus(200)
                .jsonPath()
                .getString("list.find{it.title.toLowerCase()=='" + title.toLowerCase() + "'}.id"), "ID продукта: " + title + " не найден");
    }

    @Step("Создание JSON объекта продуктового каталога")
    public JSONObject createJsonObject(String name) {
        return JsonHelper
                .getJsonTemplate(templatePath)
                .set("$.name", name)
                .build();
    }

    @Step("Частичное обновление продукта")
    public Response partialUpdateObject(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(entityName + id + "/");
    }

    //todo "Получить сообщение, сравнить с ответом"
    @Step("Частичное обновление продукта без токена")
    public void partialUpdateObjectWithOutToken(String id, JSONObject object) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(entityName + id + "/")
                .assertStatus(401);
    }

    public Response getDeleteObjectResponse(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(entityName + id + "/");
    }

    @Step("Получение списка объектов продуктового каталога по фильтру")
    public List<ItemImpl> getObjectsList(Class<?> clazz, String filter) {
        return ((GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + filter)
                .assertStatus(200)
                .extractAs(clazz)).getItemsList();
    }

    public JsonPath getJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + id + "/")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение объекта продуктового каталога по title")
    public GetListImpl getObjectByTitle(String title, Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "?title=" + title)
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по дате создания")
    public GetListImpl orderingByCreateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по дате обновления")
    public GetListImpl orderingByUpDateData(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Сортировка объектов по статусу")
    public GetListImpl orderingByStatus(Class<?> clazz) {
        return (GetListImpl) new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(entityName + "?ordering=status")
                .assertStatus(200)
                .extractAs(clazz);
    }

    @Step("Получение объекта продуктового каталога по имени с публичным токеном")
    public Response getObjectByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(entityName + "?name=" + name);
    }

    @Step("Создание объекта продуктового каталога с публичным токеном")
    public Response createProductObjectWithPublicToken(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(entityName);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public Response partialUpdateObjectWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(entityName + id + "/");
    }

    @Step("Удаление объекта продуктового каталога с публичным токеном")
    public Response deleteObjectWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(entityName + id + "/");
    }

    @Step("Обновление всего объекта продуктового каталога по Id с публичным токеном")
    public Response putObjectByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(entityName + objectId + "/");
    }

    @Step("Проверка сортировки списка")
    public static boolean isSorted(List<? extends IProductCatalog> list) {
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

    public static String delNoDigOrLet(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public Response getProductByContextProject(String projectId, String productId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/{}/", projectId, productId)
                .assertStatus(200);
    }

    @Step("Получение списка доступных категорий по id проекта")
    public List<String> getAvailableCategoriesByContextProject(String projectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/categories/", projectId)
                .assertStatus(200)
                .jsonPath().getList("");
    }

    @Step("Получение доступных категорий")
    public List<String> getAvailableCategories() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/categories/")
                .assertStatus(200)
                .jsonPath().getList("");
    }
}
