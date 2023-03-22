package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.product.ProductOrderRestriction;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv({"prod", "t1dev", "t1ift", "t1prod"})
public class ProductOrderRestrictionTest extends Tests {

    @DisplayName("Создание/Получение/Обновление/Удаление order_restriction по id")
    @TmsLink("")
    @Test
    public void productOrderRestrictionTest() {
        Product product = createProductByName("create_product_order_restriction_test_api");
        ProductOrderRestriction orderRestriction = ProductOrderRestriction.builder()
                .productName(product.getName())
                .domains(Collections.emptyList())
                .dataCenters(Collections.emptyList())
                .isDeleted(false)
                .informSystemIds(Collections.emptyList())
                .netSegments(Collections.emptyList())
                .organization("vtb")
                .isBlocking(false)
                .environments(Collections.emptyList())
                .weight(55)
                .platforms(Collections.singletonList("vsphere"))
                .build();
        String productId = product.getProductId();
        ProductOrderRestriction createdOrderRestriction = createProductOrderRestrictionById(productId, orderRestriction.toJson())
                .extractAs(ProductOrderRestriction.class);
        List<ProductOrderRestriction> list = getProductOrderRestrictionById(productId).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertEquals(1, list.size());
        assertEquals(orderRestriction, list.get(0));
        assertEquals(productId, createdOrderRestriction.getProductId());
        createdOrderRestriction.setEnvironments(Arrays.asList("DEV", "LT"));
        createdOrderRestriction.setWeight(54);
        ProductOrderRestriction updatedRestriction = updateProductOrderRestrictionById(productId, createdOrderRestriction.getId(), createdOrderRestriction.toJson())
                .extractAs(ProductOrderRestriction.class);
        assertEquals(createdOrderRestriction.getEnvironments(), updatedRestriction.getEnvironments());
        assertEquals(createdOrderRestriction.getWeight(), updatedRestriction.getWeight());
        deleteProductOrderRestrictionById(productId, createdOrderRestriction.getId());
        List<ProductOrderRestriction> listAfterDeleted = getProductOrderRestrictionById(productId).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertTrue(listAfterDeleted.isEmpty());
    }

    @DisplayName("Создание/Получение/Обновление/Удаление order_restriction по имени")
    @TmsLink("")
    @Test
    public void productOrderRestrictionByNameTest() {
        Product product = createProductByName("create_product_order_restriction_by_name_test_api");
        String productName = product.getName();
        ProductOrderRestriction orderRestriction = ProductOrderRestriction.builder()
                .productName(productName)
                .domains(Collections.emptyList())
                .dataCenters(Collections.emptyList())
                .isDeleted(false)
                .informSystemIds(Collections.emptyList())
                .netSegments(Collections.emptyList())
                .organization("vtb")
                .isBlocking(false)
                .environments(Collections.emptyList())
                .weight(49)
                .platforms(Collections.singletonList("vsphere"))
                .build();
        ProductOrderRestriction createdOrderRestriction = createProductOrderRestrictionByName(productName, orderRestriction.toJson())
                .extractAs(ProductOrderRestriction.class);
        List<ProductOrderRestriction> list = getProductOrderRestrictionByName(productName).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertEquals(1, list.size());
        assertEquals(orderRestriction, list.get(0));
        assertEquals(product.getProductId(), createdOrderRestriction.getProductId());
        createdOrderRestriction.setEnvironments(Arrays.asList("DEV", "LT"));
        createdOrderRestriction.setWeight(48);
        ProductOrderRestriction updatedRestriction = updateProductOrderRestrictionByName(productName, createdOrderRestriction.getId(), createdOrderRestriction.toJson())
                .extractAs(ProductOrderRestriction.class);
        assertEquals(createdOrderRestriction.getEnvironments(), updatedRestriction.getEnvironments());
        assertEquals(createdOrderRestriction.getWeight(), updatedRestriction.getWeight());
        deleteProductOrderRestrictionByName(productName, createdOrderRestriction.getId());
        List<ProductOrderRestriction> listAfterDeleted = getProductOrderRestrictionByName(productName).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertTrue(listAfterDeleted.isEmpty());
    }

    @DisplayName("Создание ограничения с уже занятым weight")
    @TmsLink("")
    @Test
    public void createProductOrderRestrictionWithUsedWeight() {
        Product product = createProductByName(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_test_api");
        ProductOrderRestriction orderRestriction = ProductOrderRestriction.builder()
                .productName(product.getName())
                .domains(Collections.emptyList())
                .dataCenters(Collections.emptyList())
                .isDeleted(false)
                .informSystemIds(Collections.emptyList())
                .netSegments(Collections.emptyList())
                .organization("vtb")
                .isBlocking(false)
                .environments(Collections.emptyList())
                .weight(40)
                .platforms(Collections.singletonList("vsphere"))
                .build();
        ProductOrderRestriction createdOrderRestriction = createProductOrderRestrictionById(product.getProductId(), orderRestriction.toJson())
                .extractAs(ProductOrderRestriction.class);
        createProductOrderRestrictionById(product.getProductId(), orderRestriction.toJson());
        deleteProductOrderRestrictionById(product.getProductId(), createdOrderRestriction.getId());
    }
}
