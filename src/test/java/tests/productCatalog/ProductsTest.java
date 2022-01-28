package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.product.existProduct.response.ExistProductResponse;
import httpModels.productCatalog.product.getProduct.response.GetServiceResponce;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, product.getProductId(), GetServiceResponce.class);
        Assertions.assertEquals(productCatalogGet.getName(), product.getName());
    }

    @Order(6)
    @DisplayName("Негатичный тест на получение продукта по Id без токена")
    @Test
    public void getProductByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, product.getProductId(), GetServiceResponce.class);
    }

    @Order(20)
    @DisplayName("Частичное обновление продукта")
    @Test
    public void partialUpdateProduct() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(productName, product.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        String actual = productCatalogSteps.getById(productName, product.getProductId(), GetServiceResponce.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(21)
    @DisplayName("Негативный тест на обновление продукта по Id без токена")
    @Test
    public void updateProductByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(productName, product.getProductId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(30)
    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        String currentVersion = product.getVersion();
        productCatalogSteps.partialUpdateObject(productName, product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(40)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, product.getProductId(), GetServiceResponce.class);
        Assertions.assertNotNull(productCatalogGet.getGraphVersionCalculated());
    }

    @Order(50)
    @DisplayName("Копирование продукта по Id")
    @Test
    public void copyProductById() {
        String cloneName = product.getName() + "-clone";
        productCatalogSteps.copyById(productName, product.getProductId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistProductResponse.class));
        productCatalogSteps.deleteByName(productName, cloneName, GetProductsResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistProductResponse.class));
    }

    @Order(51)
    @DisplayName("Негатичный тест на копирование продукта по Id без токена")
    @Test
    public void copyProductByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(productName, product.getProductId());
    }

    @Order(60)
    @DisplayName("Обновление продукта")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(70)
    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @Test
    public void createProductWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject(product.getName(), "productCatalog/products/createProduct.json"))
                .assertStatus(400);
    }

    @Order(80)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @Test
    public void createProductWithInvalidCharacters() {
        assertAll("Продукт создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("NameWithUppercase", "productCatalog/products/createProduct.json")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle", "productCatalog/products/createProduct.json")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("имя", "productCatalog/products/createProduct.json")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("Имя", "productCatalog/products/createProduct.json")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("a&b&c", "productCatalog/products/createProduct.json")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("", "productCatalog/products/createProduct.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject(" ", "productCatalog/products/createProduct.json"))
                        .assertStatus(400)
        );
    }

    @Order(89)
    @DisplayName("Обновление продукта с указанием версии в граничных значениях")
    @Test
    public void updateProductAndGetVersion() {
        Product productTest = Product.builder().name("product_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(productName, productTest.getProductId(), new JSONObject().put("name", "product_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(productName, productTest.getProductId(), GetServiceResponce.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productName, productTest.getProductId(), new JSONObject().put("name", "product_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(productName, productTest.getProductId(), new JSONObject().put("name", "product_version_test_api4"));
        currentVersion = productCatalogSteps.getById(productName, productTest.getProductId(), GetServiceResponce.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productName, productTest.getProductId(), new JSONObject().put("name", "product_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(productName, productTest.getProductId(), new JSONObject().put("name", "product_version_test_api6"))
                .assertStatus(500);
    }

    @Order(90)
    @DisplayName("Получение время отклика на запрос")
    @Test
    public void getTime() {
        Assertions.assertTrue(2500 < productCatalogSteps.getTime("http://d4-product-catalog.apps" +
                ".d0-oscp.corp.dev.vtb/products/?is_open=true&env=dev&information_systems=c9fd31c7-25a5-45ca-863c-18425d1ae927&page=1&per_page=100"));
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление продукта без токена")
    @Test
    public void deleteProductWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(productName, product.getProductId());
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
