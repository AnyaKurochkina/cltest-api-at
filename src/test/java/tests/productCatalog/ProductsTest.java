package tests.productCatalog;

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
import models.productCatalog.Product;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import steps.references.ReferencesStep;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductsTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("products/",
            "productCatalog/products/createProduct.json", Configure.ProductCatalogURL);
    Product product;
    Map<String, String> info = new LinkedHashMap<String, String>() {{
        put("information", "testData");
    }};
    private static final String NAME = "product_test_api-:2022.";

    @Order(1)
    @DisplayName("Создание продукта в продуктовом каталоге")
    @TmsLink("643375")
    @Test
    public void createProduct() {
        product = Product.builder()
                .name(NAME)
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .info(info)
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Создание продукта в продуктовом каталоге с категорией gitlab_group")
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


    @Order(4)
    @DisplayName("Получение списка продуктов")
    @TmsLink("643387")
    @Test
    public void getProductList() {
        List<ItemImpl> list = productCatalogSteps.getProductObjectList(GetProductsResponse.class);
        assertTrue(productCatalogSteps.isSorted(list), "Список не отсортирован.");
    }

    @Order(5)
    @DisplayName("Проверка значения next в запросе на получение списка продуктов")
    @TmsLink("679088")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetProductsResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @Order(6)
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

    @Order(10)
    @DisplayName("Проверка существования продукта по имени")
    @TmsLink("643392")
    @Test
    public void checkProductExists() {
        assertTrue(productCatalogSteps.isExists(product.getName()));
        assertFalse(productCatalogSteps.isExists("not_exists_name"));
    }

    @Order(11)
    @DisplayName("Проверка сортировки по дате создания в продуктах")
    @TmsLink("737649")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByCreateData(GetProductsResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(12)
    @DisplayName("Проверка сортировки по дате обновления в продуктах")
    @TmsLink("737651")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByUpDateData(GetProductsResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(12)
    @DisplayName("Удаление продукта со статусом is_open=true")
    @TmsLink("737656")
    @Test
    public void deleteProductWithIsOpenTrue() {
        Product productIsOpenTrue = Product.builder().name("create_product_is_open_test_api")
                .isOpen(true)
                .build()
                .createObject();
        String productId = productIsOpenTrue.getProductId();
        Response deleteResponse = productCatalogSteps.getDeleteObjectResponse(productId)
                .assertStatus(200);
        productCatalogSteps.partialUpdateObject(productId, new JSONObject().put("is_open", false));
        assertEquals(deleteResponse.jsonPath().get("error"), "Deletion not allowed (is_open=True)");

    }

    @Order(14)
    @DisplayName("Проверка доступа для методов с публичным ключом в продуктах")
    @TmsLink("737660")
    @Test
    public void checkAccessWithPublicToken() {
        productCatalogSteps.getObjectByNameWithPublicToken(product.getName()).assertStatus(200);
        productCatalogSteps.createProductObjectWithPublicToken(productCatalogSteps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.partialUpdateObjectWithPublicToken(product.getProductId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        productCatalogSteps.putObjectByIdWithPublicToken(product.getProductId(), productCatalogSteps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.deleteObjectWithPublicToken(product.getProductId()).assertStatus(403);
    }

    @Order(15)
    @DisplayName("Импорт продукта")
    @TmsLink("643393")
    @Test
    public void importProduct() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        String name = new JsonPath(data).get("Product.json.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/products/importProduct.json");
        assertTrue(productCatalogSteps.isExists(name));
        productCatalogSteps.deleteByName(name, GetProductsResponse.class);
        assertFalse(productCatalogSteps.isExists(name));
    }

    @Order(20)
    @DisplayName("Получение продукта по Id")
    @TmsLink("643395")
    @Test
    public void getProductById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(product.getProductId(), GetProductResponse.class);
        assertEquals(productCatalogGet.getName(), product.getName());
    }

    @Order(25)
    @DisplayName("Негативный тест на получение продукта по Id без токена")
    @TmsLink("643397")
    @Test
    public void getProductByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(product.getProductId());
    }

    @Order(30)
    @DisplayName("Частичное обновление продукта")
    @TmsLink("643402")
    @Test
    public void partialUpdateProduct() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", expectedValue))
                .assertStatus(200);
        String actual = productCatalogSteps.getById(product.getProductId(), GetProductResponse.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(35)
    @DisplayName("Негативный тест на обновление продукта по Id без токена")
    @TmsLink("643407")
    @Test
    public void updateProductByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(product.getProductId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(40)
    @DisplayName("Негативный тест на попытку обновления продукта до текущей версии")
    @TmsLink("643409")
    @Test
    public void partialUpdateProductForCurrentVersion() {
        String currentVersion = product.getVersion();
        productCatalogSteps.partialUpdateObject(product.getProductId(), new JSONObject().put("description", "update")
                .put("version", currentVersion)).assertStatus(500);
    }

    @Order(45)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в продуктах")
    @TmsLink("643412")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl productCatalogGet = productCatalogSteps.getById(product.getProductId(), GetProductResponse.class);
        assertNotNull(productCatalogGet.getGraphVersionCalculated());
    }

    @Order(50)
    @DisplayName("Копирование продукта по Id")
    @TmsLink("643414")
    @Test
    public void copyProductById() {
        String cloneName = product.getName() + "-clone";
        productCatalogSteps.copyById(product.getProductId());
        assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.deleteByName(cloneName, GetProductsResponse.class);
        assertFalse(productCatalogSteps.isExists(cloneName));
    }

    @Order(55)
    @DisplayName("Негативный тест на копирование продукта по Id без токена")
    @TmsLink("643416")
    @Test
    public void copyProductByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(product.getProductId());
    }

    @Order(60)
    @DisplayName("Обновление продукта")
    @TmsLink("643418")
    @Test
    public void updateProduct() {
        product.updateProduct();
    }

    @Order(65)
    @DisplayName("Негативный тест на создание продукта с существующим именем")
    @TmsLink("643420")
    @Test
    public void createProductWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(product.getName())).assertStatus(400);
    }

    @Order(70)
    @DisplayName("Негативный тест на создание продукта с недопустимыми символами в имени")
    @TmsLink("643423")
    @Test
    public void createProductWithInvalidCharacters() {
        assertAll("Продукт создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("a&b&c"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(""))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(" "))
                        .assertStatus(400)
        );
    }

    @Order(75)
    @DisplayName("Обновление продукта с указанием версии в граничных значениях")
    @TmsLink("643426")
    @Test
    public void updateProductAndGetVersion() {
        Product productTest = Product.builder().name("product_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("name", "product_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(productTest.getProductId(), GetProductResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("name", "product_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject()
                .put("name", "product_version_test_api4"));
        currentVersion = productCatalogSteps.getById(productTest.getProductId(), GetProductResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(productTest.getProductId(), new JSONObject().put("name", "product_version_test_api6"))
                .assertStatus(500);
    }

    @Order(80)
    @DisplayName("Получение время отклика на запрос")
    @TmsLink("643431")
    @Test
    public void getTime() {
        Assertions.assertTrue(2000 > productCatalogSteps.getTime("http://d4-product-catalog.apps" +
                ".d0-oscp.corp.dev.vtb/products/?is_open=true&env=dev&information_systems=c9fd31c7-25a5-45ca-863c-18425d1ae927&page=1&per_page=100"));
    }

    @Order(80)
    @DisplayName("Сортировка продуктов по статусу")
    @TmsLink("")
    @Test
    public void orderingByStatus() {
        List<ItemImpl> list = productCatalogSteps.orderingByStatus(GetProductsResponse.class).getItemsList();
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

    @Order(96)
    @DisplayName("Получение списка продуктов по фильтру is_open")
    @TmsLink("")
    @Test
    public void getProductListByIsOpen() {
        Product.builder()
                .name("get_products_by_status_test_api")
                .title("AtTestApiProduct")
                .build()
                .createObject();
        List<ItemImpl> productList = productCatalogSteps.getProductObjectList(GetProductsResponse.class, "?is_open=true");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getIsOpen());
        }
    }

    @Order(97)
    @DisplayName("Получение списка продуктов по фильтру category")
    @TmsLink("")
    @Test
    public void getProductListByCategory() {
        Product.builder()
                .name("get_products_by_category_test_api")
                .title("AtTestApiProduct")
                .category("vm")
                .build()
                .createObject();
        List<ItemImpl> productList = productCatalogSteps.getProductObjectList(GetProductsResponse.class, "?category=vm");
        for (ItemImpl item : productList) {
            ListItem listItem = (ListItem) item;
            assertEquals("vm", listItem.getCategory());
        }
    }

    @Order(98)
    @DisplayName("Получение значения ключа info")
    @TmsLink("737663")
    @Test
    public void getProductInfo() {
        Response response = productCatalogSteps.getInfoProduct(product.getProductId());
        assertEquals(response.jsonPath().get(), info);
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление продукта без токена")
    @TmsLink("643433")
    @Test
    public void deleteProductWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(product.getProductId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление продукта")
    @TmsLink("643434")
    @MarkDelete
    public void deleteProduct() {
        product.deleteObject();
    }
}
