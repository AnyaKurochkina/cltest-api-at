package api.cloud.productCatalog.service;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.OrgDirectionSteps.createOrgDirectionByName;
import static steps.productCatalog.ProductCatalogSteps.importObjects;
import static steps.productCatalog.ServiceSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceImportTest extends Tests {

    @DisplayName("Импорт сервиса")
    @TmsLink("643454")
    @Test
    public void importServiceTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importService.json");
        String serviceName = new JsonPath(data).get("Service.name");
        if (isServiceExists(serviceName)) {
            deleteServiceByName(serviceName);
        }
        ImportObject importObject = importService(Configure.RESOURCE_PATH + "/json/productCatalog/services/importService.json");
        assertEquals(serviceName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isServiceExists(serviceName));
        deleteServiceByName(serviceName);
        assertFalse(isServiceExists(serviceName));
    }

    @DisplayName("Импорт нескольких сервисов")
    @TmsLink("1523104")
    @Test
    public void importServicesTest() {
        String serviceName = "multi_import_service_test_api";
        if (isServiceExists(serviceName)) {
            deleteServiceByName(serviceName);
        }
        String serviceName2 = "multi_import_service2_test_api";
        if (isServiceExists(serviceName2)) {
            deleteServiceByName(serviceName2);
        }
        String orgId = createOrgDirectionByName(RandomStringUtils.randomAlphabetic(10).toLowerCase()).getId();
        Service service = createService(Service.builder()
                .name(serviceName)
                .directionId(orgId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Service.class);
        Service service2 = createService(Service.builder()
                .name(serviceName2)
                .directionId(orgId)
                .build()
                .toJson())
                .assertStatus(201)
                .extractAs(Service.class);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/services/multiImportProduct.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/services/multiImportProduct2.json";
        DataFileHelper.write(filePath, exportServiceById(service.getId()).toString());
        DataFileHelper.write(filePath2, exportServiceById(service2.getId()).toString());
        deleteServiceByName(serviceName);
        deleteServiceByName(serviceName2);
        importObjects("services", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isServiceExists(serviceName), "Сервис не существует");
        assertTrue(isServiceExists(serviceName2), "Сервис не существует");
        deleteServiceByName(serviceName);
        deleteServiceByName(serviceName2);
    }

    @DisplayName("Импорт сервиса c иконкой")
    @TmsLink("1085946")
    @Test
    public void importServiceWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/services/importServiceWithIcon.json");
        String name = new JsonPath(data).get("Service.name");
        if (isServiceExists(name)) {
            deleteServiceByName(name);
        }
        importService(Configure.RESOURCE_PATH + "/json/productCatalog/services/importServiceWithIcon.json");
        Service service = getServiceByName(name);
        assertFalse(service.getIconStoreId().isEmpty());
        assertFalse(service.getIconUrl().isEmpty());
        assertTrue(isServiceExists(name), "Сервис не существует");
        deleteServiceByName(name);
        assertFalse(isServiceExists(name), "Сервис существует");
    }
}
