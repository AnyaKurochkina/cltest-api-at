package tests.productCatalog;

import core.helper.Configure;
import core.helper.Deleted;
import core.helper.JsonHelper;
import httpModels.productCatalog.Service.copyService.response.CopyServiceResponse;
import httpModels.productCatalog.Service.getService.response.GetServiceResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Services;
import org.junit.jupiter.api.*;
import steps.productCatalog.ServiceSteps;
import tests.Tests;


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
                .serviceName("service_test")
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
        Assertions.assertFalse(serviceSteps.isServiceExist("NotExistName"));
    }

    @Order(4)
    @DisplayName("Импорт сервиса")
    @Test
    public void importService() {
        String data = new JsonHelper().getStringFromFile("/productCatalog/services/importService.json");
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
    @DisplayName("Получение сервиса по ID")
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

    @Order(100)
    @Test
    @DisplayName("Удаление сервиса")
    @Deleted
    public void deleteService() {
        try (Services service = Services.builder()
                .serviceName("service_test")
                .build()
                .createObjectExclusiveAccess()) {
            service.deleteObject();
        }
    }
}
