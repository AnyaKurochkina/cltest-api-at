package tests.productCatalog;

import core.helper.Deleted;
import io.qameta.allure.Feature;
import models.productCatalog.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: продукты")
public class ProductsTest extends Tests {

    Product product;

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
