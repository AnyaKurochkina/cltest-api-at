package steps.productCatalog;

import api.cloud.productCatalog.IProductCatalog;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.ProductAudit;
import org.json.JSONObject;

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
    String resourcePath;
    String templatePath;

    public ProductCatalogSteps(String resourcePath) {
        this.resourcePath = resourcePath;
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

    @Step("Экспорт {entityName} по Id c tag_list")
    public static Response exportObjectByIdWithTags(String entityName, String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/{}/{}/obj_export/?as_file=true&with_tags=true", entityName, objectId)
                .assertStatus(200);
    }

    @Step("Импорт {entityName} продуктового каталога с tag_list")
    public static ImportObject importObjectWithTagList(String entityName, String pathName) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(StringUtils.format("/api/v1/{}/obj_import/?with_tags=true", entityName), "file", new File(pathName))
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Проверка item restrictions")
    public static Response checkItemRestrictions(JSONObject jsonObject) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post("/api/v1/check_item_restrictions/");
    }

    @Step("Получение списка audit для {entityName} с id {id}")
    public static List<ProductAudit> getObjectAuditList(String entityName, String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/{}/{}/audit/", entityName, id)
                .assertStatus(200)
                .jsonPath()
                .getList("list", ProductAudit.class);
    }

    @Step("Получение деталей audit {entityName} с audit_id {auditId}")
    public static Response getObjectAudit(String entityName, String auditId) {
         return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/{}/audit_details/?audit_id={}", entityName, auditId)
                .assertStatus(200);
    }

    @Step("Получение списка уникальных obj_keys для аудита {entityName}")
    public static void getUniqueObjectKeysListAudit(String entityName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/{}/audit_object_keys/", entityName)
                .assertStatus(200);
    }

    @Step("Получение списка аудита {entityName} для obj_keys")
    public static List<ProductAudit> getAuditListForObjKeys(String entityName, String keyValue) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("obj_keys", new JSONObject().put("name", keyValue)))
                .post("/api/v1/{}/audit_by_object_keys/", entityName)
                .assertStatus(200)
                .jsonPath()
                .getList("list", ProductAudit.class);
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
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/health")
                .assertStatus(200)
                .jsonPath()
                .getString("status");
    }

    @Step("Загрузка объекта в Gitlab")
    public Response dumpToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(resourcePath + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка объекта из Gitlab")
    public Response loadFromBitbucket(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(resourcePath + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Создание объекта продуктового каталога")
    public Response createProductObject(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(resourcePath);
    }

    @Step("Получение объекта продуктового каталога по Id без токена")
    public void getByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL).setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(resourcePath + objectId + "/").assertStatus(401);
    }

    @Step("Копирование объекта продуктового каталога по Id без ключа")
    public void copyByIdWithOutToken(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(resourcePath + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Удаление объекта продуктового каталога по Id без токена")
    public void deleteObjectByIdWithOutToken(String id) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .delete(resourcePath + id + "/")
                .assertStatus(401);
    }

    @SneakyThrows
    @Step("Поиск ID объекта продуктового каталога по Title")
    public String getProductIdByTitleIgnoreCaseWithMultiSearchAndParameters(String title, String parameters) {
//        String productNameWithEncode = title.replaceAll("Разработка", "%D0%A0%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0");
        String productNameWithEncode = URLEncoder.encode(title, StandardCharsets.UTF_8.name());
        return Objects.requireNonNull(new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("{}?multisearch={}&{}", resourcePath, productNameWithEncode, parameters)
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
                .patch(resourcePath + id + "/");
    }

    @Step("Частичное обновление продукта без токена")
    public void partialUpdateObjectWithOutToken(String id, JSONObject object) {
        new Http(ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(resourcePath + id + "/")
                .assertStatus(401);
    }

    public Response getDeleteObjectResponse(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(resourcePath + id + "/");
    }

    public JsonPath getJsonPath(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(resourcePath + id + "/")
                .assertStatus(200).jsonPath();
    }

    @Step("Получение объекта продуктового каталога по имени с публичным токеном")
    public Response getObjectByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(resourcePath + "?name=" + name);
    }

    @Step("Создание объекта продуктового каталога с публичным токеном")
    public Response createProductObjectWithPublicToken(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(resourcePath);
    }

    @Step("Обновление объекта продуктового каталога с публичным токеном")
    public Response partialUpdateObjectWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(resourcePath + id + "/");
    }

    @Step("Удаление объекта продуктового каталога с публичным токеном")
    public Response deleteObjectWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(resourcePath + id + "/");
    }

    @Step("Обновление всего объекта продуктового каталога по Id с публичным токеном")
    public Response putObjectByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(resourcePath + objectId + "/");
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
