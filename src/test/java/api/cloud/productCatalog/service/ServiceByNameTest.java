package api.cloud.productCatalog.service;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ServiceSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceByNameTest extends Tests {

    @DisplayName("Получение сервиса по имени")
    @TmsLink("1361347")
    @Test
    public void getServiceByNameTest() {
        String serviceName = "get_service_by_name_example_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .build()
                .createObject();
        Service getService = getServiceByName(serviceName);
        assertEquals(service, getService);
    }

    @DisplayName("Обновление сервиса по имени")
    @TmsLink("1361350")
    @Test
    public void patchServiceByNameTest() {
        String serviceName = "service_patch_by_name_test_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .serviceInfo("test service info")
                .build()
                .createObject();
        partialUpdateServiceByName(serviceName, new JSONObject().put("service_info", "update"));
        assertEquals("1.0.1", getServiceById(service.getId()).getVersion(), "Версии не совпадают");
    }

    @Test
    @DisplayName("Удаление сервиса по имени")
    @TmsLink("1361351")
    public void deleteServiceByNameTest() {
        String service = "service_delete_by_name_test_api";
        JSONObject jsonObject = Service.builder()
                .name(service)
                .title(service)
                .build()
                .init()
                .toJson();
        createService(jsonObject).assertStatus(201);
        deleteServiceByName(service);
        assertFalse(isServiceExists(service));
    }

    @DisplayName("Копирование сервиса по имени")
    @TmsLink("1361356")
    @Test
    public void copyServiceByNameTest() {
        String serviceName = "clone_service_by_name_test_api";
        Service.builder()
                .name(serviceName)
                .title(serviceName)
                .build()
                .createObject();
        Service cloneService = copyServiceByName(serviceName);
        String cloneName = cloneService.getName();
        assertTrue(isServiceExists(cloneName), "Сервис не существует");
        deleteServiceByName(cloneName);
        assertFalse(isServiceExists(cloneName), "Сервис существует");
    }

    @Test
    @DisplayName("Загрузка сервиса в GitLab по имени")
    @TmsLink("1361359")
    public void dumpToGitlabServiceByNameTest() {
        String serviceName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Service service = Service.builder()
                .name(serviceName)
                .title(serviceName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "service_" + serviceName + "_" + service.getVersion();
        Response response = dumpServiceToGitByName(serviceName);
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @DisplayName("Экспорт сервиса по имени")
    @TmsLink("1361360")
    @Test
    public void exportServiceByNameTest() {
        String serviceName = "service_export_by_name_test_api";
        Service.builder()
                .name(serviceName)
                .title(serviceName)
                .build()
                .createObject();
        exportServiceByName(serviceName);
    }
}
