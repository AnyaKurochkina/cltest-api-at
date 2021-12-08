package tests.productCatalog;

import core.helper.Deleted;
import httpModels.productCatalog.Product.getProduct.response.GetProductResponse;
import io.qameta.allure.Feature;
import models.productCatalog.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductsSteps;
import tests.Tests;

import java.util.Collections;

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
                .productName("AtTestApiProduct")
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
    @DisplayName("Проверка существования продукта с таким именем")
    @Test
    public void checkProductExists() {
        Assertions.assertTrue(productsSteps.isProductExist(product.getProductName()));
        Assertions.assertFalse(productsSteps.isProductExist("NotExistName"));
    }

    @Order(4)
    @DisplayName("Импорт пролукта")
    @Test
    public void importProduct() {
        String name = "ImportProduct";
        JSONObject jsonObject = productsSteps.createJsonObject(name);
        productsSteps.importProduct(jsonObject);
        Assertions.assertTrue(productsSteps.isProductExist(name));
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

    @Order(10)
    @DisplayName("Обновление продукта")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(100)
    @Test
    @DisplayName("Удаление продукта")
    @Deleted
    public void deleteAction() {
        try (Product product = Product.builder()
                .productName("AtTestApiProduct")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .build()
                .createObjectExclusiveAccess()) {
            product.deleteObject();
        }
    }
}
