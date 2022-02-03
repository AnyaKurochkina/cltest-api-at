package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.action.existsAction.response.ExistsActionResponse;
import httpModels.productCatalog.orgDirection.existsOrgDirection.response.ExistsOrgDirectionResponse;
import httpModels.productCatalog.orgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
public class OrgDirectionTest extends Tests {

    private static final String ORG_DIRECTION_NAME = "org_direction_at_test-:2022.";
    OrgDirection orgDirection;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("org_direction/", "productCatalog/orgDirection/orgDirection.json");

    @Order(1)
    @DisplayName("Создание направления в продуктовом каталоге")
    @TmsLink("643303")
    @Test
    public void createOrgDirection() {
        orgDirection = OrgDirection.builder()
                .orgDirectionName(ORG_DIRECTION_NAME)
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка направлений")
    @TmsLink("643305")
    @Test
    public void getOrgDirectionList() {
        Assertions.assertTrue(productCatalogSteps
                .getProductObjectList(GetOrgDirectionListResponse.class).size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования направления по имени")
    @TmsLink("643309")
    @Test
    public void checkOrgDirectionExists() {
        Assertions.assertTrue(productCatalogSteps
                .isExists(orgDirection.getOrgDirectionName(), ExistsOrgDirectionResponse.class));
        Assertions.assertFalse(productCatalogSteps
                .isExists("NoExistsAction", ExistsActionResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт направления")
    @TmsLink("643311")
    @Test
    public void importOrgDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(productCatalogSteps.isExists(orgDirectionName, ExistsOrgDirectionResponse.class));
        productCatalogSteps.deleteByName(orgDirectionName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(orgDirectionName, ExistsOrgDirectionResponse.class));
    }

    @Order(5)
    @DisplayName("Получение направления по Id")
    @TmsLink("643313")
    @Test
    public void getOrgDirectionById() {
        GetImpl productCatalogGet = productCatalogSteps
                .getById(orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class);
        Assertions.assertEquals(productCatalogGet.getName(), orgDirection.getOrgDirectionName());
    }

    @Order(6)
    @DisplayName("Негативный тест на получение направления по Id без токена")
    @TmsLink("643315")
    @Test
    public void getOrgDirectionByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(orgDirection.getOrgDirectionId());
    }

    @Order(7)
    @DisplayName("Обновление направления по Id")
    @TmsLink("643319")
    @Test
    public void updateOrgDirection() {
        String expected = "Update description";
        productCatalogSteps.partialUpdateObject(orgDirection.getOrgDirectionId(), new JSONObject()
                .put("description", expected));
        String actual = productCatalogSteps
                .getById(orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Order(8)
    @DisplayName("Негативный тест на обновление направления по Id без токена")
    @TmsLink("643322")
    @Test
    public void updateOrgDirectionByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(orgDirection.getOrgDirectionId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(9)
    @DisplayName("Копирование направления по Id")
    @TmsLink("643327")
    @Test
    public void copyOrgDirectionById() {
        String cloneName = orgDirection.getOrgDirectionName() + "-clone";
        productCatalogSteps.copyById(orgDirection.getOrgDirectionId());
        Assertions.assertTrue(productCatalogSteps.isExists(cloneName, ExistsOrgDirectionResponse.class));
        productCatalogSteps.deleteByName(cloneName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(cloneName, ExistsOrgDirectionResponse.class));
    }

    @Order(10)
    @DisplayName("Негативный тест на копирование направления по Id без токена")
    @TmsLink("643332")
    @Test
    public void copyOrgDirectionByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(orgDirection.getOrgDirectionId());
    }

    @Order(11)
    @DisplayName("Негативный тест на создание направления с неуникальным именем")
    @Test
    public void createOrgDirectionWithNonUniqueName() {
        {
            productCatalogSteps.createProductObject(productCatalogSteps
                    .createJsonObject(ORG_DIRECTION_NAME)).assertStatus(400);
        }
    }

    @Order(80)
    @DisplayName("Экспорт направления по Id")
    @TmsLink("643334")
    @Test
    public void exportOrgDirectionById() {
        productCatalogSteps.exportById(orgDirection.getOrgDirectionId());
    }

    @Order(98)
    @DisplayName("Негативный тест на создание направления с недопустимыми символами в имени")
    @TmsLink("643340")
    @Test
    public void createActionWithInvalidCharacters() {
        assertAll("Направление создалось с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("NameWithUppercase"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("nameWithUppercaseInMiddle"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("название"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("Название"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject("a&b&c"))
                        .assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(""))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(" "))
                        .assertStatus(400)
        );
    }

    @Order(99)
    @DisplayName("Негативный тест на удаление направления без токена")
    @TmsLink("643344")
    @Test
    public void deleteOrgDirectionWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(orgDirection.getOrgDirectionId());
    }

    @Order(100)
    @DisplayName("Удаление направления")
    @TmsLink("643348")
    @MarkDelete
    @Test
    public void deleteOrgDirection() {
        try (OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(ORG_DIRECTION_NAME)
                .build()
                .createObjectExclusiveAccess()) {
            orgDirection.deleteObject();
        }
    }
}
