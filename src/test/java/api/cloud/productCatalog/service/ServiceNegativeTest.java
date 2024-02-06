package api.cloud.productCatalog.service;

import api.Tests;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.ProductCatalogSteps;

import java.util.stream.Stream;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ServiceSteps.createService;

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
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(service.getName()))
                .assertStatus(400);
    }

    @DisplayName("Негативный тест на получение сервиса по Id без токена")
    @TmsLink("643460")
    @Test
    public void getServiceByIdWithOutToken() {
        String name = "get_service_without_token_test_api";
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.getByIdWithOutToken(service.getId());
    }

    @DisplayName("Негативный тест на копирование сервиса по Id без токена")
    @TmsLink("643507")
    @Test
    public void copyServiceWithOutToken() {
        Service service = Service.builder()
                .name("copy_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.copyByIdWithOutToken(service.getId());
    }

    @DisplayName("Негативный тест на обновление сервиса по Id без токена")
    @TmsLink("643515")
    @Test
    public void updateServiceByIdWithOutToken() {
        Service service = Service.builder()
                .name("update_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(String.valueOf(service.getId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание сервиса с недопустимыми символами в имени")
    @TmsLink("643518")
    @ParameterizedTest
    @MethodSource("testData")
    public void createServiceWithInvalidCharacters(String name, String message) {
        AssertResponse.run(() -> createService(name)).status(400)
                .responseContains(format(message, name));
    }

    static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("NameWithUppercase", "Cannot instantiate (Service) named ({})"),
                Arguments.of("nameWithUppercaseInMiddle", "Cannot instantiate (Service) named ({})"),
                Arguments.of("Имя", "Cannot instantiate (Service) named ({})"),
                Arguments.of("имя", "Cannot instantiate (Service) named ({})"),
                Arguments.of("a&b&c", "Cannot instantiate (Service) named ({})"),
                Arguments.of("", "Это поле не может быть пустым."),
                Arguments.of(" ", "Это поле не может быть пустым.")
        );
    }

    @DisplayName("Негативный тест на создание сервиса с недопустимыми graph_id")
    @TmsLink("643522")
    @Test
    public void createServiceWithInvalidGraphId() {
        Service serviceModel = Service.builder()
                .name("create_service_with_not_exist_graph_id")
                .graphId("dgdh-4565-dfgdf")
                .build();
        AssertResponse.run(() -> createService(serviceModel.toJson())).status(400);
        Service serviceModel2 = Service.builder().name("create_service_with_not_exist_graph_id")
                .graphId("create_service2_with_not_exist_graph_id")
                .build();
        AssertResponse.run(() -> createService(serviceModel2.toJson())).status(400);

    }

    @DisplayName("Негативный тест на удаление сервиса без токена")
    @TmsLink("643526")
    @Test
    public void deleteServiceWithOutToken() {
        Service service = Service.builder()
                .name("delete_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(String.valueOf(service.getId()));
    }

    @Test
    @DisplayName("Негативный тест на передачу невалидного значения current_version в сервисах")
    @TmsLink("822050")
    public void setInvalidCurrentVersionService() {
        String name = "invalid_current_version_service_test_api";
        Service service = Service.builder()
                .name(name)
                .title(name)
                .version("1.0.0")
                .build().createObject();
        String serviceId = service.getId();
        String message = steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "2")).assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals("You must specify version in pattern like \"{num}. | {num}.{num}.\"", message);
    }

    @DisplayName("Негативный тест на создание сервиса c пустым полем start_btn_label")
    @TmsLink("1095548")
    @Test
    public void createServiceWithOutStartBtnLabel() {
        JSONObject json = Service.builder()
                .name("delete_service_with_out_token_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .startBtnLabel("")
                .build().toJson();
        String str = steps.createProductObject(json)
                .assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        assertEquals("\"start_btn_label\": Это поле не может быть пустым.", str);
    }
}
