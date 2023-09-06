package api.cloud.productCatalog.orgDirection;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.OrgDirectionSteps.*;

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
    public void createOrgDirectionTest() {
        String orgName = "org_direction_at_test-:2022.";
        OrgDirection orgDirection = createOrgDirectionByName(orgName);
        OrgDirection getOrgDirection = getOrgDirectionById(orgDirection.getId());
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
        OrgDirection actualOrgDirection = getOrgDirectionById(orgDirection.getId());
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
        OrgDirection actualFirstOrgDirection = getOrgDirectionById(orgDirection.getId());
        OrgDirection actualSecondOrgDirection = getOrgDirectionById(secondOrgDirection.getId());
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
        Assertions.assertTrue(isOrgDirectionExists(orgName));
        Assertions.assertFalse(isOrgDirectionExists("NoExistsAction"));
    }

    @DisplayName("Получение направления по Id")
    @TmsLink("643313")
    @Test
    public void getOrgDirectionByIdTest() {
        String orgName = "get_by_id_org_direction_test_api";
        OrgDirection orgDirection = createOrgDirectionByName(orgName);
        OrgDirection productCatalogGet = getOrgDirectionById(orgDirection.getId());
        Assertions.assertEquals(productCatalogGet.getName(), orgName);
    }

    @DisplayName("Обновление направления по Id")
    @TmsLink("643319")
    @Test
    public void updateOrgDirection() {
        String orgName = "update_org_direction_test_api";
        OrgDirection orgDirection = createOrgDirectionByName(orgName);
        String expected = "Update description";
        partialUpdateOrgDirection(orgDirection.getId(), new JSONObject().put("description", expected));
        String actual = getOrgDirectionById(orgDirection.getId()).getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Копирование направления по Id")
    @TmsLink("643327")
    @Test
    public void copyOrgDirectionById() {
        String orgName = "copy_by_id_org_direction_test_api";
        OrgDirection orgDirection = createOrgDirectionByName(orgName);
        String cloneName = orgName + "-clone";
        copyOrgDirection(orgDirection.getId());
        Assertions.assertTrue(isOrgDirectionExists(cloneName));
        deleteOrgDirectionByName(cloneName);
        Assertions.assertFalse(isOrgDirectionExists(cloneName));
    }

    @DisplayName("Проверка сортировки по дате создания в направлениях")
    @TmsLink("807561")
    @Test
    public void orderingByCreateData() {
            assertTrue(orderingOrgDirectionByCreateData(),
                    "Даты должны быть отсортированы по возрастанию");
        }

    @DisplayName("Проверка сортировки по дате обновления в направлениях")
    @TmsLink("742465")
    @Test
    public void orderingByUpDateData() {
            assertTrue(orderingOrgDirectionByUpdateData(), "Даты должны быть отсортированы по возрастанию");
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

    @DisplayName("Удаление направления")
    @TmsLink("643348")
    @Test
    public void deleteOrgDirection() {
        JSONObject json = OrgDirection.builder()
                .name("delete_org_direction_test_api")
                .title("title_org_direction_at_test-:2022.")
                .build()
                .toJson();
        OrgDirection orgDirection = createOrgDirection(json).assertStatus(201).extractAs(OrgDirection.class);
        deleteOrgDirectionById(orgDirection.getId());
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
                .name("service_for_delete_org_direction_test_api")
                .title("service_for_delete_org_direction_test_api")
                .directionId(orgDirection.getId())
                .build()
                .createObject();
        String errorMessage = steps.getDeleteObjectResponse(service.getDirectionId())
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals(String.format("Ошибка удаления. Нельзя удалить направление %s, которое используется Сервисом: %s. Отвяжите направление от сервиса и повторите попытку",
                service.getDirectionName(), service.getName()), errorMessage);
    }

    @Test
    @DisplayName("Загрузка OrgDirection в GitLab")
    @Disabled
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
    @Disabled
    @DisplayName("Выгрузка OrgDirection из GitLab")
    @TmsLink("1028957")
    public void loadFromGitlabOrgDirection() {
        String orgDirectionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        OrgDirection orgDirection = createOrgDirectionByName(orgDirectionName);
        Response response = steps.dumpToBitbucket(orgDirection.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteOrgDirectionByName(orgDirectionName);
        String path = "orgdirection_" + orgDirectionName;
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(isOrgDirectionExists(orgDirectionName));
        deleteOrgDirectionByName(orgDirectionName);
        assertFalse(isOrgDirectionExists(orgDirectionName));
    }
}
