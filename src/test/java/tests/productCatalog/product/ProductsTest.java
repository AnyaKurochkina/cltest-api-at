package tests.productCatalog.product;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.product.getProduct.response.GetProductResponse;
import httpModels.productCatalog.product.getProducts.response.GetProductsResponse;
import httpModels.productCatalog.product.getProducts.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.authorizer.Project;
import models.productCatalog.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import steps.references.ReferencesStep;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
        String productName = "create_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        GetImpl actualProduct = steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals(productName, actualProduct.getName());
    }

    @DisplayName("Создание продукта в продуктовом каталоге с категорией gitlab")
    @TmsLink("679086")
    @Test
    public void createProductWithCategory() {
        Product testProduct = Product.builder()
                .name("category_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
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
        ReferencesStep referencesStep = new ReferencesStep();
        Map<String, String> data = referencesStep.getPrivateResponsePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5").jsonPath().get("data");
        referencesStep.updateDataPrivatePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5",
                new JSONObject().put("test", "test"));
        Product product = Product.builder()
                .name("test_category")
                .title("test_category")
                .category("test")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        assertEquals("test", product.getCategory());
        referencesStep.partUpdatePrivatePagesById("enums", "bc55d445-5310-461c-a984-bf4c3bf1a6f5", new JSONObject()
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
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        assertTrue(steps.isExists(productName));
        assertFalse(steps.isExists("not_exists_name"));
    }

    @DisplayName("Проверка сортировки по дате создания в продуктах")
    @TmsLink("737649")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetProductsResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в продуктах")
    @TmsLink("737651")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetProductsResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }


    @DisplayName("Удаление продукта со статусом is_open=true")
    @TmsLink("737656")
    @Test
    public void deleteProductWithIsOpenTrue() {
        String errorText = "Deletion not allowed (is_open=True)";
        Product productIsOpenTrue = Product.builder().name("create_product_is_open_test_api")
                .isOpen(true)
                .build()
                .createObject();
        String productId = productIsOpenTrue.getProductId();
        Response deleteResponse = steps.getDeleteObjectResponse(productId).assertStatus(403);
        steps.partialUpdateObject(productId, new JSONObject().put("is_open", false));
        assertEquals(errorText, deleteResponse.jsonPath().get("error"));
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в продуктах")
    @TmsLink("737660")
    @Test
    public void checkAccessWithPublicToken() {
        String productName = "access_with_public_token_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String productId = product.getProductId();
        steps.getObjectByNameWithPublicToken(productName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps.createJsonObject("create_object_with_public_token_api"))
                .assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(productId, new JSONObject().put("description", "UpdateDescription"))
                .assertStatus(403);
        steps.putObjectByIdWithPublicToken(productId, steps.createJsonObject("update_object_with_public_token_api"))
                .assertStatus(403);
        steps.deleteObjectWithPublicToken(productId).assertStatus(403);
    }

    @DisplayName("Импорт продукта")
    @TmsLink("643393")
    @Test
    public void importProduct() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String name = new JsonPath(data).get("Product.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        assertTrue(steps.isExists(name));
        steps.deleteByName(name, GetProductsResponse.class);
        assertFalse(steps.isExists(name));
    }

    @DisplayName("Получение продукта по Id")
    @TmsLink("643395")
    @Test
    public void getProductById() {
        String productName = "get_by_id_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        GetImpl getProduct = steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals(productName, getProduct.getName());
    }

    @DisplayName("Частичное обновление продукта")
    @TmsLink("643402")
    @Test
    public void partialUpdateProduct() {
        Product product = Product.builder()
                .name("partial_update_product_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String expectedValue = "UpdateDescription";
        steps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        String actual = steps.getById(product.getProductId(), GetProductResponse.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в продуктах")
    @TmsLink("643412")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        Product product = Product.builder()
                .name("graph_version_calculated_product_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        GetImpl productCatalogGet = steps.getById(product.getProductId(), GetProductResponse.class);
        assertNotNull(productCatalogGet.getGraphVersionCalculated());
    }

    @DisplayName("Копирование продукта по Id")
    @TmsLink("643414")
    @Test
    public void copyProductById() {
        Product product = Product.builder()
                .name("clone_product_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        String cloneName = product.getName() + "-clone";
        steps.copyById(product.getProductId());
        assertTrue(steps.isExists(cloneName));
        steps.deleteByName(cloneName, GetProductsResponse.class);
        assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Обновление продукта")
    @TmsLink("643418")
    @Test
    public void updateProduct() {
        Product product = Product.builder()
                .name("update_product_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        product.updateProduct();
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
        steps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("max_count", 2));
        String currentVersion = steps.getById(productTest.getProductId(), GetProductResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("max_count", 3)
                .put("version", "1.999.999"));
        steps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("max_count", 4));
        currentVersion = steps.getById(productTest.getProductId(), GetProductResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("max_count", 5)
                .put("version", "999.999.999"));
        steps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("max_count", 6))
                .assertStatus(500);
    }

    @DisplayName("Получение время отклика на запрос")
    @TmsLink("643431")
    @Test
    public void getTime() {
        Assertions.assertTrue(2000 > steps.getTime("http://d4-product-catalog.apps" +
                ".d0-oscp.corp.dev.vtb/products/?is_open=true&env=dev&information_systems=c9fd31c7-25a5-45ca-863c-18425d1ae927&page=1&per_page=100"));
    }

    @DisplayName("Сортировка продуктов по статусу")
    @TmsLink("806312")
    @Test
    //todo вынести логику
    public void orderingByStatus() {
        List<ItemImpl> list = steps.orderingByStatus(GetProductsResponse.class).getItemsList();
        boolean result = false;
        int count = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            ListItem item = (ListItem) list.get(i);
            ListItem nextItem = (ListItem) list.get(i + 1);
            if (item.getIsOpen().equals(nextItem.getIsOpen())) {
                result = true;
            } else {
                count++;
            }
            if (count > 1) {
                result = false;
                break;
            }
        }
        assertTrue(result, "Список не отсортирован.");
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
        Response response = steps.getInfoProduct(product.getProductId());
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
        String version = product.getVersion();
        steps.partialUpdateObject(product.getProductId(), new JSONObject().put("info", newInfo));
        GetProductResponse getProductResponse = (GetProductResponse) steps.getById(product.getProductId(),
                GetProductResponse.class);
        assertEquals(version, getProductResponse.getVersion());
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
        steps.partialUpdateObject(product.getProductId(), new JSONObject().put("is_open", false));
        GetProductResponse getProductResponse = (GetProductResponse) steps.getById(product.getProductId(),
                GetProductResponse.class);
        assertEquals(version, getProductResponse.getVersion());
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
        product.deleteObject();
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
        steps.partialUpdateObject(productId, new JSONObject().put("max_count", 2));
        steps.partialUpdateObject(productId, new JSONObject().put("current_version", "1.0.1"));
        GetProductResponse getProduct = (GetProductResponse) steps.getById(productId, GetProductResponse.class);
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
        steps.partialUpdateObject(productId, new JSONObject().put("max_count", 2));
        steps.partialUpdateObject(productId, new JSONObject().put("current_version", "1.0.0"));
        GetProductResponse getProduct = (GetProductResponse) steps.getById(productId, GetProductResponse.class);
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
        GetProductResponse getProductById = (GetProductResponse) steps.getById(product.getProductId(),
                GetProductResponse.class);
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
        GetProductResponse getProductById = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertTrue(getProductById.getInGeneralList());
    }

    @Test
    @DisplayName("Загрузка Product в GitLab")
    @TmsLink("975400")
    public void dumpToGitlabProduct() {
        String productName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(product.getProductId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
    }

    @Test
    @DisplayName("Выгрузка Product из GitLab")
    @Disabled
    @TmsLink("")
    public void loadFromGitlabProduct() {
        String path = "product_standard_for_unloading_from_git_api";
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(path));
    }

    @DisplayName("Получение продукта по контексту id проекта без ограничений со стороны организации")
    @TmsLink("978268")
    @Test
    public void getProductWithOutOrgWithProjectContext() {
        Project project = Project.builder().build().createObject();
        Product product = Product.builder()
                .name("product_without_org_for_context_test_api")
                .informationSystems(Collections.emptyList())
                .envs(Collections.singletonList(project.getProjectEnvironmentPrefix().getEnvType().toLowerCase()))
                .build()
                .createObject();
        steps.getProductByContextProject(project.getId(), product.getProductId());
    }

    @Test
    @DisplayName("Получение значения поля category_v2 по умолчанию")
    @TmsLink("978267")
    public void getDefaultValueCategoryV2() {
        String productName = "get_default_value_category_v2_product_test_api";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
        GetProductResponse createdProduct = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals("compute", createdProduct.getCategoryV2());
    }

    @Test
    @DisplayName("Получение значения поля category_v2")
    @TmsLink("978299")
    public void getValueCategoryV2() {
        String productName = "get_value_category_v2_product_test_api";
        String categoryV2 = "web";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .category("postgre")
                .info(info)
                .build()
                .createObject();
        steps.partialUpdateObject(product.getProductId(), new JSONObject().put("category_v2", categoryV2));
        GetProductResponse createdProduct = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals(categoryV2, createdProduct.getCategoryV2());
        assertEquals(product.getVersion(), createdProduct.getVersion());
    }

    @Test
    @DisplayName("Получение значения поля payment в продуктах")
    @TmsLink("979091")
    public void getPaymentProduct() {
        String productName = "get_payment_product_test_api";
        String paymentValue = "paid";
        Product product = Product.builder()
                .name(productName)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList(Configure.ENV))
                .version("1.0.0")
                .payment(paymentValue)
                .info(info)
                .build()
                .createObject();
        String id = product.getProductId();
        GetProductResponse createdProduct = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals(paymentValue, createdProduct.getPayment());
        steps.partialUpdateObject(id, new JSONObject().put("payment", "free"));
        createdProduct = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals("free", createdProduct.getPayment());
        steps.partialUpdateObject(id, new JSONObject().put("payment", "partly_paid"));
        createdProduct = (GetProductResponse) steps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals("partly_paid", createdProduct.getPayment());
    }
}
