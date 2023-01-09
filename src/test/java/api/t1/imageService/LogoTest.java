package api.t1.imageService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.ImageGroup;
import models.t1.imageService.Logo;
import models.t1.imageService.Marketing;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static steps.t1.imageService.ImageServiceSteps.*;

@Tag("image_service")
@Epic("Сервис образов")
@Feature("Logo")
public class LogoTest extends Tests {

    @Test
    @TmsLink("1365797")
    @DisplayName("Создание Logo")
    public void createLogoTest() {
        Logo logo = Logo.builder()
                .name("create_logo_test_api")
                .build()
                .createObject();
        assertTrue(isLogoExist(logo.getId()));
    }

    @Test
    @TmsLink("1365811")
    @DisplayName("Получение Logo")
    public void getLogoTest() {
        Logo logo = Logo.builder()
                .name("get_logo_test_api")
                .osDistro("test_distro")
                .build()
                .createObject();
        Logo getLogo = getLogoById(logo.getId());
        assertEquals(logo, getLogo);
    }

    @Test
    @TmsLink("1365873")
    @DisplayName("Обновление Logo")
    public void patchLogoTest() {
        Logo logo = Logo.builder()
                .name("update_logo_test_api")
                .osDistro("update_test_distro")
                .build()
                .createObject();
        String expectedOsDistro = "partial_update";
        String expectedName = "update_name";
        partialUpdateLogoById(logo.getId(), new JSONObject().put("os_distro", expectedOsDistro).put("name", expectedName));
        Logo getLogo = getLogoById(logo.getId());
        assertEquals(expectedOsDistro, getLogo.getOsDistro());
        assertEquals(expectedName, getLogo.getName());
    }

    @Test
    @TmsLink("1366031")
    @DisplayName("Удаление Logo")
    public void deleteLogoTest() {
        Logo logo = Logo.builder()
                .name("delete_logo_test_api")
                .osDistro("delete_test_distro")
                .build()
                .createObject();
        deleteLogoById(logo.getId());
        assertFalse(isLogoExist(logo.getId()));
    }

    @Test
    @TmsLink("1366083")
    @DisplayName("Удаление Logo используемого в marketing")
    public void deleteLogoUsedInMarketingTest() {
        Logo logo = Logo.builder()
                .name("delete_logo_used_in_marketing_test_api")
                .osDistro("delete_test_distro")
                .build()
                .createObject();
        Marketing.builder()
                .name("marketing_for_logo_api_test")
                .logoId(logo.getId())
                .build()
                .createObject();
        String response = getDeleteLogoByIdResponse(logo.getId()).assertStatus(422).jsonPath().getString("detail");
        assertEquals("cannot delete logo", response);
    }

    @Test
    @TmsLink("1366151")
    @DisplayName("Удаление Logo используемого в image group")
    public void deleteLogoUsedInImageGroupTest() {
        Logo logo = Logo.builder()
                .name("delete_logo_used_in_marketing_test_api")
                .osDistro("delete_test_distro")
                .build()
                .createObject();
        ImageGroup.builder()
                .name("image_group_for_logo_api_test")
                .logoId(logo.getId())
                .build()
                .createObject();
        String response = getDeleteLogoByIdResponse(logo.getId()).assertStatus(422).jsonPath().getString("detail");
        assertEquals("cannot delete logo", response);
    }
}
