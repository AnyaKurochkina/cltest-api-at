package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.product.OnRequest;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.ProductSteps;

import java.util.stream.Stream;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductNegativeTest extends Tests {

    @DisplayName("Негативный тест на получение продукта по Id без токена")
    @TmsLink("643397")
    @Test
    public void getProductByIdWithOutTokenTest() {
        Product product = ProductSteps.createProduct("get_by_id_product_without_token_test_api");
        String errorMessage = getProductByIdWithOutToken(product.getProductId()).jsonPath().get("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на обновление продукта по Id без токена")
    @TmsLink("643407")
    @Test
    public void updateProductByIdWithOutToken() {
        Product product = ProductSteps.createProduct("update_product_without_token_test_api");
        String errorMessage = partialUpdateProductWithOutToken(product.getProductId(), new JSONObject()
                .put("description", "UpdateDescription")).jsonPath().get("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @TmsLink("643409")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        Product product = ProductSteps.createProduct("update_to_current_version_product_test_api");
        String currentVersion = product.getVersion();
        String errorMessage = partialUpdateProduct(product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Версия %s для update_to_current_version_product_test_api уже существует", currentVersion),
                errorMessage);
    }

    @DisplayName("Негативный тест на копирование продукта по Id без токена")
    @TmsLink("643416")
    @Test
    public void copyProductByIdWithOutTokenTest() {
        Product product = ProductSteps.createProduct("clone_product_negative_test_api");
        String errorMessage = copyProductByIdWithOutToken(product.getProductId()).jsonPath().get("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @TmsLink("643420")
    @Test
    public void createProductWithSameName() {
        Product product = ProductSteps.createProduct("create_product_with_same_name_test_api");
        String errorMessage = getCreateProductResponse(product.toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"name\": product с таким name уже существует.", errorMessage);
    }

    @DisplayName("Негативный тест на создание продукта с недопустимыми символами в имени")
    @TmsLink("643423")
    @ParameterizedTest
    @MethodSource("testData")
    public void createProductWithInvalidCharacters(String name, String message) {
        AssertResponse.run(() -> createProduct(name)).status(400)
                .responseContains(format(message, name));
    }

    static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("NameWithUppercase", "Cannot instantiate (Product) named ({})"),
                Arguments.of("nameWithUppercaseInMiddle", "Cannot instantiate (Product) named ({})"),
                Arguments.of("Имя", "Cannot instantiate (Product) named ({})"),
                Arguments.of("имя", "Cannot instantiate (Product) named ({})"),
                Arguments.of("a&b&c", "Cannot instantiate (Product) named ({})"),
                Arguments.of("", "Это поле не может быть пустым."),
                Arguments.of(" ", "Это поле не может быть пустым.")
        );
    }

    @DisplayName("Негативный тест на удаление продукта без токена")
    @TmsLink("643433")
    @Test
    public void deleteProductWithOutToken() {
        Product product = ProductSteps.createProduct("delete_product_without_token_test_api");
        String errorMessage = deleteProductByIdWithOutToken(product.getProductId()).jsonPath().get("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в продуктах")
    @TmsLink("821980")
    public void setInvalidCurrentVersionProduct() {
        Product product = ProductSteps.createProduct("invalid_current_version_product_test_api");
        String productId = product.getProductId();
        String error = partialUpdateProduct(productId, new JSONObject().put("current_version", "2")).assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals("You must specify version in pattern like \"{num}. | {num}.{num}.\"", error);
    }

    @Test
    @DisplayName("Негативный тест на передачу значения поля category_v2 не из справочника")
    @TmsLink("978310")
    public void getInvalidValueCategoryV2() {
        Product product = ProductSteps.createProduct("get_invalid_value_category_v2_product_test_api");
        String error = partialUpdateProduct(product.getProductId(), new JSONObject().put("category_v2", "test"))
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals("The value (test) is not among the valid options in ProductCategoriesV2", error);
    }

    @Test
    @DisplayName("Негативный тест на удаление product с полем isOpen=true")
    @TmsLink("979091")
    public void deleteProductIsOpenTrue() {
        String name = "delete_is_open_true_product_test_api";
        Product product = Product.builder()
                .name(name)
                .title(name)
                .isOpen(true)
                .version("1.0.0")
                .build().createObject();
        String productId = product.getProductId();
        String message = getDeleteProductResponse(productId).assertStatus(403).extractAs(ErrorMessage.class).getMessage();
        partialUpdateProduct(productId, new JSONObject().put("is_open", false));
        assertEquals("Deletion not allowed (is_open=True)", message);
    }

    @DisplayName("Негативный тест на создание продукта cо значением number меньше min значения")
    @TmsLink("1107460")
    @Test
    public void createProductWithDefaultNumber() {
        String productName = "create_product_with_default_number";
        JSONObject product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .number(-1)
                .build()
                .toJson();
        String message = getCreateProductResponse(product).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"number\": Убедитесь, что это значение больше либо равно 0.", message);
    }

    @DisplayName("Негативный тест создание продукта с недопустимым значением поля on_request")
    @TmsLink("1322843")
    @Test
    public void createProductWithOnRequestInValidValuesTest() {
        JSONObject product = Product.builder()
                .name("create_product_with_on_request_invalid_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .onRequest(OnRequest.TEST)
                .build()
                .toJson();
        String errMessage = getCreateProductResponse(product).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("\"on_request\": Значения %s нет среди допустимых вариантов.", OnRequest.TEST.getValue()), errMessage);
    }
}