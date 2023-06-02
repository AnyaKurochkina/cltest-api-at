package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.Response;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.OnRequest;
import models.cloud.productCatalog.product.Payment;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ProductSteps.*;
import static steps.references.ReferencesStep.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductsTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");

    Map<String, String> info = new LinkedHashMap<String, String>() {{
        put("information", "testData");
    }};

    @DisplayName("Создание продукта в продуктовом каталоге")
    @TmsLink("643375")
    @Test
    public void createProduct() {
        Product product = Product.builder()
                .name("create_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        Product actualProduct = getProductById(product.getProductId());
        assertEquals(product, actualProduct);
    }

    @DisplayName("Создание продукта в продуктовом каталоге с иконкой")
    @TmsLink("1081698")
    @Test
    public void createProductWithIcon() {
        Icon icon = Icon.builder()
                .name("product_icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String productName = "create_product_with_icon_test_api";
        Product product = Product.builder()
                .name(productName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Product actualProduct = getProductById(product.getProductId());
        assertFalse(actualProduct.getIconStoreId().isEmpty());
        assertFalse(actualProduct.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких продуктов в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1081741")
    @Test
    public void createSeveralProductWithSameIcon() {
        Icon icon = Icon.builder()
                .name("product_icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String productName = "create_first_product_with_same_icon_test_api";
        Product product = Product.builder()
                .name(productName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Product secondProduct = Product.builder()
                .name("create_second_action_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Product actualFirstProduct = getProductById(product.getProductId());
        Product actualSecondProduct = getProductById(secondProduct.getProductId());
        assertEquals(actualFirstProduct.getIconUrl(), actualSecondProduct.getIconUrl());
        assertEquals(actualFirstProduct.getIconStoreId(), actualSecondProduct.getIconStoreId());
    }

    @DisplayName("Создание продукта в продуктовом каталоге с категорией gitlab")
    @TmsLink("679086")
    @Test
    public void createProductWithCategory() {
        Product testProduct = Product.builder()
                .name("category_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .category("gitlab")
                .build()
                .createObject();
        assertEquals("gitlab", testProduct.getCategory());
    }

    @Disabled
    @DisplayName("Создание продукта в продуктовом каталоге c новой категорией")
    @Test
    public void createProductWithUpdateCategory() {
        //todo переделать когда на ифт вольют изменения, пока тест падает.
        Map<String, String> data = getPrivateResponsePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5").jsonPath().get("data");
        updateDataPrivatePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5",
                new JSONObject().put("test", "test"));
        Product product = Product.builder()
                .name("test_category")
                .title("test_category")
                .category("test")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        assertEquals("test", product.getCategory());
        partialUpdatePrivatePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5", new JSONObject()
                .put("data", new JSONObject(data)));
    }

    @DisplayName("Проверка существования продукта по имени")
    @TmsLink("643392")
    @Test
    public void checkProductExists() {
        String productName = "product_exist_test_api";
        Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        assertTrue(isProductExists(productName));
        assertFalse(isProductExists("not_exists_name"));
    }

    @DisplayName("Проверка сортировки по дате создания в продуктах")
    @TmsLink("737649")
    @Test
    public void orderingByCreateData() {
        assertTrue(orderingProductByCreateData());
    }

    @DisplayName("Проверка сортировки по дате обновления в продуктах")
    @TmsLink("737651")
    @Test
    public void orderingByUpDateData() {
        assertTrue(orderingProductByUpdateData());
    }

    @DisplayName("Удаление продукта со статусом is_open=true")
    @TmsLink("737656")
    @Test
    public void deleteProductWithIsOpenTrue() {
        Product productIsOpenTrue = Product.builder()
                .name("create_product_is_open_test_api")
                .isOpen(true)
                .build()
                .createObject();
        String actualErrorMsg = getDeleteProductResponse(productIsOpenTrue.getProductId())
                .assertStatus(403).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Deletion not allowed (is_open=True)", actualErrorMsg);
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в продуктах")
    @TmsLink("737660")
    @Test
    public void checkAccessWithPublicToken() {
        String productName = "access_with_public_token_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String productId = product.getProductId();
        getProductByNameWithPublicToken(productName).assertStatus(200);
        JSONObject createProduct = Product.builder()
                .name("create_object_with_public_token_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .init().toJson();
        createProductWithPublicToken(createProduct).assertStatus(403);
        partialUpdateProductWithPublicToken(productId, new JSONObject().put("description", "UpdateDescription"))
                .assertStatus(403);
        putProductByIdWithPublicToken(productId, steps.createJsonObject("update_object_with_public_token_api"))
                .assertStatus(403);
        deleteProductWithPublicToken(productId).assertStatus(403);
    }

    @DisplayName("Получение продукта по Id")
    @TmsLink("643395")
    @Test
    public void getProductByIdTest() {
        Product expectedProduct = Product.builder()
                .name("get_by_id_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        Product actualProduct = getProductById(expectedProduct.getProductId());
        assertEquals(expectedProduct, actualProduct);
    }

    @DisplayName("Получение продукта по Id и фильтру with_version_fields=true")
    @TmsLink("1284598")
    @Test
    public void getProductByIdWithVersionFieldsTest() {
        Product expectedProduct = Product.builder()
                .name("get_by_id_with_version_field_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        Product actualProduct = getProductByIdAndFilter(expectedProduct.getProductId(), "with_version_fields=true");
        assertFalse(actualProduct.getVersionFields().isEmpty());
    }

    @DisplayName("Частичное обновление продукта")
    @TmsLink("643402")
    @Test
    public void partialUpdateProductTest() {
        Product expectedProduct = Product.builder()
                .name("partial_update_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String expectedValue = "UpdateDescription";
        partialUpdateProduct(expectedProduct.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        Product actualProduct = getProductById(expectedProduct.getProductId());
        assertEquals(expectedValue, actualProduct.getDescription());
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в продуктах")
    @TmsLink("643412")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        Product product = Product.builder()
                .name("graph_version_calculated_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        // TODO: 18.08.2022 Получить граф по айди и сравнить версию графа с graph_version_calculated
        assertNotNull(product.getGraphVersionCalculated());
    }

    @DisplayName("Копирование продукта по Id")
    @TmsLink("643414")
    @Test
    public void copyProductByIdTest() {
        Product product = Product.builder()
                .name("clone_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String cloneName = product.getName() + "-clone";
        copyProductById(product.getProductId());
        assertTrue(steps.isExists(cloneName));
        deleteProductByName(cloneName);
        assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Обновление продукта")
    @TmsLink("643418")
    @Test
    public void updateProductTest() {
        Product product = Product.builder()
                .name("update_product_test_api")
                .title("AtTestApiProduct")

                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String name = "updated_product_test_api";
        JSONObject json = Product.builder()
                .name("updated_product_test_api")
                .title("updated_product")
                .version("1.0.1")
                .info(info)
                .build().init().toJson();
        Product updatedProduct = updateProduct(product.getProductId(), json);
        assertEquals(name, updatedProduct.getName());
    }

    @DisplayName("Обновление продукта с указанием версии в граничных значениях")
    @TmsLink("643426")
    @Test
    public void updateProductAndGetVersion() {
        Product productTest = Product.builder()
                .name("product_version_test_api")
                .version("1.0.999")
                .maxCount(1)
                .build()
                .createObject();
        partialUpdateProduct(productTest.getProductId(), new JSONObject()
                .put("max_count", 2));
        String currentVersion = getProductById(productTest.getProductId()).getVersion();
        assertEquals("1.1.0", currentVersion);
        partialUpdateProduct(productTest.getProductId(), new JSONObject()
                .put("max_count", 3)
                .put("version", "1.999.999"));
        partialUpdateProduct(productTest.getProductId(), new JSONObject()
                .put("max_count", 4));
        currentVersion = getProductById(productTest.getProductId()).getVersion();
        assertEquals("2.0.0", currentVersion);
        partialUpdateProduct(productTest.getProductId(), new JSONObject()
                .put("max_count", 5)
                .put("version", "999.999.999"));
        String message = partialUpdateProduct(productTest.getProductId(), new JSONObject()
                .put("max_count", 6))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Version counter full [999, 999, 999]", message);
    }

    @DisplayName("Получение значения ключа info")
    @TmsLink("737663")
    @Test
    public void getProductInfo() {
        Product product = Product.builder()
                .name("get_products_by_category_test_api")
                .title("AtTestApiProduct")
                .info(info)
                .category("vm")
                .build()
                .createObject();
        Response response = getInfoProduct(product.getProductId());
        assertEquals(response.jsonPath().get(), info);
    }

    @DisplayName("Проверка независимого от версии поля info в продуктах")
    @TmsLink("806774")
    @Test
    public void checkVersionWhenInfoUpdate() {
        Product product = Product.builder()
                .name("check_version_when_info_update_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .category("vm")
                .info(info)
                .build()
                .createObject();
        Map<String, String> newInfo = new LinkedHashMap<String, String>() {{
            put("information", "newData");
        }};
        partialUpdateProduct(product.getProductId(), new JSONObject().put("info", newInfo));
        Product updatedProduct = getProductById(product.getProductId());
        assertEquals(product.getVersion(), updatedProduct.getVersion());
    }

    @DisplayName("Проверка независимого от версии поля is_open в продуктах")
    @TmsLink("806778")
    @Test
    public void checkVersionWhenIsOpenUpdate() {
        Product product = Product.builder()
                .name("check_version_when_is_open_update_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .category("vm")
                .isOpen(true)
                .build()
                .createObject();
        String version = product.getVersion();
        partialUpdateProduct(product.getProductId(), new JSONObject().put("is_open", false));
        Product updatedProduct = getProductById(product.getProductId());
        assertEquals(version, updatedProduct.getVersion());
    }

    @Test
    @DisplayName("Удаление продукта")
    @TmsLink("643434")
    public void deleteProduct() {
        Product product = Product.builder()
                .name("delete_product_test_api")
                .title("AtTestApiProduct")
                .category("vm")
                .build()
                .createObject();
        deleteProductById(product.getProductId());
    }

    @Test
    @DisplayName("Присвоение значения current_version из списка version_list в продуктах")
    @TmsLink("821983")
    public void setCurrentVersionProduct() {
        String productName = "set_current_version_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .maxCount(1)
                .build()
                .createObject();
        String productId = product.getProductId();
        partialUpdateProduct(productId, new JSONObject().put("max_count", 2));
        partialUpdateProduct(productId, new JSONObject().put("current_version", "1.0.1"));
        Product getProduct = getProductById(productId);
        assertEquals("1.0.1", getProduct.getCurrentVersion());
        assertTrue(getProduct.getVersionList().contains(getProduct.getCurrentVersion()));
    }

    @Test
    @DisplayName("Получение продукта версии указанной в current_version")
    @TmsLink("821988")
    public void getCurrentVersionProduct() {
        String productName = "create_current_version_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .maxCount(1)
                .build()
                .createObject();
        String productId = product.getProductId();
        partialUpdateProduct(productId, new JSONObject().put("max_count", 2));
        partialUpdateProduct(productId, new JSONObject().put("current_version", "1.0.0"));
        Product getProduct = getProductById(productId);
        assertEquals("1.0.0", getProduct.getCurrentVersion());
        assertEquals(product.getMaxCount(), 1);
    }

    @Test
    @DisplayName("Получение значения extra_data в продуктах")
    @TmsLink("821990")
    public void getExtraDataProduct() {
        String productName = "extra_data_product_test_api";
        String key = "extra";
        String value = "data";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .extraData(new LinkedHashMap<String, String>() {{
                    put(key, value);
                }})
                .build()
                .createObject();
        Product getProductById = getProductById(product.getProductId());
        Map<String, String> extraData = getProductById.getExtraData();
        assertEquals(extraData.get(key), value);
    }

    @Test
    @DisplayName("Проверка значения поля in_general_list в продуктах")
    @TmsLink("852671")
    public void getInGeneralListField() {
        String productName = "in_general_list_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .inGeneralList(true)
                .build()
                .createObject();
        Product getProductById = getProductById(product.getProductId());
        assertTrue(getProductById.getInGeneralList());
    }

    @Test
    @Disabled
    @DisplayName("Загрузка Product в GitLab")
    @TmsLink("975400")
    public void dumpToGitlabProduct() {
        String productName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.2")
                .build()
                .createObject();
        String tag = "product_" + productName + "_" + product.getVersion();
        Response response = dumpProductToBitbucket(product.getProductId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @Disabled
    @DisplayName("Выгрузка Product из GitLab")
    @TmsLink("1028975")
    public void loadFromGitlabProduct() {
        String productName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .version("1.0.0")
                .build()
                .createObject();
        Response response = dumpProductToBitbucket(product.getProductId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteProductById(product.getProductId());
        String path = "product_" + productName + "_" + product.getVersion();
        loadProductFromBitbucket(new JSONObject().put("path", path));
        assertTrue(isProductExists(productName));
        deleteProductByName(productName);
        assertFalse(isProductExists(productName));
    }

    @DisplayName("Получение продукта по контексту id проекта без ограничений со стороны организации")
    @TmsLink("978268")
    @Test
    public void getProductWithOutOrgWithProjectContext() {
        Project project = Project.builder().build().createObject();
        Product product = Product.builder()
                .name("product_without_org_for_context_test_api")
                .informationSystems(Collections.emptyList())
                .build()
                .createObject();
        steps.getProductByContextProject(project.getId(), product.getProductId());
    }

    @Test
    @DisplayName("Получение значения поля category_v2")
    @TmsLink("978299")
    public void getValueCategoryV2() {
        String productName = "get_value_category_v2_product_test_api";
        Categories categoryV2 = Categories.WEB;
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .version("1.0.0")
                .category("postgre")
                .categoryV2(Categories.APPLICATION_INTEGRATION)
                .info(info)
                .build()
                .createObject();
        partialUpdateProduct(product.getProductId(), new JSONObject().put("category_v2", categoryV2.getValue()));
        Product createdProduct = getProductById(product.getProductId());
        assertEquals(categoryV2, createdProduct.getCategoryV2());
        assertEquals(product.getVersion(), createdProduct.getVersion());
    }

    @Test
    @DisplayName("Получение значения поля payment в продуктах")
    @TmsLink("979091")
    public void getPaymentProduct() {
        Product product = Product.builder()
                .name("get_payment_product_test_api")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .payment(Payment.PAID)
                .info(info)
                .build()
                .createObject();
        String id = product.getProductId();
        assertEquals(Payment.PAID, getProductById(id).getPayment());
        steps.partialUpdateObject(id, new JSONObject().put("payment", "free"));
        assertEquals(Payment.FREE, getProductById(id).getPayment());
        steps.partialUpdateObject(id, new JSONObject().put("payment", "partly_paid"));
        assertEquals(Payment.PARTLY_PAID, getProductById(id).getPayment());
    }

    @DisplayName("Создание продукта с допустимыми значениями поля on_request")
    @TmsLink("1322847")
    @Test
    public void createProductWithOnRequestValidValuesTest() {
        Product product = Product.builder()
                .name("create_product_with_on_request_only_request")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .onRequest(OnRequest.ONLY_REQUEST)
                .build()
                .createObject();
        Product productPreview = Product.builder()
                .name("create_product_with_on_request_preview")
                .title("AtTestApiProduct")
                .version("1.0.0")
                .info(info)
                .onRequest(OnRequest.PREVIEW)
                .build()
                .createObject();
        Product actualProduct = getProductById(product.getProductId());
        Product actualProductPreview = getProductById(productPreview.getProductId());
        assertEquals(OnRequest.ONLY_REQUEST, actualProduct.getOnRequest());
        assertEquals(OnRequest.PREVIEW, actualProductPreview.getOnRequest());
    }

    @DisplayName("Проверка значений полей по умолчанию")
    @TmsLinks({@TmsLink("1322845"), @TmsLink("1107406"), @TmsLink("978267")})
    @Test
    public void checkProductDefaultFieldValues() {
        Product product = Product.builder()
                .name("at_api_check_default_values")
                .title("AT API Product")
                .version("1.0.0")
                .build()
                .createObject();
        Product createdProduct = getProductById(product.getProductId());
        assertEquals(Categories.COMPUTE, createdProduct.getCategoryV2());
        assertEquals(50, createdProduct.getNumber());
        assertNull(createdProduct.getOnRequest());
        assertEquals(0, createdProduct.getTagList().size());
    }

    @DisplayName("Проверка значения поля tag_list")
    @TmsLink("1676200")
    @Test
    public void checkTagListValue() {
        List<String> tagList = Arrays.asList("TestTag1", "TestTag2");
        Product product = Product.builder()
                .name("at_api_check_tag_list_value")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        Product createdProduct = getProductById(product.getProductId());
        AssertUtils.assertEqualsList(tagList, createdProduct.getTagList());
        tagList = Collections.singletonList("TestTag3");
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject().put("tag_list", tagList));
        createdProduct = getProductById(product.getProductId());
        AssertUtils.assertEqualsList(tagList, createdProduct.getTagList());
    }

    @DisplayName("Проверка неверсионности поля tag_list")
    @TmsLink("1676202")
    @Test
    public void checkTagListVersioning() {
        List<String> tagList = Arrays.asList("TestTag1", "TestTag2");
        Product product = Product.builder()
                .name("at_api_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        Product createdProduct = getProductById(product.getProductId());
        tagList = Collections.singletonList("TestTag3");
        partialUpdateProduct(createdProduct.getProductId(), new JSONObject().put("tag_list", tagList));
        createdProduct = getProductById(product.getProductId());
        assertEquals("1.0.0", createdProduct.getVersion());
    }
}
