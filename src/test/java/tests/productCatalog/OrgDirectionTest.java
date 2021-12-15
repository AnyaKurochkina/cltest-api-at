package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.OrgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.OrgDirectionSteps;
import tests.Tests;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: направления")
public class OrgDirectionTest extends Tests {

    OrgDirection orgDirection;
    OrgDirectionSteps orgSteps = new OrgDirectionSteps();

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
        Assertions.assertTrue(orgSteps.getOrgDirectionList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка существования направления по имени")
    @Test
    public void checkOrgDirectionExists() {
        Assertions.assertTrue(orgSteps.isProductExists(orgDirection.getOrgDirectionName()));
        Assertions.assertFalse(orgSteps.isProductExists("NotExistName"));
    }

    @Order(4)
    @DisplayName("Импорт направления")
    @Test
    public void importOrgDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        orgSteps.importOrgDirection(Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(orgSteps.isProductExists(orgDirectionName));
        orgSteps.deleteOrgDirectionByName(orgDirectionName);
        Assertions.assertFalse(orgSteps.isProductExists(orgDirectionName));
    }

    @Order(5)
    @DisplayName("Получение направления по Id")
    @Test
    public void getOrgDirectionById() {
        GetOrgDirectionResponse response = orgSteps.getOrgDirectionById(orgDirection.getOrgDirectionId());
        Assertions.assertEquals(response.getName(), orgDirection.getOrgDirectionName());
    }

    @Order(6)
    @DisplayName("Обновление направления по Id")
    @Test
    public void updateOrgDirection() {
        String expected = "Update description";
        JSONObject jsonObject = orgSteps.createJsonObject(orgDirection.getOrgDirectionName(), expected);
        orgSteps.updateOrgDirectionById(orgDirection.getOrgDirectionId(), jsonObject);
        String actual = orgSteps.getOrgDirectionById(orgDirection.getOrgDirectionId()).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Order(7)
    @DisplayName("Копирование направления по Id")
    @Test
    public void copyOrgDirectionById() {
        String cloneName = orgDirection.getOrgDirectionName() + "-clone";
        orgSteps.copyOrgDirectionById(orgDirection.getOrgDirectionId());
        Assertions.assertTrue(orgSteps.isProductExists(cloneName));
        orgSteps.deleteOrgDirectionByName(cloneName);
        Assertions.assertFalse(orgSteps.isProductExists(cloneName));
    }

    @Order(8)
    @DisplayName("Экспорт направления по Id")
    @Test
    public void exportOrgDirectionById() {
    orgSteps.exportOrgDirectionById(orgDirection.getOrgDirectionId());
    }

    @Order(13)
    @ParameterizedTest
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени.")
    @MethodSource("dataName")
    public void createActionWithInvalidCharacters(String name) {
        JSONObject object = orgSteps.createJsonObject(name);
        orgSteps.createOrgDirection(object).assertStatus(400);
    }

    private static Stream<Arguments> dataName() {
        return Stream.of(
                Arguments.of("NameWithUppercase"),
                Arguments.of("nameWithUppercaseInMiddle"),
                Arguments.of("имя"),
                Arguments.of("Имя"),
                Arguments.of("a&b&c")
        );
    }

    @Order(100)
    @Test
    @DisplayName("Удаление направления")
    @MarkDelete
    public void deleteOrgDirection() {
        try (OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName("org_direction_at_test")
                .build()
                .createObjectExclusiveAccess()) {
            orgDirection.deleteObject();
        }
        Assertions.assertFalse(orgSteps.isProductExists("org_direction_at_test"));
    }
}
