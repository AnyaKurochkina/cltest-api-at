package tests.productCatalog;

import core.helper.Deleted;
import core.helper.JsonHelper;
import httpModels.productCatalog.Service.copyService.response.CopyServiceResponse;
import httpModels.productCatalog.Service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.Service.getService.response.GetServiceResponse;
import io.qameta.allure.Feature;
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
    @DisplayName("Негативный тест на создание двух сервисов с одинаковым именем")
    @Test
    public void createServiceWithSameName() {
        CreateServiceResponse serviceResponse = serviceSteps.createService(new JsonHelper()
                .getJsonTemplate("productCatalog/services/createServices.json")
                .build());
    }

    @Order(3)
    @DisplayName("Получение сервиса по ID")
    @Test
    public void getServiceById() {
        String id = service.getServiceId();
        GetServiceResponse serviceResponse = serviceSteps.getServiceById(id);
        Assertions.assertEquals(id, serviceResponse.getId());
    }

    @Order(4)
    @DisplayName("Копирование сервиса")
    @Test
    public void copyService() {
        String expectedName = service.getServiceName() + "-clone";
        CopyServiceResponse copyServiceResponse = serviceSteps.copyServiceById(service.getServiceId());
        serviceSteps.deleteServiceById(copyServiceResponse.getId());
        Assertions.assertEquals(expectedName, copyServiceResponse.getName());
    }

    @Order(5)
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
