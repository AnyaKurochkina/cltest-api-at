package api.cloud.productCatalog.service;

import httpModels.productCatalog.service.getService.response.GetServiceResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.service.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

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
    @TmsLink("1281845")
    @Test
    public void serviceRestrictedGroupRealmLevelTest() {
        Service service = Service.builder()
                .name("service_for_restricted_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением allowed_group на уровне realm")
    @TmsLink("1282617")
    @Test
    public void serviceAllowedGroupRealmLevelTest() {
        Service service = Service.builder()
                .name("service_for_allowed_group_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением restricted_group на уровне realm и ограничением allowed_group на уровне account")
    @TmsLink("1281847")
    @Test
    public void serviceRestrictedGroupRealmLevelAndAllowedGroupAccountTest() {
        Service service = Service.builder()
                .name("service_for_restricted_group_and_allowed_group_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("qa-admin"))
                .allowedGroups(Collections.singletonList("account:test"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением restricted_group на уровне account и ограничением allowed_group на уровне realm")
    @TmsLink("1282632")
    @Test
    public void serviceRestrictedGroupAccountLevelAndAllowedGroupRealmTest() {
        Service service = Service.builder()
                .name("service_for_restricted_group_acc_lvl_and_allowed_group_realm_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .allowedGroups(Collections.singletonList("superadmin"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением allowed_group на уровне account")
    @TmsLink("1281848")
    @Test
    public void serviceAllowedGroupAccountTest() {
        Service service = Service.builder()
                .name("service_for_allowed_group_api_test")
                .version("1.0.1")
                .allowedGroups(Collections.singletonList("account:role_api_tests"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }

    @DisplayName("Создание сервиса с ограничением restricted_group на уровне account")
    @TmsLink("1282603")
    @Test
    public void serviceRestrictedGroupAccountTest() {
        Service service = Service.builder()
                .name("service_for_restricted_group_account_lvl_api_test")
                .version("1.0.1")
                .restrictedGroups(Collections.singletonList("account:role2_api_tests"))
                .build()
                .createObject();
        GetServiceResponse serviceById = getServiceById(service.getId());
        assertNotNull(serviceById);
        String msg = getServiceViewerById(service.getId()).assertStatus(404).jsonPath().getString("detail");
        assertEquals("Страница не найдена.", msg);
    }
}
