package api.cloud.productCatalog.product;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
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
@DisabledIfEnv("prod")
public class ProductTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в продуктах")
    @TmsLinks({@TmsLink("1701022"), @TmsLink("1701025")})
    @Test
    public void addTagProductTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Product product1 = ProductSteps.createProduct("add_tag1_test_api");
        Product product2 = ProductSteps.createProduct("add_tag2_test_api");
        addTagListToProduct(tagList, product1.getName(), product2.getName());
        assertEquals(tagList, getProductById(product1.getProductId()).getTagList());
        assertEquals(tagList, getProductById(product2.getProductId()).getTagList());
        removeTagListToProduct(tagList, product1.getName(), product2.getName());
        assertTrue(getProductById(product1.getProductId()).getTagList().isEmpty());
        assertTrue(getProductById(product2.getProductId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list")
    @TmsLink("1676200")
    @Test
    public void checkTagListValue() {
        List<String> tagList = Arrays.asList("product_tag_test_value", "product_tag_test_value2");
        Product product = Product.builder()
                .name("at_api_check_tag_list_value")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        Product createdProduct = getProductById(product.getProductId());
        AssertUtils.assertEqualsList(tagList, createdProduct.getTagList());
        tagList = Collections.singletonList("product_tag_test_value3");
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject().put("tag_list", tagList));
        createdProduct = getProductById(product.getProductId());
        AssertUtils.assertEqualsList(tagList, createdProduct.getTagList());
    }

    @DisplayName("Проверка неверсионности поля tag_list")
    @TmsLink("1676202")
    @Test
    public void checkTagListVersioning() {
        List<String> tagList = Arrays.asList("product_tag_test_value", "product_tag_test_value2");
        Product product = Product.builder()
                .name("at_api_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        Product createdProduct = getProductById(product.getProductId());
        tagList = Collections.singletonList("product_tag_test_value3");
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject().put("tag_list", tagList));
        createdProduct = getProductById(product.getProductId());
        assertEquals("1.0.0", createdProduct.getVersion());
    }
}

