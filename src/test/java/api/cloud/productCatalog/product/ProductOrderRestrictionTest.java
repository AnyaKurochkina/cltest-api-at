package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.product.ProductOrderRestriction;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;

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
    @TmsLinks({@TmsLink("1536988"), @TmsLink("1536992"), @TmsLink("1536995"), @TmsLink("1536997")})
    @Test
    public void productOrderRestrictionTest() {
        Product product = ProductSteps.createProduct("create_product_order_restriction_test_api");
        ProductOrderRestriction orderRestriction = ProductOrderRestriction.builder()
                .productName(product.getName())
                .isDeleted(false)
                .netSegments(Collections.singletonList("dev-srv-app"))
                .domains(Collections.singletonList("corp.dev.vtb"))
                .dataCenters(Collections.singletonList("5"))
                .informSystemIds(Collections.emptyList())
                .organization("vtb")
                .isBlocking(false)
                .environments(Collections.emptyList())
                .weight(55)
                .platforms(Collections.singletonList("vsphere"))
                .build();
        String productId = product.getProductId();
        ProductOrderRestriction createdOrderRestriction = createProductOrderRestrictionById(productId, orderRestriction.toJson())
                .compareWithJsonSchema("jsonSchema/createProductOrderRestriction.json")
                .assertStatus(200)
                .extractAs(ProductOrderRestriction.class);
        assertEquals(product.getName(), createdOrderRestriction.getProductName());
        assertEquals(productId, createdOrderRestriction.getProductId());
        List<ProductOrderRestriction> list = getProductOrderRestrictionById(productId).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertEquals(1, list.size());
        assertEquals(createdOrderRestriction, list.get(0));
        createdOrderRestriction.setEnvironments(Arrays.asList("DEV", "LT"));
        createdOrderRestriction.setWeight(54);
        ProductOrderRestriction updatedRestriction = updateProductOrderRestrictionById(productId, createdOrderRestriction.getId(),
                createdOrderRestriction.toJson());
        assertEquals(createdOrderRestriction, updatedRestriction);
        deleteProductOrderRestrictionById(productId, createdOrderRestriction.getId());
        List<ProductOrderRestriction> listAfterDeleted = getProductOrderRestrictionById(productId).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertTrue(listAfterDeleted.isEmpty());
    }

    @DisplayName("Создание/Получение/Обновление/Удаление order_restriction по имени")
    @TmsLinks({@TmsLink("1536999"), @TmsLink("1537001"), @TmsLink("1537004"), @TmsLink("1537008")})
    @Test
    public void productOrderRestrictionByNameTest() {
        //TODO добавят валидацию при создании
        Product product = ProductSteps.createProduct("create_product_order_restriction_by_name_test_api");
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
                .compareWithJsonSchema("jsonSchema/createProductOrderRestriction.json")
                .assertStatus(200)
                .extractAs(ProductOrderRestriction.class);
        assertEquals(product.getName(), createdOrderRestriction.getProductName());
        assertEquals(product.getProductId(), createdOrderRestriction.getProductId());
        List<ProductOrderRestriction> list = getProductOrderRestrictionByName(productName).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertEquals(1, list.size());
        assertEquals(createdOrderRestriction, list.get(0));
        createdOrderRestriction.setEnvironments(Arrays.asList("DEV", "LT"));
        createdOrderRestriction.setWeight(48);
        createdOrderRestriction.setDomains(Collections.singletonList("corp.dev.vtb"));
        createdOrderRestriction.setNetSegments(Collections.singletonList("dev-srv-app"));
        createdOrderRestriction.setDataCenters(Collections.singletonList("5"));
        ProductOrderRestriction updatedRestriction = updateProductOrderRestrictionByName(productName, createdOrderRestriction.getId(),
                createdOrderRestriction.toJson());
        assertEquals(createdOrderRestriction, updatedRestriction);
        deleteProductOrderRestrictionByName(productName, createdOrderRestriction.getId());
        List<ProductOrderRestriction> listAfterDeleted = getProductOrderRestrictionByName(productName).jsonPath()
                .getList("list", ProductOrderRestriction.class);
        assertTrue(listAfterDeleted.isEmpty());
    }

    @DisplayName("Создание ограничения с уже занятым weight")
    @TmsLink("1537013")
    @Test
    public void createProductOrderRestrictionWithUsedWeight() {
        Product product = ProductSteps.createProduct(RandomStringUtils.randomAlphabetic(8).toLowerCase() + "_test_api");
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
        String error = createProductOrderRestrictionById(product.getProductId(), orderRestriction.toJson())
                .assertStatus(422)
                .jsonPath().getString("error.message");
        assertTrue(error.contains("В рамках одной организации не должно быть ограничений одинакового веса."));
        deleteProductOrderRestrictionById(product.getProductId(), createdOrderRestriction.getId());
    }
}
