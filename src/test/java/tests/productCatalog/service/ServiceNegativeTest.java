package tests.productCatalog.service;

import core.helper.Configure;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Services;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceNegativeTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json", Configure.ProductCatalogURL);

    @DisplayName("Негативный тест на создание сервиса с существующим именем")
    @TmsLink("643458")
    @Test
    public void createServiceWithSameName() {
        String name = "create_service_with_same_name_test_api";
        Services service = Services.builder()
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
        Services service = Services.builder()
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
        Services service = Services.builder()
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
        Services service = Services.builder()
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
        assertAll("Сервис создался с недопустимым именем",
                () -> steps.createProductObject(steps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("a&b&c")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("")).assertStatus(400),
                () -> steps.createProductObject(steps
                        .createJsonObject(" ")).assertStatus(400)
        );
    }

    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @TmsLink("643522")
    @Test
    public void createServiceWithInvalidGraphId() {
        assertAll("Создался сервис недопустимым graph_id",
                () -> steps.createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service_with_not_exist_graph_id")
                        .set("graph_id", "dgdh-4565-dfgdf")
                        .build()).assertStatus(500),
                () -> steps.createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServices.json")
                        .set("name", "create_service2_with_not_exist_graph_id")
                        .set("graph_id", 56564)
                        .build()).assertStatus(500)
        );
    }

    @DisplayName("Негативный тест на удаление сервиса без токена")
    @TmsLink("643526")
    @Test
    public void deleteServiceWithOutToken() {
        Services service = Services.builder()
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
        Services service = Services.builder()
                .serviceName(name)
                .title(name)
                .version("1.0.0")
                .build().createObject();
        String serviceId = service.getServiceId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "2")).assertStatus(500);
    }
}
