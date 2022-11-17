package api.cloud.productCatalog.orgDirection;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @DisplayName("Негативный тест на получение направления по Id без токена")
    @TmsLink("643315")
    @Test
    public void getOrgDirectionByIdWithOutToken() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("get_org_direction_by_id_without_token_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.getByIdWithOutToken(orgDirection.getId());
    }

    @DisplayName("Негативный тест на обновление направления по Id без токена")
    @TmsLink("643322")
    @Test
    public void updateOrgDirectionByIdWithOutToken() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("update_org_direction_by_id_without_token_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(orgDirection.getId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на копирование направления по Id без токена")
    @TmsLink("643332")
    @Test
    public void copyOrgDirectionByIdWithOutToken() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("copy_org_direction_by_id_without_token_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.copyByIdWithOutToken(orgDirection.getId());
    }

    @DisplayName("Негативный тест на создание направления с неуникальным именем")
    @TmsLink("679074")
    @Test
    public void createOrgDirectionWithNonUniqueName() {
        String orgName = "create_org_direction_with_same_name_test_api";
        OrgDirection.builder()
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(orgName)).assertStatus(400);
    }

    @DisplayName("Негативный тест на создание направления с недопустимыми символами в имени")
    @TmsLink("643340")
    @Test
    public void createActionWithInvalidCharacters() {
        OrgDirection.builder()
                .name("NameWithUppercase")
                .build()
                .negativeCreateRequest(500);
        OrgDirection.builder()
                .name("nameWithUppercaseInMiddle")
                .build()
                .negativeCreateRequest(500);
        OrgDirection.builder()
                .name("имя")
                .build()
                .negativeCreateRequest(500);
        OrgDirection.builder()
                .name("Имя")
                .build()
                .negativeCreateRequest(500);
        OrgDirection.builder()
                .name("a&b&c")
                .build()
                .negativeCreateRequest(500);
        OrgDirection.builder()
                .name("")
                .build()
                .negativeCreateRequest(400);
        OrgDirection.builder()
                .name(" ")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на удаление направления без токена")
    @TmsLink("643344")
    @Test
    public void deleteOrgDirectionWithOutToken() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("delete_org_direction_by_id_without_token_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(orgDirection.getId());
    }
}
