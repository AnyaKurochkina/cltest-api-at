package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Services;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
public class ServicesTest extends Tests {

    Services service;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("services/", "/productCatalog/services/createServices.json");

    @Order(1)
    @DisplayName("Создание сервиса в продуктовом каталоге")
    @TmsLink("643448")
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
    @TmsLink("643450")
    @Test
    public void getServiceList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(GetServiceListResponse.class)
                .size() > 0);
    }

    @Order(2)
    @DisplayName("Проверка значения next в запросе на получение списка сервисов")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetServiceListResponse.class).getNext();
        if (!(str == null)) {
            assertTrue(str.startsWith("http://dev-kong-service.apps.d0-oscp.corp.dev.vtb/"));
        }
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @TmsLink("643453")
    @Test
    public void checkServiceExists() {
        Assertions.assertTrue(productCatalogSteps.isExists(service.getServiceName()));
        Assertions.assertFalse(productCatalogSteps.isExists("not_exist_name"));
    }

    @Order(4)
    @DisplayName("Импорт сервиса")
    @TmsLink("643454")
    @Test
    public void importService() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importService.json");
        String serviceName = new JsonPath(data).get("Service.json.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        Assertions.assertTrue(productCatalogSteps.isExists(serviceName));
        productCatalogSteps.deleteByName(serviceName, GetServiceListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(serviceName));
    }

    @Order(5)
    @DisplayName("Негативный тест на создание сервиса с существующим именем")
    @TmsLink("643458")
    @Test
    public void createServiceWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(service.getServiceName()))
                .assertStatus(400);
    }

    @Order(6)
    @DisplayName("Получение сервиса по Id")
    @TmsLink("643459")
    @Test
    public void getServiceById() {
        GetImpl serviceResponse = productCatalogSteps.getById(service.getServiceId(), GetServiceResponse.class);
        Assertions.assertEquals(serviceResponse.getName(), service.getServiceName());
    }

    @Order(7)
    @DisplayName("Негативный тест на получение сервиса по Id без токена")
    @TmsLink("643460")
    @Test
    public void getServiceByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(service.getServiceId());
    }

    @Order(40)
    @DisplayName("Копирование сервиса")
    @TmsLink("643465")
    @Test
    public void copyService() {
        String expectedName = service.getServiceName() + "-clone";
        productCatalogSteps.copyById(service.getServiceId());
        Assertions.assertTrue(productCatalogSteps.isExists(expectedName));
        productCatalogSteps.deleteByName(expectedName, GetServiceListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(expectedName));
    }

    @Order(41)
    @DisplayName("Негативный тест на копирование сервиса по Id без токена")
    @TmsLink("643507")
    @Test
    public void copyServiceWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(service.getServiceId());
    }

    @Order(50)
    @DisplayName("Обновление сервиса")
    @TmsLink("643510")
    @Test
    public void updateServiceDescription() {
        String expected = "Update description";
        productCatalogSteps.partialUpdateObject(service.getServiceId(), new JSONObject().put("description", expected));
        String actual = productCatalogSteps.getById(service.getServiceId(), GetServiceResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Order(51)
    @DisplayName("Негативный тест на обновление сервиса по Id без токена")
    @TmsLink("643515")
    @Test
    public void updateServiceByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(String.valueOf(service.getServiceId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(60)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @TmsLink("643516")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetImpl getServiceResponse = productCatalogSteps.getById(service.getServiceId(), GetServiceResponse.class);
        Assertions.assertNotNull(getServiceResponse.getGraphVersionCalculated());
    }

    @Order(70)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми символами в имени")
    @TmsLink("643518")
    @Test
    public void createServiceWithInvalidCharacters() {
        assertAll("Сервис создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("Имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("a&b&c")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject("")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                                .createJsonObject(" ")).assertStatus(400)
        );
    }

    @Order(80)
    @DisplayName("Создание сервиса со значением ключа graph_id равным null")
    @TmsLink("643519")
    @Test
    public void createServiceWithGraphIdNull() {
        CreateServiceResponse createServiceResponse = productCatalogSteps
                .createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServiceWithGraphIdNull.json")
                        .set("name", "create_service_with_graph_id_null_test_api")
                        .build()).extractAs(CreateServiceResponse.class);
        Assertions.assertNull(createServiceResponse.getGraphId(), "GraphId не равен null");
        productCatalogSteps.deleteById(createServiceResponse.getId());
    }

    @Order(89)
    @DisplayName("Обновление сервиса с указанием версии в граничных значениях")
    @TmsLink("643521")
    @Test
    public void updateServiceAndGetVersion() {
        Services services = Services.builder().serviceName("service_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api4"));
        currentVersion = productCatalogSteps.getById(services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api6"))
                .assertStatus(500);
    }

    @Order(90)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @TmsLink("643522")
    @Test
    public void createServiceWithInvalidGraphId() {
        assertAll("Создался сервис недопустимым graph_id",
                () -> productCatalogSteps.createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_not_exist_graph_id")
                        .set("graph_id", "dgdh-4565-dfgdf")
                        .build()).assertStatus(500),
                () -> productCatalogSteps.createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service2_with_not_exist_graph_id")
                        .set("graph_id", 56564)
                        .build()).assertStatus(500)
        );
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление сервиса без токена")
    @TmsLink("643526")
    @Test
    public void deleteServiceWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(String.valueOf(service.getServiceId()));
    }

    @Order(100)
    @DisplayName("Удаление сервиса")
    @TmsLink("643543")
    @MarkDelete
    @Test
    public void deleteService() {
        try (Services service = Services.builder()
                .serviceName("create_service_test_api")
                .build()
                .createObjectExclusiveAccess()) {
            service.deleteObject();
        }
    }
}
