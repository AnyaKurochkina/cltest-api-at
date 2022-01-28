package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.existsService.response.ExistsServiceResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Services;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: сервисы")
public class ServicesTest extends Tests {

    Services service;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    private final String productName = "services/";

    @Order(1)
    @DisplayName("Создание сервиса в продуктовом каталоге")
    @Test
    public void createService() {
        service = Services.builder()
                .serviceName("create_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка сервисов")
    @Test
    public void getServiceList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(productName, GetServiceListResponse.class)
                .size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @Test
    public void checkServiceExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(productName, service.getServiceName(), ExistsServiceResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists(productName, "not_exist_name", ExistsServiceResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт сервиса")
    @Test
    public void importService() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importService.json");
        String serviceName = new JsonPath(data).get("Service.json.name");
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        Assertions.assertTrue(productCatalogSteps.isExists(productName, serviceName, ExistsServiceResponse.class));
        productCatalogSteps.deleteByName(productName, serviceName, GetServiceListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, serviceName, ExistsServiceResponse.class));
    }

    @Order(5)
    @DisplayName("Негативный тест на создание сервиса с существующим именем")
    @Test
    public void createServiceWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject(service.getServiceName(), "/productCatalog/services/createServices.json"))
                .assertStatus(400);
    }

    @Order(6)
    @DisplayName("Получение сервиса по Id")
    @Test
    public void getServiceById() {
        GetImpl serviceResponse = productCatalogSteps.getById(productName, service.getServiceId(), GetServiceResponse.class);
        Assertions.assertEquals(serviceResponse.getName(), service.getServiceName());
    }

    @Order(7)
    @DisplayName("Негатичный тест на получение сервиса по Id без токена")
    @Test
    public void getServiceByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, service.getServiceId(), GetServiceResponse.class);
    }

    @Order(40)
    @DisplayName("Копирование сервиса")
    @Test
    public void copyService() {
        String expectedName = service.getServiceName() + "-clone";
        productCatalogSteps.copyById(productName, service.getServiceId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, expectedName, ExistsServiceResponse.class));
        productCatalogSteps.deleteByName(productName, expectedName, GetServiceListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, expectedName, ExistsServiceResponse.class));
    }

    @Order(41)
    @DisplayName("Негатичный тест на копирование сервиса по Id без токена")
    @Test
    public void copyServiceWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(productName, service.getServiceId());
    }

    @Order(50)
    @DisplayName("Обновление сервиса")
    @Test
    public void updateServiceDescription() {
        String expected = "Update description";
        productCatalogSteps.partialUpdateObject(productName, service.getServiceId(), new JSONObject()
                .put("description", expected));
        String actual = productCatalogSteps
                .getById(productName, service.getServiceId(), GetServiceResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Order(51)
    @DisplayName("Негативный тест на обновление сервиса по Id без токена")
    @Test
    public void updateServiceByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(productName, String.valueOf(service.getServiceId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(60)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl getServiceResponse = productCatalogSteps.getById(productName, service.getServiceId(), GetServiceResponse.class);
        Assertions.assertNotNull(getServiceResponse.getGraphVersionCalculated());
    }

    @Order(70)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми символами в имени")
    @Test
    public void createServiceWithInvalidCharacters() {
        assertAll("Сервис создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("NameWithUppercase", "/productCatalog/services/createServices.json"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("nameWithUppercaseInMiddle", "/productCatalog/services/createServices.json"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("имя", "/productCatalog/services/createServices.json"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("Имя", "/productCatalog/services/createServices.json"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("a&b&c", "/productCatalog/services/createServices.json"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("", "/productCatalog/services/createServices.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject(" ", "/productCatalog/services/createServices.json"))
                        .assertStatus(400)
        );
    }

    @Order(80)
    @DisplayName("Создание сервиса со значением ключа graph_id равным null")
    @Test
    public void createServiceWithGraphIdNull() {
        CreateServiceResponse createServiceResponse = productCatalogSteps
                .createProductObject(productName, JsonHelper.getJsonTemplate("productCatalog/services/createServiceWithGraphIdNull.json")
                        .set("name", "create_service_with_graph_id_null_test_api")
                        .build()).extractAs(CreateServiceResponse.class);
        Assertions.assertNull(createServiceResponse.getGraphId(), "GraphId не равен null");
        productCatalogSteps.deleteById(productName, createServiceResponse.getId());
    }

    @Order(89)
    @DisplayName("Обновление сервиса с указанием версии в граничных значениях")
    @Test
    public void updateServiceAndGetVersion() {
        Services services = Services.builder().serviceName("service_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(productName, services.getServiceId(), new JSONObject().put("name", "service_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(productName, services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productName, services.getServiceId(), new JSONObject().put("name", "service_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(productName, services.getServiceId(), new JSONObject().put("name", "service_version_test_api4"));
        currentVersion = productCatalogSteps.getById(productName, services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(productName, services.getServiceId(), new JSONObject().put("name", "service_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(productName, services.getServiceId(), new JSONObject().put("name", "service_version_test_api6"))
                .assertStatus(500);
    }

    @Order(90)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @Test
    public void createServiceWithInvalidGraphId() {
        assertAll("Создался сервис недопустимым graph_id",
                () -> productCatalogSteps.createProductObject(productName, JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_not_exist_graph_id")
                        .set("graph_id", "dgdh-4565-dfgdf")
                        .build()).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service2_with_not_exist_graph_id")
                        .set("graph_id", 56564)
                        .build()).assertStatus(500)
        );
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление сервиса без токена")
    @Test
    public void deleteServiceWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(productName, String.valueOf(service.getServiceId()));
    }

    @Order(100)
    @Test
    @DisplayName("Удаление сервиса")
    @MarkDelete
    public void deleteService() {
        try (Services service = Services.builder()
                .serviceName("create_service_test_api")
                .build()
                .createObjectExclusiveAccess()) {
            service.deleteObject();
        }
    }
}
