package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Product.existProduct.response.ExistProductResponse;
import httpModels.productCatalog.Product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.Product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.GetImpl;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: продукты")
public class ProductsTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    Product product;
    private final String productName = "products/";

    @Order(1)
    @DisplayName("Создание продукта в продуктовом каталоге")
    @Test
    public void createProduct() {
        product = Product.builder()
                .name("at_test_api_product55")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка продуктов")
    @Test
    public void getProductList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(productName, GetProductsResponse.class)
                .size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @Test
    public void checkProductExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(productName, product.getName(), ExistProductResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists(productName, "not_exists_name", ExistProductResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт продукта")
    @Test
    public void importProduct() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String name = new JsonPath(data).get("Product.json.name");
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        Assertions.assertTrue(productCatalogSteps.isExists(productName, name, ExistProductResponse.class));
        productCatalogSteps.deleteByName(productName, name, GetProductsResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, name, ExistProductResponse.class));
    }

    @Order(5)
    @DisplayName("Получение продукта по Id")
    @Test
    public void getProductById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, product.getProductId(), GetProductResponse.class);
        Assertions.assertEquals(productCatalogGet.getName(), product.getName());
    }

    @Order(6)
    @DisplayName("Частичное обновление продукта")
    @Test
    public void partialUpdateProduct() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(productName, product.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        String actual = productCatalogSteps.getById(productName, product.getProductId(), GetProductResponse.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(7)
    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        String currentVersion = product.getVersion();
        productCatalogSteps.partialUpdateObject(productName, product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(8)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, product.getProductId(), GetProductResponse.class);
        Assertions.assertNotNull(productCatalogGet.getGraphVersionCalculated());
    }

    @Order(9)
    @DisplayName("Копирование продукта по Id")
    @Test
    public void copyProductById() {
        String cloneName = product.getName() + "-clone";
        productCatalogSteps.copyById(productName, product.getProductId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistProductResponse.class));
        productCatalogSteps.deleteByName(productName, cloneName, GetProductsResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistProductResponse.class));
    }

    @Order(10)
    @DisplayName("Обновление продукта")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(11)
    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @Test
    public void createProductWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject(product.getName(), "productCatalog/products/createProduct.json"))
                .assertStatus(400);
    }

    @Order(13)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @Test
    public void createProductWithInvalidCharacters() {
        assertAll("Продукт создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("NameWithUppercase", "productCatalog/products/createProduct.json")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle", "productCatalog/products/createProduct.json")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("имя", "productCatalog/products/createProduct.json")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("Имя", "productCatalog/products/createProduct.json")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("a&b&c", "productCatalog/products/createProduct.json")).assertStatus(400)
        );
    }

    @Order(14)
    @DisplayName("Получение время отклика на запрос")
    @Test
    public void getTime() {
        Assertions.assertTrue(2500 < productCatalogSteps.getTime("http://d4-product-catalog.apps" +
                ".d0-oscp.corp.dev.vtb/products/?is_open=true&env=dev&information_systems=c9fd31c7-25a5-45ca-863c-18425d1ae927&page=1&per_page=100"));
    }

    @Order(100)
    @Test
    @DisplayName("Удаление продукта")
    @MarkDelete
    public void deleteProduct() {
        try (Product product = Product.builder()
                .name("at_test_api_product55")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .build()
                .createObjectExclusiveAccess()) {
            product.deleteObject();
        }
    }
}
