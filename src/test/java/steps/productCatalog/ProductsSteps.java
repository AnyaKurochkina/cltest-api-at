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

import static io.restassured.RestAssured.given;

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
        given()
                .contentType("multipart/form-data")
                .multiPart("file", new File(pathName))
                .when()
                .post("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/product-catalog/products/obj_import/")
                .then()
                .statusCode(200);
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
    public void partialUpdateProduct(String id, String key, String value) {
        new Http(Configure.ProductCatalogURL)
                .body(new JSONObject().put(key, value))
                .patch("products/" + id + "/")
                .assertStatus(200);
    }

    public void deleteProductByName(String actionName) {
        deleteProductById(getProductId(actionName));
    }

    public void deleteProductById(String productId) {
        new Http(Configure.ProductCatalogURL)
                
                .delete("products/" + productId + "/")
                .assertStatus(204);
    }
}
