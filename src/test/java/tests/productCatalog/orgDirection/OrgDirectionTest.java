package tests.productCatalog.orgDirection;

import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.orgDirection.getOrgDirection.response.GetOrgDirectionResponse;
import httpModels.productCatalog.orgDirection.getOrgDirectionList.response.GetOrgDirectionListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.OrgDirection;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Tag("org_direction")
@Epic("Продуктовый каталог")
@Feature("Направления")
@DisabledIfEnv("prod")
public class OrgDirectionTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/org_direction/",
            "productCatalog/orgDirection/orgDirection.json");

    @DisplayName("Создание направления в продуктовом каталоге")
    @TmsLink("643303")
    @Test
    public void createOrgDirection() {
        String orgName = "org_direction_at_test-:2022.";
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        GetImpl getOrgDirection = steps.getById(orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class);
        assertEquals(orgName, getOrgDirection.getName());
    }

    @DisplayName("Проверка существования направления по имени")
    @TmsLink("643309")
    @Test
    public void checkOrgDirectionExists() {
        String orgName = "check_org_direction_is_exist_test_api";
        OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(orgName));
        Assertions.assertFalse(steps.isExists("NoExistsAction"));
    }

    @DisplayName("Импорт направления")
    @TmsLink("643311")
    @Test
    public void importOrgDirection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirection.json");
        String orgDirectionName = new JsonPath(data).get("OrgDirection.name");
        steps.importObject(RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirection.json");
        Assertions.assertTrue(steps.isExists(orgDirectionName));
        steps.deleteByName(orgDirectionName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(steps.isExists(orgDirectionName));
    }

    @DisplayName("Получение направления по Id")
    @TmsLink("643313")
    @Test
    public void getOrgDirectionById() {
        String orgName = "get_by_id_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        GetImpl productCatalogGet = steps.getById(orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class);
        Assertions.assertEquals(productCatalogGet.getName(), orgName);
    }

    @DisplayName("Обновление направления по Id")
    @TmsLink("643319")
    @Test
    public void updateOrgDirection() {
        String orgName = "update_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String expected = "Update description";
        steps.partialUpdateObject(orgDirection.getOrgDirectionId(), new JSONObject().put("description", expected));
        String actual = steps.getById(orgDirection.getOrgDirectionId(), GetOrgDirectionResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Копирование направления по Id")
    @TmsLink("643327")
    @Test
    public void copyOrgDirectionById() {
        String orgName = "copy_by_id_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String cloneName = orgName + "-clone";
        steps.copyById(orgDirection.getOrgDirectionId());
        Assertions.assertTrue(steps.isExists(cloneName));
        steps.deleteByName(cloneName, GetOrgDirectionListResponse.class);
        Assertions.assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Проверка сортировки по дате создания в направлениях")
    @TmsLink("807561")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetOrgDirectionListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в направлениях")
    @TmsLink("742465")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetOrgDirectionListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    String.format("Даты обновлений направлений с именами %s и %s не соответсвуют условию сортировки."
                            , list.get(i).getName(), list.get(i + 1).getName()));
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в направлениях")
    @TmsLink("742468")
    @Test
    public void checkAccessWithPublicToken() {
        String orgName = "check_access_with_public_token_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String orgDirectionId = orgDirection.getOrgDirectionId();
        steps.getObjectByNameWithPublicToken(orgName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps.createJsonObject("create_object_with_public_token_api"))
                .assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(orgDirectionId, new JSONObject()
                .put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(orgDirectionId, steps.createJsonObject("update_object_with_public_token_api"))
                .assertStatus(403);
        steps.deleteObjectWithPublicToken(orgDirectionId).assertStatus(403);
    }

    @DisplayName("Экспорт направления по Id")
    @TmsLink("643334")
    @Test
    public void exportOrgDirectionById() {
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName("export_org_direction_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.exportById(orgDirection.getOrgDirectionId());
    }

    @DisplayName("Удаление направления")
    @TmsLink("643348")
    @Test
    public void deleteOrgDirection() {
        OrgDirection orgDirection = OrgDirection.builder()
                .orgDirectionName("delete_org_direction_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        orgDirection.deleteObject();
    }

    @Test
    @DisplayName("Загрузка OrgDirection в GitLab")
    @EnabledIfEnv("ift")
    @TmsLink("975382")
    public void dumpToGitlabOrgDirection() {
        String orgDirection = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        OrgDirection jinja = OrgDirection.builder()
                .orgDirectionName(orgDirection)
                .title(orgDirection)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(jinja.getOrgDirectionId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
    }

    @Test
    @DisplayName("Выгрузка OrgDirection из GitLab")
    @EnabledIfEnv("ift")
    @TmsLink("1028957")
    public void loadFromGitlabOrgDirection() {
        String orgDirectionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = OrgDirection.builder()
                .orgDirectionName(orgDirectionName)
                .title(orgDirectionName)
                .build()
                .init().toJson();
        GetOrgDirectionResponse jinja = steps.createProductObject(jsonObject).extractAs(GetOrgDirectionResponse.class);
        Response response = steps.dumpToBitbucket(jinja.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(orgDirectionName, GetOrgDirectionListResponse.class);
        String path = "orgdirection_" + orgDirectionName;
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(orgDirectionName));
        steps.deleteByName(orgDirectionName, GetOrgDirectionListResponse.class);
        assertFalse(steps.isExists(orgDirectionName));
    }
}
