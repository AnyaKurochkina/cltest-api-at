package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Service.copyService.response.CopyServiceResponse;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.Service.getService.response.GetServiceResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Services;
import org.junit.jupiter.api.*;
import steps.productCatalog.ServiceSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: сервисы")
public class ServicesTest extends Tests {

    Services service;
    ServiceSteps serviceSteps = new ServiceSteps();

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
        Assertions.assertTrue(serviceSteps.getServicesList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования продукта по имени")
    @Test
    public void checkServiceExists() {
        Assertions.assertTrue(serviceSteps.isServiceExist(service.getServiceName()));
        Assertions.assertFalse(serviceSteps.isServiceExist("not_exist_name"));
    }

    @Order(4)
    @DisplayName("Импорт сервиса")
    @Test
    public void importService() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importService.json");
        String serviceName = new JsonPath(data).get("Service.json.name");
        serviceSteps.importService(Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        Assertions.assertTrue(serviceSteps.isServiceExist(serviceName));
        serviceSteps.deleteServiceById(serviceSteps.getServiceIdByName(serviceName));
        Assertions.assertFalse(serviceSteps.isServiceExist(serviceName));
    }

    @Order(5)
    @DisplayName("Негативный тест на создание сервиса с существующим именем")
    @Test
    public void createServiceWithSameName() {
        serviceSteps.createService(serviceSteps.createJsonObject(service.getServiceName())).assertStatus(400);
    }

    @Order(6)
    @DisplayName("Получение сервиса по Id")
    @Test
    public void getServiceById() {
        GetServiceResponse serviceResponse = serviceSteps.getServiceById(service.getServiceId());
        Assertions.assertEquals(serviceResponse.getName(), service.getServiceName());
    }

    @Order(7)
    @DisplayName("Копирование сервиса")
    @Test
    public void copyService() {
        String expectedName = service.getServiceName() + "-clone";
        CopyServiceResponse copyServiceResponse = serviceSteps.copyServiceById(service.getServiceId());
        serviceSteps.deleteServiceById(copyServiceResponse.getId());
        Assertions.assertEquals(expectedName, copyServiceResponse.getName());
    }

    @Order(8)
    @DisplayName("Обновление сервиса")
    @Test
    public void updateServiceDescription() {
        serviceSteps.updateServiceById(service.getServiceId());
    }

    @Order(9)
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        GetServiceResponse getServiceResponse = serviceSteps.getServiceById(service.getServiceId());
        Assertions.assertNotNull(getServiceResponse.getGraphVersionCalculated());
    }

    @Order(10)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми символами в имени")
    @Test
    public void createServiceWithInvalidCharacters() {
        assertAll("Сервис создался с недопустимым именем",
                () -> serviceSteps.createService(serviceSteps.createJsonObject("NameWithUppercase")).assertStatus(400),
                () -> serviceSteps.createService(serviceSteps.createJsonObject("nameWithUppercaseInMiddle")).assertStatus(400),
                () -> serviceSteps.createService(serviceSteps.createJsonObject("имя")).assertStatus(400),
                () -> serviceSteps.createService(serviceSteps.createJsonObject("Имя")).assertStatus(400),
                () -> serviceSteps.createService(serviceSteps.createJsonObject("a&b&c")).assertStatus(400)
        );
    }

    @Order(11)
    @DisplayName("Создание сервиса со значением ключа graph_id равным null")
    @Test
    public void createServiceWithGraphIdNull() {
        CreateServiceResponse createServiceResponse = serviceSteps
                .createService(JsonHelper.getJsonTemplate("productCatalog/services/createServiceWithGraphIdNull.json")
                        .set("name", "create_service_with_graph_id_null_test_api")
                        .build()).extractAs(CreateServiceResponse.class);
        Assertions.assertNull(createServiceResponse.getGraphId(), "GraphId не равен null");
        serviceSteps.deleteServiceById(createServiceResponse.getId());
    }

    @Order(12)
    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @Test
    public void createServiceWithInvalidGraphId() {
        assertAll("Создался сервис недопустимым graph_id",
                () -> serviceSteps.createService(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_empty_graph_id")
                        .set("graph_id", "")
                        .build()).assertStatus(500),
                () -> serviceSteps.createService(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_not_exist_graph_id")
                        .set("graph_id", "dgdh-4565-dfgdf")
                        .build()).assertStatus(500),
                () -> serviceSteps.createService(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_not_exist_graph_id")
                        .set("graph_id", 56564)
                        .build()).assertStatus(500)
        );
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
