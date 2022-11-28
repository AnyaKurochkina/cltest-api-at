package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.product.GetProductList;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ProductSteps extends Steps {
    private static final String productUrl = "/api/v1/products/";

    @Step("Получение списка Продуктов")
    public static List<Product> getProductList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение Meta списка Продуктов")
    public static Meta getMetaProductList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getMeta();
    }

    public static List<Product> getProductListByProjectContext(String projectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/" + projectId + "/products/?is_open=true")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение списка продуктов по фильтру {filter}")
    public static List<Product> getProductListByFilter(String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + "?{}", filter)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение продукта по имени")
    public static Product getProductByName(String name) {
        List<Product> list = new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + "?{}", "name=" + name)
                .extractAs(GetProductList.class).getList();
        assertEquals(name, list.get(0).getName());
        return list.get(0);
    }

    @Step("Создание продукта")
    public static Product createProduct(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post("/api/v1/products/")
                .assertStatus(201)
                .extractAs(Product.class);
    }

    @Step("Создание продукта")
    public static Response getCreateProductResponse(JSONObject body) {
        return new Http(ProductCatalogURL)
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
        return new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + objectId + "/")
                .extractAs(Product.class);
    }

    @Step("Получение продукта по Id и фильтру {filter}")
    public static Product getProductByIdAndFilter(String objectId, String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + objectId + "/?{}", filter)
                .extractAs(Product.class);
    }

    @Step("Получение продукта по Id под ролью Viewer")
    public static Response getProductViewerById(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productUrl + objectId + "/");
    }

    @Step("Проверка существования продукта по имени")
    public static boolean isProductExists(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "exists/?name=" + name)
                .assertStatus(200).jsonPath().get("exists");
    }

    @Step("Сортировка продуктов по дате создания")
    public static boolean orderingProductByCreateData() {
        List<Product> list = new Http(ProductCatalogURL)
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
        List<Product> list = new Http(ProductCatalogURL)
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
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrl + id + "/");
    }

    @Step("Частичное обновление продукта")
    public static Response partialUpdateProduct(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(productUrl + id + "/");
    }

    @Step("Обновление продукта")
    public static Product updateProduct(String id, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .put(productUrl + id + "/")
                .assertStatus(200).extractAs(Product.class);
    }

    @Step("Удаление продукта по Id")
    public static void deleteProductById(String id) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Получение продукта по имени {name} с публичным токеном")
    public static Response getProductByNameWithPublicToken(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .get(productUrl + "?name=" + name);
    }

    @Step("Импорт продукта")
    public static Response importProduct(String pathName) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(productUrl + "obj_import/", "file", new File(pathName));
    }

    @Step("Загрузка продукта в Gitlab")
    public static Response dumpProductToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка продукта из Gitlab")
    public static Response loadProductFromBitbucket(JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Поиск ID продукта по имени {name} с использованием multiSearch")
    public static String getProductObjectIdByNameWithMultiSearch(String name) {
        String objectId = null;
        List<Product> list = new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetProductList.class).getList();
        for (Product product : list) {
            if (product.getName().equals(name)) {
                objectId = product.getProductId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Продукт с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("Копирование продукта по Id")
    public static void copyProductById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Удаление объекта продуктового каталога по имени")
    public static void deleteProductByName(String name) {
        deleteProductById(getProductObjectIdByNameWithMultiSearch(name));
    }

    @Step("Получение info продукта")
    public static Response getInfoProduct(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + id + "/info/")
                .assertStatus(200);
    }

    @Step("Получение списка продуктов отсортированного по статусу is_open")
    public static List<Product> getProductListOrderingByStatus() {
        return new Http(ProductCatalogURL)
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
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .post(productUrl);
    }

    @Step("Обновление продукта с публичным токеном")
    public static Response partialUpdateProductWithPublicToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(object)
                .patch(productUrl + id + "/");
    }

    @Step("Обновление продукта по Id с публичным токеном")
    public static Response putProductByIdWithPublicToken(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .body(body)
                .put(productUrl + objectId + "/");
    }

    @Step("Удаление продукта с публичным токеном")
    public static Response deleteProductWithPublicToken(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_VIEWER)
                .delete(productUrl + id + "/");
    }
}
