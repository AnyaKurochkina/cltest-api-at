package api.cloud.productCatalog.service;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Service;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceNegativeTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json");

    @DisplayName("Негативный тест на создание сервиса с существующим именем")
    @TmsLink("643458")
    @Test
    public void createServiceWithSameName() {
        String name = "create_service_with_same_name_test_api";
        Service service = Service.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(service.getServiceName()))
                .assertStatus(400);
    }

    @DisplayName("Негативный тест на получение сервиса по Id без токена")
    @TmsLink("643460")
    @Test
    public void getServiceByIdWithOutToken() {
        String name = "get_service_without_token_test_api";
        Service service = Service.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.getByIdWithOutToken(service.getServiceId());
    }

    @DisplayName("Негативный тест на копирование сервиса по Id без токена")
    @TmsLink("643507")
    @Test
    public void copyServiceWithOutToken() {
        Service service = Service.builder()
                .serviceName("copy_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.copyByIdWithOutToken(service.getServiceId());
    }

    @DisplayName("Негативный тест на обновление сервиса по Id без токена")
    @TmsLink("643515")
    @Test
    public void updateServiceByIdWithOutToken() {
        Service service = Service.builder()
                .serviceName("update_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(String.valueOf(service.getServiceId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание сервиса с недопустимыми символами в имени")
    @TmsLink("643518")
    @Test
    public void createServiceWithInvalidCharacters() {
        Service.builder().serviceName("NameWithUppercase").build().negativeCreateRequest(500);
        Service.builder().serviceName("nameWithUppercaseInMiddle").build().negativeCreateRequest(500);
        Service.builder().serviceName("имя").build().negativeCreateRequest(500);
        Service.builder().serviceName("Имя").build().negativeCreateRequest(500);
        Service.builder().serviceName("a&b&c").build().negativeCreateRequest(500);
        Service.builder().serviceName("").build().negativeCreateRequest(400);
        Service.builder().serviceName(" ").build().negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @TmsLink("643522")
    @Test
    public void createServiceWithInvalidGraphId() {
        Service.builder().serviceName("create_service_with_not_exist_graph_id")
                .graphId("dgdh-4565-dfgdf")
                .build()
                .negativeCreateRequest(400);
        Service.builder().serviceName("create_service_with_not_exist_graph_id")
                .graphId("create_service2_with_not_exist_graph_id")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на удаление сервиса без токена")
    @TmsLink("643526")
    @Test
    public void deleteServiceWithOutToken() {
        Service service = Service.builder()
                .serviceName("delete_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(String.valueOf(service.getServiceId()));
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в сервисах")
    @TmsLink("822050")
    public void setInvalidCurrentVersionService() {
        String name = "invalid_current_version_service_test_api";
        Service service = Service.builder()
                .serviceName(name)
                .title(name)
                .version("1.0.0")
                .build().createObject();
        String serviceId = service.getServiceId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "2")).assertStatus(500);
    }

    @DisplayName("Негативный тест на создание сервиса c пустым полем start_btn_label")
    @TmsLink("1095548")
    @Test
    public void createServiceWithOutStartBtnLabel() {
        JSONObject json = Service.builder()
                .serviceName("delete_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .startBtnLabel("")
                .build().init().toJson();
        Object str = steps.createProductObject(json)
                .assertStatus(400)
                .jsonPath().getList("start_btn_label").get(0);
        assertEquals("Это поле не может быть пустым.", str);
    }
}
