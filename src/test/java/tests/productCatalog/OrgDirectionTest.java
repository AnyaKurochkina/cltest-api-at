package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.orgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionTest extends Tests {

    private static final String ORG_DIRECTION_NAME = "org_direction_at_test-:2022.";
    private static final String ORG_DIRECTION_TITLE = "title_org_direction_at_test-:2022.";
    OrgDirection orgDirection;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("org_direction/", "productCatalog/orgDirection/orgDirection.json");

    @Order(1)
    @DisplayName("Создание направления в продуктовом каталоге")
    @TmsLink("643303")
    @Test
    public void createOrgDirection() {
        orgDirection = OrgDirection.builder()
                .orgDirectionName(ORG_DIRECTION_NAME)
                .title(ORG_DIRECTION_TITLE)
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

    @Order(2)
    @DisplayName("Проверка значения next в запросе на получение списка направлений")
    @TmsLink("679060")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetOrgDirectionListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @Order(3)
    @DisplayName("Проверка существования направления по имени")
    @TmsLink("643309")
    @Test
    public void checkOrgDirectionExists() {
        Assertions.assertTrue(productCatalogSteps
                .isExists(orgDirection.getOrgDirectionName()));
        Assertions.assertFalse(productCatalogSteps
                .isExists("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Импорт направления")
    @TmsLink("643311")
    @Test
    public void importOrgDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        productCatalogSteps.importObject(RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(productCatalogSteps.isExists(orgDirectionName));
        productCatalogSteps.deleteByName(orgDirectionName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(orgDirectionName));
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
        Assertions.assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.deleteByName(cloneName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(cloneName));
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

    @Order(12)
    @DisplayName("Проверка сортировки по дате создания в направлениях")
    @TmsLink("679074")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByCreateData(GetOrgDirectionListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @Order(13)
    @DisplayName("Проверка сортировки по дате обновления в направлениях")
    @TmsLink("742465")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = productCatalogSteps
                .orderingByUpDateData(GetOrgDirectionListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    String.format("Даты обновлений направлений с именами %s и %s не соответсвуют условию сортировки."
                            , list.get(i).getName(), list.get(i + 1).getName()));
        }
    }

    @Order(14)
    @DisplayName("Проверка доступа для методов с публичным ключом в направлениях")
    @TmsLink("742468")
    @Test
    public void checkAccessWithPublicToken() {
        productCatalogSteps.getObjectByNameWithPublicToken(orgDirection.getOrgDirectionName()).assertStatus(200);
        productCatalogSteps.createProductObjectWithPublicToken(productCatalogSteps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.partialUpdateObjectWithPublicToken(orgDirection.getOrgDirectionId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        productCatalogSteps.putObjectByIdWithPublicToken(orgDirection.getOrgDirectionId(), productCatalogSteps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        productCatalogSteps.deleteObjectWithPublicToken(orgDirection.getOrgDirectionId()).assertStatus(403);
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
            orgDirection.deleteObject();
    }
}
