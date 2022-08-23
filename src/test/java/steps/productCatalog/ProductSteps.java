package steps.productCatalog;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.productCatalog.product.GetProductList;
import models.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.ProductCatalogURL;
import static steps.productCatalog.ProductCatalogSteps.delNoDigOrLet;

public class ProductSteps extends Steps {
    private static final String productUrl = "/api/v1/products/";

    @Step("Получение списка Продуктов продуктового каталога")
    public static List<Product> getProductList() {
        return new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .get("/api/v1/products/")
                .compareWithJsonSchema("jsonSchema/getProductListSchema.json")
                .assertStatus(200)
                .extractAs(GetProductList.class).getList();
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
            if(!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
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
            if(!(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime))) {
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
                .put(productUrl+ id + "/")
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
    public static void importProduct(String pathName) {
        new Http(ProductCatalogURL)
                .setRole(Role.PRODUCT_CATALOG_ADMIN)
                .multiPart(productUrl + "obj_import/", "file", new File(pathName))
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
            Product item =  list.get(i);
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
}
