package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Product.getProduct.response.GetProductResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Product;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductsSteps;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: продукты")
public class ProductsTest extends Tests {

    Product product;
    ProductsSteps productsSteps = new ProductsSteps();

    @Order(1)
    @DisplayName("Создание продукта в продуктовом каталоге")
    @Test
    public void createProduct() {
        product = Product.builder()
                .productName("at_test_api_product55")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка продуктов")
    @Test
    public void getProductList() {
        Assertions.assertTrue(productsSteps.getProductList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @Test
    public void checkProductExists() {
        Assertions.assertTrue(productsSteps.isProductExist(product.getProductName()));
        Assertions.assertFalse(productsSteps.isProductExist("not_exists_name"));
    }

    @Order(4)
    @DisplayName("Импорт продукта")
    @Test
    public void importProduct() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String actionName = new JsonPath(data).get("Product.json.name");
        productsSteps.importProduct(Configure.RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        Assertions.assertTrue(productsSteps.isProductExist(actionName));
        productsSteps.deleteProductByName(actionName);
        Assertions.assertFalse(productsSteps.isProductExist(actionName));
    }

    @Order(5)
    @DisplayName("Получение продукта по Id")
    @Test
    public void getProductById() {
        GetProductResponse response = productsSteps.getProductById(product.getProductId());
        Assertions.assertEquals(response.getName(), product.getProductName());
    }

    @Order(6)
    @DisplayName("Частичное обновление продукта")
    @Test
    public void partialUpdateProduct() {
        String expectedValue = "UpdateDescription";
        productsSteps.partialUpdateProduct(product.getProductId(), "description", expectedValue);
        String actual = productsSteps.getProductById(product.getProductId()).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(7)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetProductResponse getProductResponse = productsSteps.getProductById(product.getProductId());
        Assertions.assertNotNull(getProductResponse.getGraphVersionCalculated());
    }

    @Order(8)
    @DisplayName("Копирование продукта по Id")
    @Test
    public void copyProductById() {
        String cloneName = product.getProductName() + "-clone";
        productsSteps.copyProductById(product.getProductId());
        Assertions.assertTrue(productsSteps.isProductExist(cloneName));
        productsSteps.deleteProductByName(cloneName);
        Assertions.assertFalse(productsSteps.isProductExist(cloneName));
    }

    @Order(10)
    @DisplayName("Обновление продукта")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(12)
    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @Test
    public void createProductWithSameName() {
        productsSteps.createProduct(productsSteps.createJsonObject(product.getProductName())).assertStatus(400);
    }

    @Order(13)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени.")
    @Test
    public void createProductWithInvalidCharacters() {
        assertAll(
                () -> productsSteps.createProduct(productsSteps.createJsonObject("NameWithUppercase")).assertStatus(400),
                () -> productsSteps.createProduct(productsSteps.createJsonObject("nameWithUppercaseInMiddle")).assertStatus(400),
                () -> productsSteps.createProduct(productsSteps.createJsonObject("имя")).assertStatus(400),
                () -> productsSteps.createProduct(productsSteps.createJsonObject("Имя")).assertStatus(400),
                () -> productsSteps.createProduct(productsSteps.createJsonObject("a&b&c")).assertStatus(400)
        );
    }

    @Order(100)
    @Test
    @DisplayName("Удаление продукта")
    @MarkDelete
    public void deleteProduct() {
        try (Product product = Product.builder()
                .productName("at_test_api_product55")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .build()
                .createObjectExclusiveAccess()) {
            product.deleteObject();
        }
    }
}
