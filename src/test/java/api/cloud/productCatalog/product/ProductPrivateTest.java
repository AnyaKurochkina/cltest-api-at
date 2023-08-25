package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductPrivateSteps.*;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductPrivateTest extends Tests {

    @DisplayName("Создание/Получение/Удаление продукта в продуктовом каталоге c сервисным токеном")
    @TmsLinks({@TmsLink("1420357"), @TmsLink("1420358"), @TmsLink("1420359")})
    @Test
    public void productPrivateByIdTest() {
        String productName = "product_private_test_api";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        JSONObject jsonObject = Product.builder()
                .name(productName)
                .graphId(createGraph(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getGraphId())
                .build()
                .toJson();
        Product product = createProductPrivate(jsonObject);
        String productId = product.getProductId();
        Product actualProduct = getProductPrivateById(productId);
        assertEquals(product, actualProduct);
        deleteProductPrivateById(productId);
    }

    @DisplayName("Обновление продукта c сервисным токеном")
    @TmsLink("1420373")
    @Test
    public void updateProductPrivateTest() {
        String productName = "product_update_private_test_api";
        Product product = ProductSteps.createProduct(productName);
        partialUpdatePrivateProduct(product.getProductId(), new JSONObject().put("max_count", 2));
        Product updatedProduct = getProductById(product.getProductId());
        assertEquals("1.0.1", updatedProduct.getVersion(), "Версии не совпадают");
        assertEquals(2, updatedProduct.getMaxCount());
    }

    @DisplayName("Создание/Получение/Удаление продукта в продуктовом каталоге c сервисным токеном api/v2")
    @TmsLinks({@TmsLink("1420375"), @TmsLink("1420378"), @TmsLink("1420380")})
    @Test
    public void productPrivateByNameTest() {
        String productName = "product_private_v2_test_api";
        if (isProductExists(productName)) {
            deleteProductByName(productName);
        }
        JSONObject jsonObject = Product.builder()
                .name(productName)
                .graphId(createGraph(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getGraphId())
                .build()
                .toJson();
        Product product = createProductPrivateV2(jsonObject);
        Product actualProduct = getProductPrivateByName(productName);
        assertEquals(product, actualProduct);
        deleteProductPrivateByName(productName);
    }

    @DisplayName("Обновление продукта c сервисным токеном api/v2")
    @TmsLink("1420383")
    @Test
    public void updateProductPrivateByNameTest() {
        String productName = "product_update_private_by_name_test_api";
        ProductSteps.createProduct(productName);
        partialUpdateProductPrivateByName(productName, new JSONObject().put("max_count", 2));
        Product updatedProduct = getProductPrivateByName(productName);
        assertEquals("1.0.1", updatedProduct.getVersion(), "Версии не совпадают");
        assertEquals(2, updatedProduct.getMaxCount());
    }
}
