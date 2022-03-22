package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.service.createService.response.CreateServiceResponse;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import httpModels.productCatalog.service.getServiceList.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Services;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
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
                .title("title_service_test_api")
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
    @TmsLink("682758")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetServiceListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @Order(3)
    @DisplayName("Проверка существования сервиса по имени")
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

    @Order(8)
    @DisplayName("Получение сервиса по title")
    @TmsLink("738673")
    @Test
    public void getServiceByTitle() {
        GetServiceListResponse list = (GetServiceListResponse) productCatalogSteps.getObjectByTitle(service.getTitle(), GetServiceListResponse.class);
        ListItem item = (ListItem) list.getItemsList().get(0);
        String title = item.getTitle();
        assertEquals(service.getTitle(), title, "Title не совпадают");
    }

    @Order(9)
    @DisplayName("Проверка сортировки по дате создания в сервисах")
    @TmsLink("738676")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByCreateData(GetServiceListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(10)
    @DisplayName("Проверка сортировки по дате обновления в сервисах")
    @TmsLink("738680")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByUpDateData(GetServiceListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(11)
    @DisplayName("Проверка доступа для методов с публичным ключом в сервисах")
    @TmsLink("738683")
    @Test
    public void checkAccessWithPublicToken() {
        productCatalogSteps.getObjectByNameWithPublicToken(service.getServiceName()).assertStatus(200);
        productCatalogSteps.createProductObjectWithPublicToken(productCatalogSteps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.partialUpdateObjectWithPublicToken(service.getServiceId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        productCatalogSteps.putObjectByIdWithPublicToken(service.getServiceId(), productCatalogSteps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.deleteObjectWithPublicToken(service.getServiceId()).assertStatus(403);
    }

    @Order(12)
    @DisplayName("Удаление сервиса со статусом is_published=true")
    @TmsLink("738684")
    @Test
    public void deleteIsPublishedService() {
        Services serviceIsPublished = Services.builder().serviceName("create_service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        String serviceId = serviceIsPublished.getServiceId();
        Response deleteResponse = productCatalogSteps.getDeleteObjectResponse(serviceId)
                .assertStatus(200);
        productCatalogSteps.partialUpdateObject(serviceId, new JSONObject().put("is_published", false));
        assertEquals(deleteResponse.jsonPath().get("error"), "Deletion not allowed (is_published=True)");
    }

    @Order(30)
    @DisplayName("Проверка независимых от версии параметров в сервисах")
    @TmsLink("738686")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        Services serv = Services.builder().serviceName("services_api_test").version("1.0.0").isPublished(true).build().createObject();
        String version = serv.getVersion();
        Response response = productCatalogSteps.partialUpdateObject(serv.getServiceId(), new JSONObject().put("is_published", false));
        String newVersion = response.jsonPath().get("version");
        assertEquals(version, newVersion);
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
    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в сервисах")
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

    @Order(98)
    @DisplayName("Получение списка сервисов по фильтру is_published")
    @TmsLink("")
    @Test
    public void getServiceListByPublished() {
        Services.builder()
                .serviceName("service_is_published_test_api")
                .title("title_service_is_published_test_api")
                .description("service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        List<ItemImpl> serviceList = productCatalogSteps.getProductObjectList(GetServiceListResponse.class, "?is_published=true");
        for (ItemImpl item : serviceList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getIsPublished());
        }
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
        service.deleteObject();
    }
}
