package tests.productCatalog.product;

import core.helper.Configure;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");

    Map<String, String> info = new LinkedHashMap<String, String>() {{
        put("information", "testData");
    }};

    @DisplayName("Негативный тест на получение продукта по Id без токена")
    @TmsLink("643397")
    @Test
    public void getProductByIdWithOutToken() {
        Product product = Product.builder()
                .name("get_by_id_product_without_token_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        steps.getByIdWithOutToken(product.getProductId());
    }

    @DisplayName("Негативный тест на обновление продукта по Id без токена")
    @TmsLink("643407")
    @Test
    public void updateProductByIdWithOutToken() {
        Product product = Product.builder()
                .name("update_product_without_token_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(product.getProductId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @TmsLink("643409")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        Product product = Product.builder()
                .name("update_to_current_version_product_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String currentVersion = product.getVersion();
        steps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @DisplayName("Негативный тест на копирование продукта по Id без токена")
    @TmsLink("643416")
    @Test
    public void copyProductByIdWithOutToken() {
        Product product = Product.builder()
                .name("clone_product_negative_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(product.getProductId());
    }

    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @TmsLink("643420")
    @Test
    public void createProductWithSameName() {
        Product product = Product.builder()
                .name("create_product_with_same_name_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(product.getName())).assertStatus(400);
    }

    @DisplayName("Негативный тест на создание продукта с недопустимыми символами в имени")
    @TmsLink("643423")
    @Test
    public void createProductWithInvalidCharacters() {
        Product.builder().name("NameWithUppercase").build().negativeCreateRequest(500);
        Product.builder().name("nameWithUppercaseInMiddle").build().negativeCreateRequest(500);
        Product.builder().name("имя").build().negativeCreateRequest(500);
        Product.builder().name("Имя").build().negativeCreateRequest(500);
        Product.builder().name("a&b&c").build().negativeCreateRequest(500);
        Product.builder().name("").build().negativeCreateRequest(400);
        Product.builder().name(" ").build().negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на удаление продукта без токена")
    @TmsLink("643433")
    @Test
    public void deleteProductWithOutToken() {
        Product product = Product.builder()
                .name("delete_product_without_token_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(product.getProductId());
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в продуктах")
    @TmsLink("821980")
    public void setInvalidCurrentVersionProduct() {
        String name = "invalid_current_version_product_test_api";
        Product product = Product.builder()
                .name(name)
                .title(name)
                .version("1.0.0")
                .build().createObject();
        String productId = product.getProductId();
        steps.partialUpdateObject(productId, new JSONObject().put("current_version", "2")).assertStatus(500);
    }

    @Test
    @DisplayName("Негативный тест на передачу значения поля category_v2 не из справочника")
    @TmsLink("978310")
    public void getInvalidValueCategoryV2() {
        String productName = "get_invalid_value_category_v2_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        Response response = steps.partialUpdateObject(product.getProductId(), new JSONObject().put("category_v2", "test"));
        assertEquals("Значения test нет среди допустимых вариантов.", response.jsonPath()
                .getList("category_v2").get(0));
    }

    @Test
    @DisplayName("Негативный тест на удаление product с полем isOpen=true")
    @TmsLink("979091")
    public void deleteProductIsOpenTrue() {
        String name = "delete_is_open_true_product_test_api";
        String errorText = "Deletion not allowed (is_open=True)";
        Product product = Product.builder()
                .name(name)
                .title(name)
                .isOpen(true)
                .version("1.0.0")
                .build().createObject();
        String productId = product.getProductId();
       Response response = steps.getDeleteObjectResponse(productId).assertStatus(403);
       steps.partialUpdateObject(productId, new JSONObject().put("is_open", false));
       assertEquals(errorText, response.jsonPath().get("error"));
    }

    @Test
    @DisplayName("Негативный тест на передачу значения поля payment не из справочника")
    @TmsLink("979225")
    public void setInvalidValuePayment() {
        String productName = "set_invalid_value_payment_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        Response response = steps.partialUpdateObject(product.getProductId(), new JSONObject().put("payment", "test"))
                .assertStatus(400);
        assertEquals("Значения test нет среди допустимых вариантов.", response.jsonPath()
                .getList("payment").get(0));
    }
}
