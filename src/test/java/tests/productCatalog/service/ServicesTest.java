package tests.productCatalog.service;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServicesTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json");

    @DisplayName("Создание сервиса в продуктовом каталоге")
    @TmsLink("643448")
    @Test
    public void createService() {
        String name = "create_service_test_api";
        Services service = Services.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        GetImpl getService = steps.getById(service.getServiceId(), GetServiceResponse.class);
        assertEquals(name, getService.getName());
    }

    @DisplayName("Проверка существования сервиса по имени")
    @TmsLink("643453")
    @Test
    public void checkServiceExists() {
        String name = "exist_service_test_api";
        Services.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(name));
        Assertions.assertFalse(steps.isExists("not_exist_name"));
    }

    @DisplayName("Импорт сервиса")
    @TmsLink("643454")
    @Test
    public void importService() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importService.json");
        String serviceName = new JsonPath(data).get("Service.json.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        Assertions.assertTrue(steps.isExists(serviceName));
        steps.deleteByName(serviceName, GetServiceListResponse.class);
        Assertions.assertFalse(steps.isExists(serviceName));
    }

    @DisplayName("Получение сервиса по Id")
    @TmsLink("643459")
    @Test
    public void getServiceById() {
        String name = "get_service_by_id_test_api";
        Services service = Services.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        GetImpl serviceResponse = steps.getById(service.getServiceId(), GetServiceResponse.class);
        Assertions.assertEquals(name, serviceResponse.getName());
    }

    @DisplayName("Проверка сортировки по дате создания в сервисах")
    @TmsLink("738676")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetServiceListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в сервисах")
    @TmsLink("738680")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetServiceListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в сервисах")
    @TmsLink("738683")
    @Test
    public void checkAccessWithPublicToken() {
        String name = "check_access_with_public_token_service_test_api";
        Services service = Services.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(service.getServiceName()).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(service.getServiceId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(service.getServiceId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(service.getServiceId()).assertStatus(403);
    }

    @DisplayName("Удаление сервиса со статусом is_published=true")
    @TmsLink("738684")
    @Test
    public void deleteIsPublishedService() {
        Services serviceIsPublished = Services.builder().serviceName("create_service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        String serviceId = serviceIsPublished.getServiceId();
        Response deleteResponse = steps.getDeleteObjectResponse(serviceId)
                .assertStatus(200);
        steps.partialUpdateObject(serviceId, new JSONObject().put("is_published", false));
        assertEquals(deleteResponse.jsonPath().get("error"), "Deletion not allowed (is_published=True)");
    }

    @DisplayName("Проверка независимого от версии поля is_published в сервисах")
    @TmsLink("738686")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        Services serv = Services.builder()
                .serviceName("services_api_test")
                .version("1.0.0")
                .isPublished(true)
                .build()
                .createObject();
        String version = serv.getVersion();
        Response response = steps.partialUpdateObject(serv.getServiceId(), new JSONObject().put("is_published", false));
        String newVersion = response.jsonPath().get("version");
        assertEquals(version, newVersion);
    }

    @DisplayName("Копирование сервиса")
    @TmsLink("643465")
    @Test
    public void copyService() {
        String name = "copy_service_test_api";
        Services service = Services.builder()
                .serviceName(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        String expectedName = name + "-clone";
        steps.copyById(service.getServiceId());
        Assertions.assertTrue(steps.isExists(expectedName));
        steps.deleteByName(expectedName, GetServiceListResponse.class);
        Assertions.assertFalse(steps.isExists(expectedName));
    }

    @DisplayName("Обновление сервиса")
    @TmsLink("643510")
    @Test
    public void updateServiceDescription() {
        Services service = Services.builder()
                .serviceName("update_service_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        String serviceId = service.getServiceId();
        String expected = "Update description";
        steps.partialUpdateObject(serviceId, new JSONObject().put("description", expected));
        String actual = steps.getById(serviceId, GetServiceResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в сервисах")
    @TmsLink("643516")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        Services service = Services.builder()
                .serviceName("get_graph_version_calculated_service_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        GetImpl getServiceResponse = steps.getById(service.getServiceId(), GetServiceResponse.class);
        Assertions.assertNotNull(getServiceResponse.getGraphVersionCalculated());
    }

    @DisplayName("Создание сервиса со значением ключа graph_id равным null")
    @TmsLink("643519")
    @Test
    public void createServiceWithGraphIdNull() {
        CreateServiceResponse createServiceResponse = steps
                .createProductObject(JsonHelper.getJsonTemplate("productCatalog/services/createServiceWithGraphIdNull.json")
                        .set("name", "create_service_with_graph_id_null_test_api")
                        .build()).extractAs(CreateServiceResponse.class);
        Assertions.assertNull(createServiceResponse.getGraphId(), "GraphId не равен null");
        steps.deleteById(createServiceResponse.getId());
    }

    @DisplayName("Обновление сервиса с указанием версии в граничных значениях")
    @TmsLink("643521")
    @Test
    public void updateServiceAndGetVersion() {
        Services services = Services.builder().serviceName("service_version_test_api").version("1.0.999").build().createObject();
        steps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api2"));
        String currentVersion = steps.getById(services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api3")
                .put("version", "1.999.999"));
        steps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api4"));
        currentVersion = steps.getById(services.getServiceId(), GetServiceResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api5")
                .put("version", "999.999.999"));
        steps.partialUpdateObject(services.getServiceId(), new JSONObject().put("name", "service_version_test_api6"))
                .assertStatus(500);
    }

    @DisplayName("Сортировка сервисов по статусу")
    @TmsLink("811054")
    @Test
    public void orderingByStatus() {
        List<ItemImpl> list = steps.orderingByStatus(GetServiceListResponse.class).getItemsList();
        boolean result = false;
        int count = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            ListItem item = (ListItem) list.get(i);
            ListItem nextItem = (ListItem) list.get(i + 1);
            if (item.getIsPublished().equals(nextItem.getIsPublished())) {
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

    @DisplayName("Удаление сервиса")
    @TmsLink("643543")
    @Test
    public void deleteService() {
        Services service = Services.builder()
                .serviceName("delete_service_test_api")
                .title("title_service_test_api")
                .description("at_tests")
                .build()
                .createObject();
        service.deleteObject();
    }

    @Test
    @DisplayName("Присвоение значения current_version из списка version_list в сервисах")
    @TmsLink("")
    public void setCurrentVersionService() {
        String serviceName = "set_current_version_service_test_api";
        Services service = Services.builder()
                .serviceName(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .build()
                .createObject();
        String serviceId = service.getServiceId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("title", "update_title"));
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "1.0.1"));
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertEquals("1.0.1", getService.getCurrentVersion());
        assertTrue(getService.getVersionList().contains(getService.getCurrentVersion()));
    }

    @Test
    @DisplayName("Получение сервиса версии указанной в current_version")
    @TmsLink("")
    public void getCurrentVersionService() {
        String serviceName = "create_current_version_service_test_api";
        Services service = Services.builder()
                .serviceName(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .build()
                .createObject();
        String serviceId = service.getServiceId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("title", "update_title"));
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "1.0.0"));
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertEquals("1.0.0", getService.getCurrentVersion());
        assertEquals(serviceName, getService.getTitle());
    }
}
