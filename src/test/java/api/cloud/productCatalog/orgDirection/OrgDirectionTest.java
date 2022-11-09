package api.cloud.productCatalog.orgDirection;

import core.helper.Configure;
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
import models.cloud.productCatalog.OrgDirection;
import models.cloud.productCatalog.Service;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

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
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        GetImpl getOrgDirection = steps.getById(orgDirection.getId(), GetOrgDirectionResponse.class);
        assertEquals(orgName, getOrgDirection.getName());
    }

    @DisplayName("Создание направления в продуктовом каталоге с иконкой")
    @TmsLink("1082790")
    @Test
    public void createOrgDirectionWithIcon() {
        Icon icon = Icon.builder()
                .name("org_direction_icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String orgName = "create_org_direction_with_icon_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name(orgName)
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetOrgDirectionResponse actualOrgDirection = (GetOrgDirectionResponse) steps.getById(orgDirection.getId(), GetOrgDirectionResponse.class);
        assertFalse(actualOrgDirection.getIconStoreId().isEmpty());
        assertFalse(actualOrgDirection.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких направлений в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1082792")
    @Test
    public void createSeveralOrgDirectionWithSameIcon() {
        Icon icon = Icon.builder()
                .name("org_direction_icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String orgName = "create_first_org_direction_with_same_icon_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name(orgName)
                .iconStoreId(icon.getId())
                .build()
                .createObject();

        OrgDirection secondOrgDirection = OrgDirection.builder()
                .name("create_second_org_direction_with_same_icon_test_api")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetOrgDirectionResponse actualFirstOrgDirection = (GetOrgDirectionResponse) steps.getById(orgDirection.getId(), GetOrgDirectionResponse.class);
        GetOrgDirectionResponse actualSecondOrgDirection = (GetOrgDirectionResponse) steps.getById(secondOrgDirection.getId(), GetOrgDirectionResponse.class);
        assertEquals(actualFirstOrgDirection.getIconUrl(), actualSecondOrgDirection.getIconUrl());
        assertEquals(actualFirstOrgDirection.getIconStoreId(), actualSecondOrgDirection.getIconStoreId());
    }

    @DisplayName("Проверка существования направления по имени")
    @TmsLink("643309")
    @Test
    public void checkOrgDirectionExists() {
        String orgName = "check_org_direction_is_exist_test_api";
        OrgDirection.builder()
                .name(orgName)
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

    @DisplayName("Импорт направления c иконкой")
    @TmsLink("1086532")
    @Test
    public void importOrgDirectionWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/orgDirection/importOrgDirectionWithIcon.json");
        String name = new JsonPath(data).get("OrgDirection.name");
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetOrgDirectionListResponse.class);
        }
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/orgDirection/importOrgDirectionWithIcon.json");
        String id = steps.getProductObjectIdByNameWithMultiSearch(name, GetOrgDirectionListResponse.class);
        GetOrgDirectionResponse orgDirection = (GetOrgDirectionResponse) steps.getById(id, GetOrgDirectionResponse.class);
        assertFalse(orgDirection.getIconStoreId().isEmpty());
        assertFalse(orgDirection.getIconUrl().isEmpty());
        assertTrue(steps.isExists(name), "Направление не существует");
        steps.deleteByName(name, GetOrgDirectionListResponse.class);
        assertFalse(steps.isExists(name), "Направление существует");
    }

    @DisplayName("Получение направления по Id")
    @TmsLink("643313")
    @Test
    public void getOrgDirectionById() {
        String orgName = "get_by_id_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        GetImpl productCatalogGet = steps.getById(orgDirection.getId(), GetOrgDirectionResponse.class);
        Assertions.assertEquals(productCatalogGet.getName(), orgName);
    }

    @DisplayName("Обновление направления по Id")
    @TmsLink("643319")
    @Test
    public void updateOrgDirection() {
        String orgName = "update_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String expected = "Update description";
        steps.partialUpdateObject(orgDirection.getId(), new JSONObject().put("description", expected));
        String actual = steps.getById(orgDirection.getId(), GetOrgDirectionResponse.class).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Копирование направления по Id")
    @TmsLink("643327")
    @Test
    public void copyOrgDirectionById() {
        String orgName = "copy_by_id_org_direction_test_api";
        OrgDirection orgDirection = OrgDirection.builder()
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String cloneName = orgName + "-clone";
        steps.copyById(orgDirection.getId());
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
                .name(orgName)
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        String orgDirectionId = orgDirection.getId();
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
                .name("export_org_direction_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        steps.exportById(orgDirection.getId());
    }

    @DisplayName("Удаление направления")
    @TmsLink("643348")
    @Test
    public void deleteOrgDirection() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("delete_org_direction_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .createObject();
        orgDirection.deleteObject();
    }

    @DisplayName("Удаление направления используемого в сервисе")
    @TmsLink("1172114")
    @Test
    public void deleteOrgDirectionUsedInService() {
        OrgDirection orgDirection = OrgDirection.builder()
                .name("delete_org_direction_used_in_service")
                .title("delete_org_direction_used_in_service")
                .build()
                .createObject();
        Service service = Service.builder()
                .serviceName("service_for_delete_org_direction_test_api")
                .title("service_for_delete_org_direction_test_api")
                .directionId(orgDirection.getId())
                .build()
                .createObject();
        String errorMessage = steps.getDeleteObjectResponse(service.getDirectionId())
                .assertStatus(400).jsonPath().getString("error");
        assertEquals(String.format("Нельзя удалить направление %s. Оно используется:\nService: (name: %s)", service.getDirectionName(), service.getServiceName()), errorMessage);
    }

    @Test
    @DisplayName("Загрузка OrgDirection в GitLab")
    @TmsLink("975382")
    public void dumpToGitlabOrgDirection() {
        String orgDirectionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        OrgDirection jinja = OrgDirection.builder()
                .name(orgDirectionName)
                .title(orgDirectionName)
                .build()
                .createObject();
        String tag = "orgdirection_" + orgDirectionName;
        Response response = steps.dumpToBitbucket(jinja.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка OrgDirection из GitLab")
    @TmsLink("1028957")
    public void loadFromGitlabOrgDirection() {
        String orgDirectionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = OrgDirection.builder()
                .name(orgDirectionName)
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
