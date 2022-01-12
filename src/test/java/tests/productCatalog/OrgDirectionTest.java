package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Action.existsAction.response.ExistsActionResponse;
import httpModels.productCatalog.OrgDirection.existsOrgDirection.response.ExistsOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import httpModels.productCatalog.OrgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import httpModels.productCatalog.GetImpl;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: направления")
public class OrgDirectionTest extends Tests {

    OrgDirection orgDirection;
    private final String productName = "org_direction/";
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();


    @Order(1)
    @DisplayName("Создание направления в продуктовом каталоге")
    @Test
    public void createOrgDirection() {
        orgDirection = OrgDirection.builder()
                .orgDirectionName("org_direction_at_test2021")
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка направлений")
    @Test
    public void getOrgDirectionList() {
        Assertions.assertTrue(productCatalogSteps
                .getProductObjectList(productName, GetOrgDirectionListResponse.class).size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования направления по имени")
    @Test
    public void checkOrgDirectionExists() {
        Assertions.assertTrue(productCatalogSteps
                .isExists(productName, orgDirection.getOrgDirectionName(), ExistsOrgDirectionResponse.class));
        Assertions.assertFalse(productCatalogSteps
                .isExists(productName, "NoExistsAction", ExistsActionResponse.class));
    }

    @Order(4)
    @DisplayName("Импорт направления")
    @Test
    public void importOrgDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(productCatalogSteps.isExists(productName, orgDirectionName, ExistsOrgDirectionResponse.class));
        productCatalogSteps.deleteByName(productName, orgDirectionName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, orgDirectionName, ExistsOrgDirectionResponse.class));
    }

    @Order(5)
    @DisplayName("Получение направления по Id")
    @Test
    public void getOrgDirectionById() {
        GetImpl productCatalogGet = productCatalogSteps
                .getById(productName, orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class);
        Assertions.assertEquals(productCatalogGet.getName(), orgDirection.getOrgDirectionName());
    }

    @Order(6)
    @DisplayName("Обновление направления по Id")
    @Test
    public void updateOrgDirection() {
        String expected = "Update description";
        productCatalogSteps.partialUpdateObject(productName, orgDirection.getOrgDirectionId(), new JSONObject()
                .put("description", expected));
        String actual = productCatalogSteps
                .getById(productName, orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Order(7)
    @DisplayName("Копирование направления по Id")
    @Test
    public void copyOrgDirectionById() {
        String cloneName = orgDirection.getOrgDirectionName() + "-clone";
        productCatalogSteps.copyById(productName, orgDirection.getOrgDirectionId());
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistsOrgDirectionResponse.class));
        productCatalogSteps.deleteByName(productName, cloneName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistsOrgDirectionResponse.class));
    }

    @Order(8)
    @DisplayName("Экспорт направления по Id")
    @Test
    public void exportOrgDirectionById() {
        productCatalogSteps.exportById(productName, orgDirection.getOrgDirectionId());
    }

    @Order(9)
    @Disabled
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени.")
    @Test
    public void createActionWithInvalidCharacters() {
        assertAll("Направление создалось с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("NameWithUppercase", "productCatalog/orgDirection/orgDirection.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("nameWithUppercaseInMiddle", "productCatalog/orgDirection/orgDirection.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("имя", "productCatalog/orgDirection/orgDirection.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("Имя", "productCatalog/orgDirection/orgDirection.json"))
                        .assertStatus(400),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                                .createJsonObject("a&b&c", "productCatalog/orgDirection/orgDirection.json"))
                        .assertStatus(400)
        );
    }

    @Order(100)
    @Test
    @DisplayName("Удаление направления")
    @MarkDelete
    public void deleteOrgDirection() {
        try (OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName("org_direction_at_test2021")
                .build()
                .createObjectExclusiveAccess()) {
            orgDirection.deleteObject();
        }
    }
}
