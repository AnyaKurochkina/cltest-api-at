package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.AbstractEntity;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.product.GetProductList;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.product.ProductOrderRestriction;
import org.json.JSONArray;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static api.routes.ProductProductCatalogApi.apiV1ProductsCreate;
import static core.helper.Configure.productCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;
import static steps.productCatalog.ProductCatalogSteps.getProductCatalogAdmin;

public class ProductSteps extends Steps {
    private static final String productUrl = "/api/v1/products/";
    private static final String productContextUrl = "/api/v1/projects/{}/products/{}/";
    private static final String productUrlV2 = "/api/v2/products/";

    @Step("Получение списка Продуктов")
    public static List<Product> getProductList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение Meta списка Продуктов")
    public static Meta getMetaProductList() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getMeta();
    }

    public static List<Product> getProductListByProjectContext(String projectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/" + projectId + "/products/?is_open=true")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    public static Response getProductByProjectContext(String projectId, String productId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/{}/", projectId, productId);
    }

    @Step("Получение списка продуктов по фильтру {filter}")
    public static List<Product> getProductListByFilter(String filter) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + "?{}", filter)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение списка продуктов по фильтрам")
    public static List<Product> getProductListByFilters(String... filter) {
        String filters = String.join("&", filter);
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "?" + filters)
                .assertStatus(200)
                .extractAs(GetProductList.class)
                .getList();
    }

    /*
    На данный момент массово можно изменить только один параметр is_open
     */
    @Step("Массовое изменение параметров продукта")
    public static Response massChangeProductParam(List<String> id, boolean isOpen) {
        return uncheckedMassChangeProductParamResponse(id, isOpen)
                .assertStatus(200);
    }

    @Step("Массовое изменение параметров продукта")
    public static Response uncheckedMassChangeProductParamResponse(List<String> id, boolean isOpen) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("objects_change", new JSONArray().put(new JSONObject().put("id", id)
                        .put("params", new JSONObject().put("is_open", isOpen)))))
                .post(productUrl + "mass_change/");
    }

    @Step("Получение продукта по имени {name}")
    public static Product getProductByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrlV2 + name + "/")
                .extractAs(Product.class);
    }

    @Step("Создание продукта")
    public static Product createProduct(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productUrl)
                .assertStatus(201)
                .extractAs(Product.class);
    }

    @Step("Создание продукта")
    public static Product createProduct(Product product) {
        return getProductCatalogAdmin()
                .body(product.toJson())
                .api(apiV1ProductsCreate)
                .extractAs(Product.class)
                .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Step("Создание продукта")
    public static Product createProduct(String name) {
        return Product.builder()
                .name(name)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .build()
                .createObject();
    }

    @Step("Создание продукта")
    public static Product createProduct(String name, String title) {
        return Product.builder()
                .name(name)
                .title(title)
                .version("1.0.0")
                .build()
                .createObject();
    }

    @Step("Создание продукта")
    public static Response getCreateProductResponse(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post("/api/v1/products/");
    }

    @Step("Проверка сортировки списка продуктов")
    public static boolean isProductListSorted(List<Product> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            Product currentProduct = list.get(i);
            Product nextProduct = list.get(i + 1);
            Integer currentNumber = currentProduct.getNumber();
            Integer nextNumber = nextProduct.getNumber();
            String currentTitle = delNoDigOrLet(currentProduct.getTitle());
            String nextTitle = delNoDigOrLet(nextProduct.getTitle());
            if (currentNumber > nextNumber || ((currentNumber.equals(nextNumber)) && currentTitle.compareToIgnoreCase(nextTitle) > 0)) {
                return false;
            }
        }
        return true;
    }

    @Step("Получение продукта по Id")
    public static Product getProductById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + objectId + "/")
                .extractAs(Product.class);
    }

    @Step("Получение order_restrictions продукта по Id")
    public static Response getProductOrderRestrictionById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + objectId + "/order_restrictions/")
                .assertStatus(200);
    }

    @Step("Обновление order_restrictions продукта по Id")
    public static ProductOrderRestriction updateProductOrderRestrictionById(String objectId, String restrictionId, JSONObject json) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .patch(productUrl + objectId + "/order_restrictions/?order_restriction_id={}", restrictionId)
                .compareWithJsonSchema("jsonSchema/createProductOrderRestriction.json")
                .assertStatus(200)
                .extractAs(ProductOrderRestriction.class);
    }

    @Step("Создание order_restrictions продукта по Id")
    public static Response createProductOrderRestrictionById(String objectId, JSONObject jsonObject) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(productUrl + objectId + "/order_restrictions/");
    }

    @Step("Удаление order_restrictions продукта по Id")
    public static void deleteProductOrderRestrictionById(String objectId, String restrictionId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrl + objectId + "/order_restrictions/?order_restriction_id={}", restrictionId)
                .assertStatus(200);
    }

    @Step("Получение order_restrictions продукта по имени")
    public static Response getProductOrderRestrictionByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrlV2 + name + "/order_restrictions/")
                .assertStatus(200);
    }

    @Step("Создание order_restrictions продукта по имени")
    public static Response createProductOrderRestrictionByName(String name, JSONObject jsonObject) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(jsonObject)
                .post(productUrlV2 + name + "/order_restrictions/")
                .assertStatus(200);
    }

    @Step("Обновление order_restrictions продукта по имени")
    public static ProductOrderRestriction updateProductOrderRestrictionByName(String name, String restrictionId, JSONObject json) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(json)
                .patch(productUrlV2 + name + "/order_restrictions/?order_restriction_id={}", restrictionId)
                .compareWithJsonSchema("jsonSchema/createProductOrderRestriction.json")
                .assertStatus(200)
                .extractAs(ProductOrderRestriction.class);
    }

    @Step("Удаление order_restrictions продукта по имени")
    public static void deleteProductOrderRestrictionByName(String name, String restrictionId) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrlV2 + name + "/order_restrictions/?order_restriction_id={}", restrictionId)
                .assertStatus(200);
    }

    @Step("Получение продукта по Id без токена")
    public static Response getProductByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + objectId + "/").assertStatus(401);
    }

    @Step("Получение продукта по Id")
    public static Product getProductByCloudAdmin(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + objectId + "/")
                .extractAs(Product.class);
    }

    @Step("Получение продукта по Id и фильтру {filter}")
    public static Product getProductByIdAndFilter(String objectId, String filter) {
        return new Http(productCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + objectId + "/?{}", filter)
                .extractAs(Product.class);
    }

    @Step("Получение продукта по Id под ролью Viewer")
    public static Response getProductViewerById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productUrl + objectId + "/");
    }

    @Step("Проверка существования продукта по имени")
    public static boolean isProductExists(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Сортировка продуктов по дате создания")
    public static boolean orderingProductByCreateData() {
        List<Product> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "?ordering=create_dt")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }

    @Step("Сортировка продуктов по дате создания")
    public static boolean orderingProductByUpdateData() {
        List<Product> list = new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "?ordering=update_dt")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
            if (!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
                return false;
            }
        }
        return true;
    }

    public static Response getDeleteProductResponse(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrl + id + "/");
    }

    @Step("Частичное обновление продукта")
    public static Response partialUpdateProduct(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(productUrl + id + "/");
    }

    @Step("Частичное обновление продукта по имени {name}")
    public static void partialUpdateProductByName(String name, JSONObject object) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(productUrlV2 + name + "/");
    }

    public static Response partialUpdateProductWithOutToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(productUrl + id + "/")
                .assertStatus(401);
    }

    @Step("Обновление продукта")
    public static Product updateProduct(String id, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(productUrl + id + "/")
                .assertStatus(200).extractAs(Product.class);
    }

    @Step("Удаление продукта по Id")
    public static void deleteProductById(String id) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Получение продукта по имени {name} с публичным токеном")
    public static Response getProductByNameWithPublicToken(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productUrl + "?name=" + name);
    }

    @Step("Импорт продукта")
    public static ImportObject importProduct(String pathName) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(productUrl + "obj_import/", "file", new File(pathName))
                .assertStatus(200)
                .compareWithJsonSchema("jsonSchema/importResponseSchema.json")
                .jsonPath()
                .getList("imported_objects", ImportObject.class)
                .get(0);
    }

    @Step("Экспорт продукта по имени {name}")
    public static void exportProductByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Экспорт продукта по Id {id}")
    public static Response exportProductById(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + id + "/obj_export/?as_file=true")
                .assertStatus(200);
    }

    @Step("Загрузка продукта в Gitlab")
    public static Response dumpProductToBitbucket(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Загрузка продукта в Gitlab по имени {name}")
    public static Response dumpProductToGitByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrlV2 + name + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка продукта из Gitlab")
    public static void loadProductFromBitbucket(JSONObject body) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Копирование продукта по Id")
    public static Product copyProductById(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + objectId + "/copy/")
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Копирование продукта по имени {name}")
    public static Product copyProductByName(String name) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrlV2 + name + "/copy/")
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Копирование продукта по Id без ключа")
    public static Response copyProductByIdWithOutToken(String objectId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(productUrl + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Удаление продукта по имени {name}")
    public static void deleteProductByName(String name) {
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Получение info продукта")
    public static Response getInfoProduct(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + id + "/info/")
                .assertStatus(200);
    }

    @Step("Получение списка продуктов отсортированного по статусу is_open")
    public static List<Product> getProductListOrderingByStatus() {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "?ordering=status")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Проверка сортировки списка")
    public static boolean isOrderingByStatus(List<Product> list) {
        int count = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            Product item = list.get(i);
            Product nextItem = list.get(i + 1);
            if (!item.getIsOpen().equals(nextItem.getIsOpen())) {
                count++;
            }
            if (count > 1) {
                return false;
            }
        }
        return true;
    }

    @Step("Создание продукта с публичным токеном")
    public static Response createProductWithPublicToken(JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(productUrl);
    }

    @Step("Обновление продукта с публичным токеном")
    public static Response partialUpdateProductWithPublicToken(String id, JSONObject object) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(productUrl + id + "/");
    }

    @Step("Обновление продукта по Id с публичным токеном")
    public static Response putProductByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(productUrl + objectId + "/");
    }

    @Step("Удаление продукта с публичным токеном")
    public static Response deleteProductWithPublicToken(String id) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(productUrl + id + "/");
    }

    @Step("Удаление продукта по Id без токена")
    public static Response deleteProductByIdWithOutToken(String id) {
        return new Http(productCatalogURL)
                .setWithoutToken()
                .delete(productUrl + id + "/").assertStatus(401);
    }

    @Step("Добавление списка Тегов продуктам")
    public static void addTagListToProduct(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("add_tags", tagsList))
                .post(productUrl + "add_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    @Step("Удаление списка Тегов продуктов")
    public static void removeTagListToProduct(List<String> tagsList, String... name) {
        String names = String.join(",", name);
        new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(new JSONObject().put("remove_tags", tagsList))
                .post(productUrl + "remove_tag_list/?name__in=" + names)
                .assertStatus(200);
    }

    public static Response getProductByContextProject(String projectId, String productId) {
        return new Http(productCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productContextUrl, projectId, productId)
                .assertStatus(200);
    }
}
