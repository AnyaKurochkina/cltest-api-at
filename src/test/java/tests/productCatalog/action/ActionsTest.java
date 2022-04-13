package tests.productCatalog.action;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.action.getAction.response.GetActionResponse;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("actions/",
            "productCatalog/actions/createAction.json", Configure.ProductCatalogURL);

    @DisplayName("Создание действия в продуктовом каталоге")
    @TmsLink("640545")
    @Test
    public void createAction() {
        String actionName = "create_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        GetImpl actualAction = steps.getById(action.getActionId(), GetActionResponse.class);
        assertEquals(actionName, actualAction.getName());
    }

    @DisplayName("Проверка существования действия по имени")
    @TmsLink("642432")
    @Test
    public void checkActionExists() {
        String actionName = "action_exist_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        assertTrue(steps.isExists(action.getActionName()), "Действие не существует");
        assertFalse(steps.isExists("NoExistsAction"), "Действие существует");
    }

    @DisplayName("Проверка дефолтного значения поля location_restriction в действиях")
    @TmsLink("783425")
    @Test
    public void locationRestrictionCheckDefaultValue() {
        String actionName = "location_restriction_default_value_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        GetActionResponse getAction = (GetActionResponse) steps.getById(action.getActionId(), GetActionResponse.class);
        assertEquals("", getAction.getLocationRestriction());
    }


    @DisplayName("Проверка значения поля location_restriction в действиях")
    @TmsLink("783427")
    @Test
    public void locationRestrictionCheckValue() {
        String actionName = "location_restriction_value_test_api";
        String fieldName = "local";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .locationRestriction(fieldName)
                .build()
                .createObject();
        GetActionResponse getAction = (GetActionResponse) steps.getById(action.getActionId(), GetActionResponse.class);
        assertEquals(fieldName, getAction.getLocationRestriction());
    }

    @DisplayName("Импорт действия")
    @TmsLink("642433")
    @Test
    public void importAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.json.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertTrue(steps.isExists(actionName), "Действие не существует");
        steps.deleteByName(actionName, GetActionsListResponse.class);
        assertFalse(steps.isExists(actionName), "Действие существует");
    }

    @DisplayName("Получение действия по Id")
    @TmsLink("642436")
    @Test
    public void getActionById() {
        String actionName = "get_action_by_id_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        GetImpl getAction = steps.getById(action.getActionId(), GetActionResponse.class);
        assertEquals(action.getActionName(), getAction.getName());
    }

    @DisplayName("Копирование действия по Id")
    @TmsLink("642489")
    @Test
    public void copyActionById() {
        String actionName = "clone_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        String cloneName = action.getActionName() + "-clone";
        steps.copyById(action.getActionId());
        assertTrue(steps.isExists(cloneName), "Действие не существует");
        steps.deleteByName(cloneName, GetActionsListResponse.class);
        assertFalse(steps.isExists(cloneName), "Действие существует");
    }

    @DisplayName("Копирование действия по Id и проверка на соответствие полей")
    @TmsLink("642493")
    @Test
    public void copyActionByIdAndFieldCheck() {
        String actionName = "clone_action_check_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        String cloneName = action.getActionName() + "-clone";
        steps.copyById(action.getActionId());
        String cloneId = steps.getProductObjectIdByNameWithMultiSearch(cloneName, GetActionsListResponse.class);
        steps.partialUpdateObject(cloneId, new JSONObject().put("description", "descr_api"));
        GetImpl importedAction = steps.getByIdAndVersion(cloneId, "1.0.1", GetActionResponse.class);
        assertAll(
                () -> assertEquals("1.0.1", importedAction.getVersion()),
                () -> assertEquals("descr_api", importedAction.getDescription())
        );
        assertTrue(steps.isExists(cloneName));
        steps.deleteByName(cloneName, GetActionsListResponse.class);
        assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Проверка сортировки по дате создания в действиях")
    @TmsLink("737375")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetActionsListResponse.class).getItemsList();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
                ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
                assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                        String.format("Даты создания действий с именами %s и %s не соответсвуют условию сортировки."
                                , list.get(i).getName(), list.get(i + 1).getName()));
            }
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в действиях")
    @TmsLink("737383")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetActionsListResponse.class).getItemsList();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
                ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
                assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                        String.format("Даты обновлений действий с именами %s и %s не соответсвуют условию сортировки."
                                , list.get(i).getName(), list.get(i + 1).getName()));
            }
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в действиях")
    @TmsLink("737385")
    @Test
    public void checkAccessWithPublicToken() {
        String actionName = "action_access_with_public_token_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(actionName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(action.getActionId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(action.getActionId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(action.getActionId()).assertStatus(403);
    }

    @DisplayName("Экспорт действия по Id")
    @TmsLink("642499")
    @Test
    public void exportActionById() {
        String actionName = "action_export_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        steps.exportById(action.getActionId());
    }

    @DisplayName("Проверка независимых от версии параметров в действиях")
    @TmsLink("716373")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        String actionName = "action_check_param_version_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        String version = action.getVersion();
        String id = action.getActionId();
        List<String> list = Collections.singletonList("restricted");
        steps.partialUpdateObject(action.getActionId(), new JSONObject().put("restricted_groups", list));
        List<String> allowed_groups = steps.getJsonPath(id).get("restricted_groups");
        String newVersion = steps.getById(id, GetActionResponse.class).getVersion();
        assertEquals(list, allowed_groups, "Доступные группы не соврадают");
        assertEquals(version, newVersion, "Версии не совпадают");

    }

    @DisplayName("Обновление действия с указанием версии в граничных значениях")
    @TmsLink("642507")
    @Test
    public void updateActionAndGetVersion() {
        Action actionTest = Action.builder()
                .actionName("action_version_test_api")
                .version("1.0.999")
                .build()
                .createObject();
        steps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api2"));
        String currentVersion = steps.getById(actionTest.getActionId(), GetActionResponse.class).getVersion();
        Assertions.assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api3")
                .put("version", "1.999.999"));
        steps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api4"));
        currentVersion = steps.getById(actionTest.getActionId(), GetActionResponse.class).getVersion();
        Assertions.assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api5")
                .put("version", "999.999.999"));
        steps.partialUpdateObject(actionTest.getActionId(), new JSONObject().put("name", "action_version_test_api6"))
                .assertStatus(500);
    }

    @DisplayName("Обновление действия без указания версии, версия должна инкрементироваться")
    @TmsLink("642515")
    @Test
    public void patchTest() {
        String actionName = "action_patch_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        String version = steps
                .patchObject(GetActionResponse.class, actionName, action.getGraphId(), action.getActionId())
                .getVersion();
        assertEquals("1.0.1", version, "Версии не совпадают");
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в действиях")
    @TmsLink("642524")
    @Test
    public void getKeyGraphVersionCalculatedInResponse() {
        String actionName = "action_get_key_version_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        String graphVersionCalculated = steps.getById(action.getActionId(), GetActionResponse.class)
                .getGraphVersionCalculated();
        Assertions.assertNotNull(graphVersionCalculated);
    }

    @Test
    @DisplayName("Удаление действия")
    @TmsLink("642530")
    public void deleteAction() {
        String actionName = "action_delete_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        action.deleteObject();
    }
}

