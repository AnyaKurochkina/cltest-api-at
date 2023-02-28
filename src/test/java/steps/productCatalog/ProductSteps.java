package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.Meta;
import models.cloud.productCatalog.product.GetProductList;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ProductSteps extends Steps {
    private static final String productUrl = "/api/v1/products/";
    private static final String productUrlV2 = "/api/v2/products/";

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

    public static Product getProductByProjectContext(String projectId, String productId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/projects/{}/products/{}/", projectId, productId)
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Получение списка продуктов по фильтру {filter}")
    public static List<Product> getProductListByFilter(String filter) {
        return new Http(ProductCatalogURL)
                .setRole(Role.CLOUD_ADMIN)
                .get(productUrl + "?{}", filter)
                .extractAs(GetProductList.class).getList();
    }

    @Step("Получение продукта по имени {name}")
    public static Product getProductByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrlV2 + name + "/")
                .extractAs(Product.class);
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
    public static Product createProductByName(String name) {
        return Product.builder()
                .name(name)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .build()
                .createObject();
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
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + objectId + "/")
                .extractAs(Product.class);
    }

    @Step("Получение продукта по Id без токена")
    public static Response getProductByIdWithOutToken(String objectId) {
        return new Http(ProductCatalogURL)
                .setWithoutToken()
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrl + objectId + "/").assertStatus(401);
    }

    @Step("Получение продукта по Id")
    public static Product getProductByCloudAdmin(String objectId) {
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

    @Step("Частичное обновление продукта по имени {name}")
    public static void partialUpdateProductByName(String name, JSONObject object) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(object)
                .patch(productUrlV2 + name + "/");
    }

    public static Response partialUpdateProductWithOutToken(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .setWithoutToken()
                .body(object)
                .patch(productUrl + id + "/")
                .assertStatus(401);
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

    @Step("Экспорт продукта по имени {name}")
    public static void exportProductByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get(productUrlV2 + name + "/obj_export/")
                .assertStatus(200);
    }

    @Step("Загрузка продукта в Gitlab")
    public static Response dumpProductToBitbucket(String id) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + id + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Загрузка продукта в Gitlab по имени {name}")
    public static Response dumpProductToGitByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrlV2 + name + "/dump_to_bitbucket/")
                .compareWithJsonSchema("jsonSchema/gitlab/dumpToGitLabSchema.json")
                .assertStatus(201);
    }

    @Step("Выгрузка продукта из Gitlab")
    public static void loadProductFromBitbucket(JSONObject body) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .body(body)
                .post(productUrl + "load_from_bitbucket/")
                .assertStatus(200);
    }

    @Step("Копирование продукта по Id")
    public static void copyProductById(String objectId) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrl + objectId + "/copy/")
                .assertStatus(200);
    }

    @Step("Копирование продукта по имени {name}")
    public static Product copyProductByName(String name) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .post(productUrlV2 + name + "/copy/")
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Копирование продукта по Id без ключа")
    public static Response copyProductByIdWithOutToken(String objectId) {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .setWithoutToken()
                .post(productUrl + objectId + "/copy/")
                .assertStatus(401);
    }

    @Step("Удаление продукта по имени {name}")
    public static void deleteProductByName(String name) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .delete(productUrlV2 + name + "/")
                .assertStatus(204);
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

    @Step("Удаление продукта по Id без токена")
    public static Response deleteProductByIdWithOutToken(String id) {
        return new Http(ProductCatalogURL)
                .setWithoutToken()
                .delete(productUrl + id + "/").assertStatus(401);
    }
}
