package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.VersionDiff;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;

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
                .name(actionName)
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
                .name(actionName)
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
                .name(actionName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        Action secondAction = Action.builder()
                .name("create_second_action_with_same_icon_test_api")
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
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        assertTrue(isActionExists(action.getName()), "Действие не существует");
        assertFalse(isActionExists("NoExistsAction"), "Действие существует");
    }

    @DisplayName("Проверка дефолтного значения поля location_restriction в действиях")
    @TmsLink("783425")
    @Test
    public void locationRestrictionCheckDefaultValue() {
        String actionName = "location_restriction_default_value_test_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name(actionName)
                .title(actionName)
                .locationRestriction(fieldName)
                .build()
                .createObject();
        Action getAction = getActionById(action.getActionId());
        assertEquals(fieldName, getAction.getLocationRestriction());
    }

    @DisplayName("Получение действия по Id")
    @TmsLink("642436")
    @Test
    public void getActionByIdTest() {
        String actionName = "get_action_by_id_example_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        Action getAction = getActionById(action.getActionId());
        assertEquals(action, getAction);
    }

    @DisplayName("Получение действия по Id c параметром with_version_fields=true")
    @TmsLink("1284073")
    @Test
    public void getActionByIdWithVersionFieldTest() {
        String actionName = "get_action_by_id_with_version_fields_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        Action getAction = getActionByFilter(action.getActionId(), "with_version_fields=true");
        List<String> versionFields = Arrays.asList("graph_id", "graph_version", "graph_version_pattern", "priority",
                "data_config_path", "data_config_key", "data_config_fields", "item_restriction", "auto_removing_if_failed",
                "ignore_restriction_service", "multiple", "location_restriction", "extra_data", "available_with_cost_reduction",
                "skip_on_prebilling", "available_without_money", "skip_request_resource_pools", "skip_reservation",
                "skip_validate_checker", "skip_restriction_service", "skip_item_change");
        assertEquals(versionFields, getAction.getVersionFields());
    }

    @DisplayName("Копирование действия по Id")
    @TmsLink("642489")
    @Test
    public void copyActionByIdTest() {
        String actionName = "clone_action_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        Action cloneAction = copyActionById(action.getActionId());
        String cloneName = cloneAction.getName();
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
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String cloneName = action.getName() + "-clone";
        Action cloneAction = copyActionById(action.getActionId());
        String cloneId = cloneAction.getActionId();
        Action updatedCloneAction = partialUpdateAction(cloneId, new JSONObject().put("priority", 1))
                .extractAs(Action.class);
        Action actualAction = getActionByFilter(updatedCloneAction.getActionId(), "version=" + updatedCloneAction.getVersion());
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
                                , list.get(i).getName(), list.get(i + 1).getName()));
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
                .name(actionName)
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

    //todo Добавить проверку на allowed groups
    @DisplayName("Проверка независимого от версии поля restricted_groups в действиях")
    @TmsLink("716373")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        String actionName = "action_check_param_version_test_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name("action_version_test_api")
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
        String errorMessage = partialUpdateAction(actionTest.getActionId(), new JSONObject().put("priority", 5))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Version counter full [999, 999, 999]", errorMessage);
    }

    @DisplayName("Обновление действия без указания версии, версия должна инкрементироваться")
    @TmsLink("642515")
    @Test
    public void patchTest() {
        String actionName = "action_patch_test_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String graphVersionCalculated = getActionById(action.getActionId())
                .getGraphVersionCalculated();
        assertEquals("1.0.0", graphVersionCalculated);
    }

    @Test
    @DisplayName("Удаление действия")
    @TmsLink("642530")
    public void deleteAction() {
        String actionName = "action_delete_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        String graphId = createGraph(RandomStringUtils.randomAlphabetic(10).toLowerCase()).getGraphId();
        JSONObject action = Action.builder()
                .name(actionName)
                .title(actionName)
                .graphId(graphId)
                .build()
                .toJson();
        String id = createAction(action).extractAs(Action.class).getActionId();
        deleteActionById(id);
        assertFalse(isActionExists(actionName));
    }

    @Test
    @DisplayName("Проверка значения current_version в действиях")
    @TmsLink("821963")
    public void checkCurrentVersionAction() {
        String actionName = "current_version_action_test_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name(actionName)
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
                .name(actionName)
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
                .name(actionName)
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
    @Disabled
    @DisplayName("Загрузка action в GitLab")
    @TmsLink("975375")
    public void dumpToGitlabAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Action action = Action.builder()
                .name(actionName)
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
    @Disabled
    @DisplayName("Выгрузка action из GitLab")
    @TmsLink("1028840")
    public void loadFromGitlabAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        Action action = Action.builder()
                .name(actionName)
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
                .name(actionName)
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
                .name(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertEquals(50, actualAction.getNumber());
    }

    @DisplayName("Создание действия с флагом is_safe = true и false")
    @TmsLink("1267102")
    @Test
    public void createActionWithIsSafe() {
        String actionName = "create_action_with_is_safe_true";
        Action action = Action.builder()
                .name(actionName)
                .isSafe(true)
                .version("1.0.1")
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertTrue(actualAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
        partialUpdateAction(action.getActionId(), new JSONObject().put("is_safe", false));
        Action updatedAction = getActionById(action.getActionId());
        assertFalse(updatedAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
    }

    @DisplayName("Создание действия без передачи поля is_safe")
    @TmsLink("1267104")
    @Test
    public void createActionWithoutIsSafe() {
        String actionName = "create_action_without_is_safe_true";
        Action action = Action.builder()
                .name(actionName)
                .version("1.0.1")
                .build()
                .createObject();
        Action actualAction = getActionById(action.getActionId());
        assertFalse(actualAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
    }

    @DisplayName("Проверка валидации полей available_without_money, skip_reservation при значении поля skip_on_prebilling = true")
    @TmsLink("1741033")
    @Test
    public void createActionAndCheckFields() {
        Action action = Action.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "api_test")
                .skipOnPrebilling(true)
                .availableWithoutMoney(true)
                .skipReservation(true)
                .skipItemChange(true)
                .build()
                .createObject();
        isActionExists(action.getName());

        String errMessage = createAction(Action.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "api_test")
                .graphId(createGraph().getGraphId())
                .skipOnPrebilling(true)
                .availableWithoutMoney(false)
                .skipReservation(true)
                .skipItemChange(true)
                .build()
                .toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Если значение поля (skip_on_prebilling) True, значения следующий полей должны быть True: (available_without_money, skip_reservation)",
                errMessage);

        String errMessage2 = createAction(Action.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "api_test")
                .graphId(createGraph().getGraphId())
                .skipOnPrebilling(true)
                .availableWithoutMoney(true)
                .skipReservation(false)
                .skipItemChange(true)
                .build()
                .toJson()).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Если значение поля (skip_on_prebilling) True, значения следующий полей должны быть True: (available_without_money, skip_reservation)",
                errMessage2);
    }

}

