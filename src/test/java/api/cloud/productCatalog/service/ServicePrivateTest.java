package api.cloud.productCatalog.service;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.OrgDirectionSteps.createOrgDirectionByName;
import static steps.productCatalog.ServicePrivateSteps.*;
import static steps.productCatalog.ServiceSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServicePrivateTest extends Tests {

    @DisplayName("Создание/Получение/Удаление сервиса в продуктовом каталоге c сервисным токеном")
    @TmsLinks({@TmsLink("1420337"), @TmsLink("1420341"), @TmsLink("1420344")})
    @Test
    public void servicePrivateByIdTest() {
        String serviceName = "service_private_test_api";
        if (isServiceExists(serviceName)) {
            deleteServiceByName(serviceName);
        }
        JSONObject jsonObject = Service.builder()
                .name(serviceName)
                .directionId(createOrgDirectionByName(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getId())
                .build()
                .toJson();
        Service service = createServicePrivate(jsonObject);
        String serviceId = service.getId();
        Service actualService = getServicePrivateById(serviceId);
        assertEquals(service, actualService);
        deleteServicePrivateById(serviceId);
    }

    @DisplayName("Обновление сервиса c сервисным токеном")
    @TmsLink("1420347")
    @Test
    public void updateServicePrivateTest() {
        String serviceInfo = "test";
        Service service = createService("service_update_private_test_api");
        partialUpdatePrivateService(service.getId(), new JSONObject().put("service_info", serviceInfo));
        Service updatedService = getServiceById(service.getId());
        assertEquals("1.0.1", updatedService.getVersion(), "Версии не совпадают");
        assertEquals(serviceInfo, updatedService.getServiceInfo());
    }

    @DisplayName("Создание/Получение/Удаление сервиса в продуктовом каталоге c сервисным токеном api/v2")
    @TmsLinks({@TmsLink("1420349"), @TmsLink("1420351"), @TmsLink("1420352")})
    @Test
    public void servicePrivateByNameTest() {
        String serviceName = "service_private_v2_test_api";
        if (isServiceExists(serviceName)) {
            deleteServiceByName(serviceName);
        }
        JSONObject jsonObject = Service.builder()
                .name(serviceName)
                .directionId(createOrgDirectionByName(RandomStringUtils.randomAlphabetic(5).toLowerCase()).getId())
                .build()
                .toJson();
        Service service = createServicePrivateV2(jsonObject);
        Service actualService = getServicePrivateByName(serviceName);
        assertEquals(service, actualService);
        deleteServicePrivateByName(serviceName);
    }

    @DisplayName("Обновление сервиса c сервисным токеном api/v2")
    @TmsLink("1420353")
    @Test
    public void updateServicePrivateByNameTest() {
        String serviceName = "service_update_private_by_name_test_api";
        String serviceInfo = "test";
        createService(serviceName);
        partialUpdateServicePrivateByName(serviceName, new JSONObject().put("service_info", serviceInfo));
        Service updatedService = getServicePrivateByName(serviceName);
        assertEquals("1.0.1", updatedService.getVersion(), "Версии не совпадают");
        assertEquals(serviceInfo, updatedService.getServiceInfo());
    }
}
