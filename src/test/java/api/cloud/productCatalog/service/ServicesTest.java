package api.cloud.productCatalog.service;

import api.Tests;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import httpModels.productCatalog.service.getServiceList.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import steps.productCatalog.ServiceSteps;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ServiceSteps.*;

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
    public void createServiceTest() {
        Service service = createService("create_service_test_api");
        Service actualService = getServiceById(service.getId());
        assertEquals(service, actualService);
    }

    @DisplayName("Создание сервиса в продуктовом каталоге с иконкой")
    @TmsLink("1082639")
    @Test
    public void createServiceWithIcon() {
        Icon icon = Icon.builder()
                .name("service_icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String serviceName = "create_service_with_icon_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetServiceResponse actualService = (GetServiceResponse) steps.getById(service.getId(), GetServiceResponse.class);
        assertFalse(actualService.getIconStoreId().isEmpty());
        assertFalse(actualService.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких сервисов в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1082663")
    @Test
    public void createSeveralServiceWithSameIcon() {
        Icon icon = Icon.builder()
                .name("service_icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String serviceName = "create_first_service_with_same_icon_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();

        Service secondService = Service.builder()
                .name("create_second_service_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetServiceResponse actualFirstService = (GetServiceResponse) steps.getById(service.getId(), GetServiceResponse.class);
        GetServiceResponse actualSecondService = (GetServiceResponse) steps.getById(secondService.getId(), GetServiceResponse.class);
        assertEquals(actualFirstService.getIconUrl(), actualSecondService.getIconUrl());
        assertEquals(actualFirstService.getIconStoreId(), actualSecondService.getIconStoreId());
    }

    @DisplayName("Проверка существования сервиса по имени")
    @TmsLink("643453")
    @Test
    public void checkServiceExists() {
        String name = "exist_service_test_api";
        Service.builder()
                .name(name)
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
        String serviceName = new JsonPath(data).get("Service.name");
        if (steps.isExists(serviceName)) {
            steps.deleteByName(serviceName, GetServiceListResponse.class);
        }
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        Assertions.assertTrue(steps.isExists(serviceName));
        steps.deleteByName(serviceName, GetServiceListResponse.class);
        Assertions.assertFalse(steps.isExists(serviceName));
    }

    @DisplayName("Импорт сервиса c иконкой")
    @TmsLink("1085946")
    @Test
    public void importServiceWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importServiceWithIcon.json");
        String name = new JsonPath(data).get("Service.name");
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetServiceListResponse.class);
        }
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/services/importServiceWithIcon.json");
        String id = steps.getProductObjectIdByNameWithMultiSearch(name, GetServiceListResponse.class);
        GetServiceResponse service = (GetServiceResponse) steps.getById(id, GetServiceResponse.class);
        assertFalse(service.getIconStoreId().isEmpty());
        assertFalse(service.getIconUrl().isEmpty());
        assertTrue(steps.isExists(name), "Сервис не существует");
        steps.deleteByName(name, GetServiceListResponse.class);
        assertFalse(steps.isExists(name), "Сервис существует");
    }

    @DisplayName("Получение сервиса по Id")
    @TmsLink("643459")
    @Test
    public void getServiceByIdTest() {
        String name = "get_service_by_id_test_api";
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        GetImpl serviceResponse = steps.getById(service.getId(), GetServiceResponse.class);
        Assertions.assertEquals(name, serviceResponse.getName());
    }

    @DisplayName("Получение сервиса по Id и фильтру with_version_fields=true")
    @TmsLink("1284668")
    @Test
    public void getServiceByIdAndVersionFieldsTest() {
        String name = "get_service_by_id_and_version_fields_test_api";
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        Service serviceWithFields = getServiceByIdAndFilter(service.getId(), "with_version_fields=true");
        assertFalse(serviceWithFields.getVersionFields().isEmpty());
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
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(service.getName()).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(service.getId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(service.getId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(service.getId()).assertStatus(403);
    }

    @DisplayName("Удаление сервиса со статусом is_published=true")
    @TmsLink("738684")
    @Test
    public void deleteIsPublishedService() {
        String errorText = "Deletion not allowed (is_published=True)";
        Service serviceIsPublished = Service.builder()
                .name("create_service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        String serviceId = serviceIsPublished.getId();
        String errorMessage = steps.getDeleteObjectResponse(serviceId).assertStatus(403).extractAs(ErrorMessage.class).getMessage();
        steps.partialUpdateObject(serviceId, new JSONObject().put("is_published", false));
        assertEquals(errorText, errorMessage);
    }

    @DisplayName("Проверка независимого от версии поля is_published в сервисах")
    @TmsLink("738686")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        Service serv = Service.builder()
                .name("services_api_test")
                .version("1.0.0")
                .isPublished(true)
                .build()
                .createObject();
        String version = serv.getVersion();
        Response response = steps.partialUpdateObject(serv.getId(), new JSONObject().put("is_published", false));
        String newVersion = response.jsonPath().get("version");
        assertEquals(version, newVersion);
    }

    @DisplayName("Копирование сервиса")
    @TmsLink("643465")
    @Test
    public void copyService() {
        String name = "copy_service_test_api";
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        String expectedName = name + "-clone";
        steps.copyById(service.getId());
        Assertions.assertTrue(steps.isExists(expectedName));
        steps.deleteByName(expectedName, GetServiceListResponse.class);
        Assertions.assertFalse(steps.isExists(expectedName));
    }

    @DisplayName("Обновление сервиса")
    @TmsLink("643510")
    @Test
    public void updateServiceDescription() {
        Service service = Service.builder()
                .name("update_service_test_api")
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        String serviceId = service.getId();
        String expected = "Update description";
        steps.partialUpdateObject(serviceId, new JSONObject().put("description", expected));
        String actual = steps.getById(serviceId, GetServiceResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в сервисах")
    @TmsLink("643516")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        Service service = Service.builder()
                .name("get_graph_version_calculated_service_test_api")
                .title("title_service_test_api")
                .graphId(createGraph(RandomStringUtils.randomAlphabetic(6).toLowerCase()).getGraphId())
                .description("ServiceForAT")
                .build()
                .createObject();
        Service getService = ServiceSteps.getServiceById(service.getId());
        assertEquals("1.0.0", getService.getGraphVersionCalculated());
    }

    @DisplayName("Создание сервиса со значением ключа graph_id равным null")
    @TmsLink("643519")
    @Test
    public void createServiceWithGraphIdNull() {
        Service service = createService("create_service_with_graph_id_null_test_api");
        assertNull(service.getGraphId(), "GraphId не равен null");
    }

    @DisplayName("Обновление сервиса с указанием версии в граничных значениях")
    @TmsLink("643521")
    @Test
    public void updateServiceAndGetVersion() {
        Service services = Service.builder().
                name("service_version_test_api")
                .version("1.0.999")
                .serviceInfo("test service info")
                .build()
                .createObject();
        steps.partialUpdateObject(services.getId(), new JSONObject().put("service_info", "service_version_test_api2"));
        String currentVersion = steps.getById(services.getId(), GetServiceResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(services.getId(), new JSONObject().put("service_info", "service_version_test_api3")
                .put("version", "1.999.999"));
        steps.partialUpdateObject(services.getId(), new JSONObject().put("service_info", "service_version_test_api4"));
        currentVersion = steps.getById(services.getId(), GetServiceResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(services.getId(), new JSONObject().put("service_info", "service_version_test_api5")
                .put("version", "999.999.999"));
        String message = steps.partialUpdateObject(services.getId(), new JSONObject().put("service_info", "service_version_test_api6"))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Version counter full [999, 999, 999]", message);
    }

    @DisplayName("Сортировка сервисов по статусу")
    @TmsLink("811054")
    //todo логику вынести
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
        Service service = Service.builder()
                .name("delete_service_test_api")
                .title("title_service_test_api")
                .description("at_tests")
                .build()
                .createObject();
        deleteServiceById(service.getId());
    }

    @Test
    @DisplayName("Присвоение значения current_version из списка version_list в сервисах")
    @TmsLink("856539")
    public void setCurrentVersionService() {
        String serviceName = "set_current_version_service_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .serviceInfo("test")
                .build()
                .createObject();
        String serviceId = service.getId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("service_info", "update_service_info"));
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "1.0.1"));
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertEquals("1.0.1", getService.getCurrentVersion());
        assertTrue(getService.getVersionList().contains(getService.getCurrentVersion()));
    }

    @Test
    @DisplayName("Получение сервиса версии указанной в current_version")
    @TmsLink("856540")
    public void getCurrentVersionService() {
        String serviceName = "create_current_version_service_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .serviceInfo("test")
                .build()
                .createObject();
        String serviceId = service.getId();
        steps.partialUpdateObject(serviceId, new JSONObject().put("service_info", "update_service_info"));
        steps.partialUpdateObject(serviceId, new JSONObject().put("current_version", "1.0.0"));
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertEquals("1.0.0", getService.getCurrentVersion());
        assertEquals(service.getServiceInfo(), getService.getServiceInfo());
    }

    @Test
    @DisplayName("Получение значения поля auto_open_results в сервисах")
    @TmsLink("856542")
    public void getAutoOpenResultService() {
        String serviceName = "get_auto_open_result_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .autoOpenResults(false)
                .build()
                .createObject();
        String serviceId = service.getId();
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertFalse(getService.getAutoOpenResults());
        steps.partialUpdateObject(serviceId, new JSONObject().put("auto_open_results", true));
        GetServiceResponse getUpdatedService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertTrue(getUpdatedService.getAutoOpenResults());
    }

    @Test
    @DisplayName("Получение значения поля direction_title в сервисах")
    @TmsLink("856543")
    public void getDirectionTitleService() {
        String directionTitle = "direction_title_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name("direction_title_test_api")
                .title(directionTitle)
                .build()
                .createObject();
        String serviceName = "get_direction_title_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .directionId(orgDirection.getId())
                .build()
                .createObject();
        String serviceId = service.getId();
        GetServiceResponse getService = (GetServiceResponse) steps.getById(serviceId, GetServiceResponse.class);
        assertEquals(directionTitle, getService.getDirectionTitle());
    }

    @Test
    @DisplayName("Загрузка Service в GitLab")
    @TmsLink("975412")
    public void dumpToGitlabService() {
        String serviceName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "service_" + serviceName + "_" + service.getVersion();
        Response response = steps.dumpToBitbucket(service.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка Service из GitLab")
    @TmsLink("1029279")
    public void loadFromGitlabService() {
        String serviceName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .build()
                .init().toJson();
        GetServiceResponse service = steps.createProductObject(jsonObject).extractAs(GetServiceResponse.class);
        Response response = steps.dumpToBitbucket(service.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(serviceName, GetServiceListResponse.class);
        String path = "service_" + serviceName + "_" + service.getVersion();
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(serviceName));
        steps.deleteByName(serviceName, GetServiceListResponse.class);
        assertFalse(steps.isExists(serviceName));
    }

    @DisplayName("Проверка дефолтного значения start_btn_label")
    @TmsLink("1095542")
    @Test
    public void createServiceWithStartBtnNull() {
        String name = "create_service_with_start_btn_null";
        Service service = Service.builder()
                .name(name)
                .title("title_service_test_api")
                .description("ServiceForAT")
                .build()
                .createObject();
        GetServiceResponse getService = (GetServiceResponse) steps.getById(service.getId(), GetServiceResponse.class);
        assertEquals("Запуск", getService.getStartBtnLabel());
    }
}
