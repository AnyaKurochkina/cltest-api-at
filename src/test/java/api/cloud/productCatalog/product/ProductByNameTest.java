package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductByNameTest extends Tests {

    @DisplayName("Получение продукта по имени")
    @TmsLink("1361362")
    @Test
    public void getProductByNameTest() {
        String productName = "get_product_by_name_example_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .build()
                .createObject();
        Product getProduct = getProductByName(productName);
        assertEquals(product, getProduct);
    }

    @DisplayName("Обновление продукта по имени")
    @TmsLink("1361363")
    @Test
    public void patchProductByNameTest() {
        String productName = "product_patch_by_name_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .maxCount(1)
                .build()
                .createObject();
        partialUpdateProductByName(productName, new JSONObject().put("max_count", 2));
        assertEquals("1.0.1", getProductById(product.getProductId()).getVersion(), "Версии не совпадают");
    }

    @Test
    @DisplayName("Удаление продукта по имени")
    @TmsLink("1361365")
    public void deleteProductByNameTest() {
        String product = "product_delete_by_name_test_api";
        JSONObject jsonObject = Product.builder()
                .name(product)
                .title(product)
                .build()
                .init()
                .toJson();
        createProduct(jsonObject);
        deleteProductByName(product);
        assertFalse(isProductExists(product));
    }

    @DisplayName("Копирование продукта по имени")
    @TmsLink("1361367")
    @Test
    public void copyProductByNameTest() {
        String productName = "clone_product_by_name_test_api";
        Product.builder()
                .name(productName)
                .title(productName)
                .build()
                .createObject();
        Product cloneProduct = copyProductByName(productName);
        String cloneName = cloneProduct.getName();
        assertTrue(isProductExists(cloneName), "Продукт не существует");
        deleteProductByName(cloneName);
        assertFalse(isProductExists(cloneName), "Продукт существует");
    }

    @DisplayName("Проверка tag_list при копировании продукта V2")
    @TmsLink("")
    @Test
    public void copyProductAndCheckTagListV2Test() {
        String productName = "clone_product_and_check_tag_list_v2_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .tagList(Arrays.asList("api_test", "test"))
                .build()
                .createObject();
        Product cloneProduct = copyProductByName(productName);
        deleteProductById(cloneProduct.getProductId());
        assertEquals(product.getTagList(), cloneProduct.getTagList());
    }

    @Test
    @Disabled
    @DisplayName("Загрузка продукта в GitLab по имени")
    @TmsLink("1361369")
    public void dumpToGitlabProductByNameTest() {
        String productName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "product_" + productName + "_" + product.getVersion();
        Response response = dumpProductToGitByName(productName);
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }
}
