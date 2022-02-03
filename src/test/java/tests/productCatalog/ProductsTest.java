package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.product.existProduct.response.ExistProductResponse;
import httpModels.productCatalog.product.getProduct.response.GetServiceResponce;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
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
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
public class ProductsTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("products/", "productCatalog/products/createProduct.json");
    Product product;

    @Order(1)
    @DisplayName("Создание продукта в продуктовом каталоге")
    @TmsLink("643375")
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
    @TmsLink("643387")
    @Test
    public void getProductList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(GetProductsResponse.class)
                .size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @TmsLink("643392")
    @Test
    public void checkProductExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(product.getName(), ExistProductResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists("not_exists_name", ExistProductResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт продукта")
    @TmsLink("643393")
    @Test
    public void importProduct() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String name = new JsonPath(data).get("Product.json.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        Assertions.assertTrue(productCatalogSteps.isExists(name, ExistProductResponse.class));
        productCatalogSteps.deleteByName(name, GetProductsResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(name, ExistProductResponse.class));
    }

    @Order(5)
    @DisplayName("Получение продукта по Id")
    @TmsLink("643395")
    @Test
    public void getProductById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(product.getProductId(), GetServiceResponce.class);
        Assertions.assertEquals(productCatalogGet.getName(), product.getName());
    }

    @Order(6)
    @DisplayName("Негативный тест на получение продукта по Id без токена")
    @TmsLink("643397")
    @Test
    public void getProductByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(product.getProductId());
    }

    @Order(20)
    @DisplayName("Частичное обновление продукта")
    @TmsLink("643402")
    @Test
    public void partialUpdateProduct() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        String actual = productCatalogSteps.getById(product.getProductId(), GetServiceResponce.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(21)
    @DisplayName("Негативный тест на обновление продукта по Id без токена")
    @TmsLink("643407")
    @Test
    public void updateProductByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(product.getProductId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(30)
    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @TmsLink("643409")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        String currentVersion = product.getVersion();
        productCatalogSteps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(40)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @TmsLink("643412")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl productCatalogGet = productCatalogSteps.getById(product.getProductId(), GetServiceResponce.class);
        Assertions.assertNotNull(productCatalogGet.getGraphVersionCalculated());
    }

    @Order(50)
    @DisplayName("Копирование продукта по Id")
    @TmsLink("643414")
    @Test
    public void copyProductById() {
        String cloneName = product.getName() + "-clone";
        productCatalogSteps.copyById(product.getProductId());
        Assertions.assertTrue(productCatalogSteps.isExists(cloneName, ExistProductResponse.class));
        productCatalogSteps.deleteByName(cloneName, GetProductsResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(cloneName, ExistProductResponse.class));
    }

    @Order(51)
    @DisplayName("Негативный тест на копирование продукта по Id без токена")
    @TmsLink("643416")
    @Test
    public void copyProductByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(product.getProductId());
    }

    @Order(60)
    @DisplayName("Обновление продукта")
    @TmsLink("643418")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(70)
    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @TmsLink("643420")
    @Test
    public void createProductWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject(product.getName()))
                .assertStatus(400);
    }

    @Order(80)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @TmsLink("643423")
    @Test
    public void createProductWithInvalidCharacters() {
        assertAll("Продукт создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("a&b&c"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(""))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(" "))
                        .assertStatus(400)
        );
    }

    @Order(89)
    @DisplayName("Обновление продукта с указанием версии в граничных значениях")
    @TmsLink("643426")
    @Test
    public void updateProductAndGetVersion() {
        Product productTest = Product.builder().name("product_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(productTest.getProductId(), GetServiceResponce.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api4"));
        currentVersion = productCatalogSteps.getById(productTest.getProductId(), GetServiceResponce.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api6"))
                .assertStatus(500);
    }

    @Order(90)
    @DisplayName("Получение время отклика на запрос")
    @TmsLink("643431")
    @Test
    public void getTime() {
        Assertions.assertTrue(2500 < productCatalogSteps.getTime("http://d4-product-catalog.apps" +
                ".d0-oscp.corp.dev.vtb/products/?is_open=true&env=dev&information_systems=c9fd31c7-25a5-45ca-863c-18425d1ae927&page=1&per_page=100"));
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление продукта без токена")
    @TmsLink("643433")
    @Test
    public void deleteProductWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(product.getProductId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление продукта")
    @TmsLink("643434")
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
