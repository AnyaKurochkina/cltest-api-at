package tests.productCatalog.service;

import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.productCatalog.ServiceSteps.getServiceById;
import static steps.productCatalog.ServiceSteps.getServiceViewerById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceRestrictedAndAllowedGroupsTest extends Tests {

    @DisplayName("Создание сервиса с ограничением restricted group на уровне realm")
    @TmsLink("")
    @Test
    public void serviceRestrictedGroupRealmLevelTest() {
        Service service = Service.builder()
                .serviceName("service_for_restricted_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getServiceId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getServiceId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением restricted_group на уровне realm и ограничением allowed_group на уровне account")
    @TmsLink("")
    @Test
    public void serviceRestrictedGroupRealmLevelAndAllowedGroupAccountTest() {
        Service service = Service.builder()
                .serviceName("service_for_restricted_group_and_allowed_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .allowedGroups(Collections.singletonList("account:test"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getServiceId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getServiceId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением allowed_group на уровне account")
    @TmsLink("")
    @Test
    public void serviceAllowedGroupAccountTest() {
        Service service = Service.builder()
                .serviceName("service_for_allowed_group_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("account:role_api_tests"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getServiceId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getServiceId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }
}
