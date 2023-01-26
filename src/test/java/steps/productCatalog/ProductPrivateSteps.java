package steps.productCatalog;

import core.helper.http.Http;
import core.helper.http.Response;
import io.qameta.allure.Step;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class ProductPrivateSteps extends Steps {

    private static final String adminUrl = "/private/api/v1/products/";
    private static final String adminUrlV2 = "/private/api/v2/products/";

    @Step("Получение действия по Id")
    public static Product getProductPrivateById(String objectId) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrl + objectId + "/")
                .extractAs(Product.class);
    }

    @Step("Получение действия по имени {name}")
    public static Product getProductPrivateByName(String name) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .get(adminUrlV2 + name + "/")
                .extractAs(Product.class);
    }

    @Step("Создание действия")
    public static Product createProductPrivate(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrl)
                .assertStatus(201)
                .extractAs(Product.class);
    }

    @Step("Создание действия")
    public static Product createProductPrivateV2(JSONObject body) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(body)
                .post(adminUrlV2)
                .assertStatus(201)
                .extractAs(Product.class);
    }

    @Step("Удаление действия по id")
    public static void deleteProductPrivateById(String id) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrl + id + "/")
                .assertStatus(204);
    }

    @Step("Удаление действия по имени {name}")
    public static void deleteProductPrivateByName(String name) {
        new Http(ProductCatalogURL)
                .withServiceToken()
                .delete(adminUrlV2 + name + "/")
                .assertStatus(204);
    }

    @Step("Копирование действия по Id")
    public static Product copyProductPrivateById(String objectId) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .post(adminUrl + objectId + "/copy/")
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Копирование действия по имени {name}")
    public static Product copyProductPrivateByName(String name) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .post(adminUrlV2 + name + "/copy/")
                .assertStatus(200)
                .extractAs(Product.class);
    }

    @Step("Частичное обновление действия")
    public static Response partialUpdatePrivateProduct(String id, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrl + id + "/");
    }

    @Step("Частичное обновление действия по имени {name}")
    public static Response partialUpdateProductPrivateByName(String name, JSONObject object) {
        return new Http(ProductCatalogURL)
                .withServiceToken()
                .body(object)
                .patch(adminUrlV2 + name + "/");
    }
}
