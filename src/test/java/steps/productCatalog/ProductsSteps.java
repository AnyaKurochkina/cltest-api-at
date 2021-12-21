package steps.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.OrgDirection.existsOrgDirection.response.ExistsOrgDirectionResponse;
import httpModels.productCatalog.Product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.Product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.Product.getProducts.response.ListItem;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class ProductsSteps {

    @SneakyThrows
    @Step("Получение ID продукта по его имени: {actionName}")
    public String getProductId(String productName) {
        String productsId = null;
        for (ListItem listItem : getProductList()) {
            if (listItem.getName().equals(productName)) {
                productsId = listItem.getId();
                break;
            }
        }
        return productsId;
    }

    @SneakyThrows
    @Step("Создание продукта")
    public Http.Response createProduct(JSONObject body) {
        return new Http(Configure.ProductCatalogURL)
                .body(body)
                .post("products/");
    }

    @Step("Получение списка продуктов")
    public List<ListItem> getProductList() {
        return new Http(Configure.ProductCatalogURL)
                .get("products/")
                .assertStatus(200)
                .extractAs(GetProductsResponse.class)
                .getList();
    }

    @Step("Проверка существования действия по имени")
    public boolean isProductExist(String name) {
        return new Http(Configure.ProductCatalogURL)
                .get("/products/exists/?name=" + name)
                .assertStatus(200)
                .extractAs(ExistsOrgDirectionResponse.class)
                .getExists();
    }

    @Step("Ипорт продукта")
    public void importProduct(String pathName) {
        new Http(Configure.ProductCatalogURL)
                .multiPart("products/obj_import/", "file", new File(pathName))
                .assertStatus(200);
    }

    @Step("Создание JSON объекта по продуктам")
    public JSONObject createJsonObject(String name) {
        return JsonHelper
                .getJsonTemplate("productCatalog/products/createProduct.json")
                .set("$.name", name)
                .build();
    }

    @Step("Получение продукта по Id")
    public GetProductResponse getProductById(String id) {
        return new Http(Configure.ProductCatalogURL)
                .get("products/" + id + "/")
                .assertStatus(200)
                .extractAs(GetProductResponse.class);
    }

    @Step("Частичное обновление продукта")
    public Http.Response partialUpdateProduct(String id, JSONObject object) {
        return new Http(Configure.ProductCatalogURL)
                .body(object)
                .patch("products/" + id + "/");
    }

    public void deleteProductByName(String actionName) {
        deleteProductById(getProductId(actionName));
    }

    public void deleteProductById(String productId) {
        new Http(Configure.ProductCatalogURL)
                .delete("products/" + productId + "/")
                .assertStatus(204);
    }

    @SneakyThrows
    @Step("Копирование действия по Id")
    public void copyProductById(String id) {
        new Http(Configure.ProductCatalogURL)
                .setContentType("application/json")
                .post("products/" + id + "/copy/")
                .assertStatus(200);
    }
}
