package tests.productCatalog.action;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.VersionDiff;
import models.productCatalog.action.Action;
import models.productCatalog.icon.Icon;
import models.productCatalog.icon.IconStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/actions/",
            "productCatalog/actions/createAction.json");

    @DisplayName("Создание действия в продуктовом каталоге")
    @TmsLink("640545")
    @Test
    public void createActionTest() {
        String actionName = "create_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertEquals(action, actualAction);
    }

    @DisplayName("Создание действия в продуктовом каталоге с иконкой")
    @TmsLink("1081243")
    @Test
    public void createActionWithIcon() {
        Icon icon = Icon.builder()
                .name("icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String actionName = "create_action_with_icon_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertFalse(actualAction.getIconStoreId().isEmpty());
        assertFalse(actualAction.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких действий в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1081441")
    @Test
    public void createSeveralActionWithSameIcon() {
        Icon icon = Icon.builder()
                .name("icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String actionName = "create_first_action_with_same_icon_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Action secondAction = Action.builder()
                .actionName("create_second_action_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Action actualFirstAction = getActionById(action.getActionId());
        Action actualSecondAction = getActionById(secondAction.getActionId());
        assertEquals(actualFirstAction.getIconUrl(), actualSecondAction.getIconUrl());
        assertEquals(actualFirstAction.getIconStoreId(), actualSecondAction.getIconStoreId());
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
        assertTrue(isActionExists(action.getActionName()), "Действие не существует");
        assertFalse(isActionExists("NoExistsAction"), "Действие существует");
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
        Action getAction = getActionById(action.getActionId());
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
        Action getAction = getActionById(action.getActionId());
        assertEquals(fieldName, getAction.getLocationRestriction());
    }

    @DisplayName("Импорт действия")
    @TmsLink("642433")
    @Test
    public void importActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Импорт действия c иконкой")
    @TmsLink("1085391")
    @Test
    public void importActionWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importActionWithIcon.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importActionWithIcon.json");
        String id = getActionIdByNameWithMultiSearch(actionName);
        Action action = getActionById(id);
        assertFalse(action.getIconStoreId().isEmpty());
        assertFalse(action.getIconUrl().isEmpty());
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Получение действия по Id")
    @TmsLink("642436")
    @Test
    public void getActionByIdTest() {
        String actionName = "get_action_by_id_example_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        Action getAction = getActionById(action.getActionId());
        assertEquals(action, getAction);
    }

    @DisplayName("Копирование действия по Id")
    @TmsLink("642489")
    @Test
    public void copyActionByIdTest() {
        String actionName = "clone_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        Action cloneAction = copyActionById(action.getActionId());
        String cloneName = cloneAction.getActionName();
        assertTrue(isActionExists(cloneName), "Действие не существует");
        deleteActionByName(cloneName);
        assertFalse(isActionExists(cloneName), "Действие существует");
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
        Action cloneAction = copyActionById(action.getActionId());
        String cloneId = cloneAction.getActionId();
        Action updatedCloneAction = partialUpdateAction(cloneId, new JSONObject().put("priority", 1))
                .extractAs(Action.class);
        Action actualAction = getActionByFilter(updatedCloneAction.getActionId(), "version", updatedCloneAction.getVersion());
        assertEquals(updatedCloneAction, actualAction);
        assertTrue(isActionExists(cloneName));
        deleteActionByName(cloneName);
        assertFalse(isActionExists(cloneName));
    }

    @DisplayName("Проверка сортировки по дате создания в действиях")
    @TmsLink("737375")
    @Test
    public void orderingByCreateData() {
        List<Action> list = orderingActionByCreateData();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
                ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
                assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                        String.format("Даты создания действий с именами %s и %s не соответсвуют условию сортировки."
                                , list.get(i).getActionName(), list.get(i + 1).getActionName()));
            }
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в действиях")
    @TmsLink("737383")
    @Test
    public void orderingByUpDateData() {
        List<Action> list = orderingActionByUpDateData();
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
                ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
                assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                        String.format("Даты обновлений действий с именами %s и %s не соответсвуют условию сортировки."
                                , list.get(i).getActionName(), list.get(i + 1).getActionName()));
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

    //todo Добавить проверку на allowed groups
    @DisplayName("Проверка независимого от версии поля restricted_groups в действиях")
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
        partialUpdateAction(action.getActionId(), new JSONObject().put("restricted_groups", list));
        List<String> restrictedGroups = getActionById(id).getRestrictedGroups();
        String newVersion = getActionById(id).getVersion();
        assertEquals(list, restrictedGroups, "Доступные группы не соврадают");
        assertEquals(version, newVersion, "Версии не совпадают");
    }

    @DisplayName("Обновление действия с указанием версии в граничных значениях")
    @TmsLink("642507")
    @Test
    public void updateActionAndGetVersion() {
        Action actionTest = Action.builder()
                .actionName("action_version_test_api")
                .version("1.0.999")
                .priority(0)
                .build()
                .createObject();
        partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 1));
        String currentVersion = getActionById(actionTest.getActionId()).getVersion();
        Assertions.assertEquals("1.1.0", currentVersion);
        partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 2)
                .put("version", "1.999.999"));
        partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 3));
        currentVersion = getActionById(actionTest.getActionId()).getVersion();
        Assertions.assertEquals("2.0.0", currentVersion);
        partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 4)
                .put("version", "999.999.999"));
        partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 5))
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
                .priority(0)
                .build()
                .createObject();
        partialUpdateAction(action.getActionId(), new JSONObject().put("priority", 1));
        assertEquals("1.0.1", getActionById(action.getActionId()).getVersion(), "Версии не совпадают");
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
        String graphVersionCalculated = getActionById(action.getActionId())
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
        deleteActionById(action.getActionId());
    }

    @Test
    @DisplayName("Проверка значения current_version в действиях")
    @TmsLink("821963")
    public void checkCurrentVersionAction() {
        String actionName = "current_version_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        partialUpdateAction(action.getActionId(), new JSONObject().put("current_version", "1.0.0"));
        Action getAction = getActionById(action.getActionId());
        assertEquals("1.0.0", getAction.getCurrentVersion());
    }

    @Test
    @DisplayName("Присвоение значения current_version из списка version_list в действиях")
    @TmsLink("821964")
    public void setCurrentVersionAction() {
        String actionName = "set_current_version_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .priority(0)
                .build()
                .createObject();
        String actionId = action.getActionId();
        partialUpdateAction(actionId, new JSONObject().put("priority", 1));
        partialUpdateAction(actionId, new JSONObject().put("current_version", "1.0.1"));
        Action getAction = getActionById(actionId);
        assertEquals("1.0.1", getAction.getCurrentVersion());
        assertTrue(getAction.getVersionList().contains(getAction.getCurrentVersion()));
    }

    @Test
    @DisplayName("Получение экшена версии указанной в current_version")
    @TmsLink("821967")
    public void getCurrentVersionAction() {
        String actionName = "create_current_version_action_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .priority(0)
                .build()
                .createObject();
        String actionId = action.getActionId();
        partialUpdateAction(actionId, new JSONObject().put("priority", 2));
        partialUpdateAction(actionId, new JSONObject().put("current_version", "1.0.0"));
        Action getAction = getActionById(actionId);
        assertEquals("1.0.0", getAction.getCurrentVersion());
        assertEquals(action.getPriority(), getAction.getPriority());
    }

    @Test
    @DisplayName("Получение значения extra_data в действиях")
    @TmsLink("821969")
    public void getExtraDataAction() {
        String actionName = "extra_data_action_test_api";
        String key = "extra";
        String value = "data";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .extraData(new LinkedHashMap<String, String>() {{
                    put(key, value);
                }})
                .build()
                .createObject();
        Action getActionById = getActionById(action.getActionId());
        Map<String, String> extraData = getActionById.getExtraData();
        assertEquals(extraData.get(key), value);
    }

    @Test
    @DisplayName("Загрузка action в GitLab")
    @TmsLink("975375")
    public void dumpToGitlabAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "action_" + actionName + "_" + action.getVersion();
        Response response = dumpActionToGit(action.getActionId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка action из GitLab")
    @TmsLink("1028840")
    public void loadFromGitlabAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .version("1.0.0")
                .build()
                .createObject();
        Response response = dumpActionToGit(action.getActionId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteActionById(action.getActionId());
        String path = "action_" + actionName + "_" + action.getVersion();
        loadActionFromGit(new JSONObject().put("path", path));
        assertTrue(isActionExists(actionName));
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName));
    }

    @Test
    @DisplayName("Сравнение версий действия")
    @TmsLink("1063081")
    public void compareActionVersionsTest() {
        String actionName = "compare_action_versions_test_api";
        Action action = Action.builder()
                .actionName(actionName)
                .title(actionName)
                .build()
                .createObject();
        partialUpdateAction(action.getActionId(), new JSONObject().put("priority", 1)
                .put("available_without_money", true));
        Action getActionResponse = compareActionVersions(action.getActionId(), "1.0.0", "1.0.1");
        VersionDiff versionDiff = getActionResponse.getVersionDiff();
        assertEquals(versionDiff.getDiff().get("priority"), 0);
        assertEquals(versionDiff.getDiff().get("available_without_money"), false);
    }

    @DisplayName("Создание действия c дефолтным значением number")
    @TmsLink("1143273")
    @Test
    public void createActionWithDefaultNumber() {
        String actionName = "create_action_with_default_number";
        Action action = Action.builder()
                .actionName(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertEquals(50, actualAction.getNumber());
    }
}

