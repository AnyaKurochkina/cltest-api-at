package api.cloud.productCatalog.action;

import core.helper.StringUtils;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.VersionDiff;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static core.utils.AssertUtils.assertEqualsList;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static tests.routes.ActionProductCatalogApi.*;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionsTest extends ActionBaseTest {

    @DisplayName("Создание действия в продуктовом каталоге")
    @TmsLink("640545")
    @Test
    public void createActionTest() {
        Action action = createActionModel("create_action_test_api");
        Action createdAction = createAction(action);
        Action actualAction = getActionById(createdAction.getId());
        //todo сделать сравнение моделей
        // assertEquals(action, actualAction, "Созданное действие не соответствует ожидаемому");
        assertEquals("create_action_test_api", actualAction.getName());
    }

    @DisplayName("Создание действия в продуктовом каталоге с иконкой")
    @TmsLink("1081243")
    @Test
    public void createActionWithIconTest() {
        Icon icon = Icon.builder()
                .name("icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        Action action = createActionModel("create_action_with_icon_test_api");
        action.setIconStoreId(icon.getId());
        Action createdAction = createAction(action);
        Action actualAction = getActionById(createdAction.getId());
        assertFalse(actualAction.getIconStoreId().isEmpty(), "Поле icon_store_id пустое");
        assertFalse(actualAction.getIconUrl().isEmpty(), "Поле icon_url пустое");
    }

    @DisplayName("Создание нескольких действий в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1081441")
    @Test
    public void createSeveralActionWithSameIconTest() {
        Icon icon = Icon.builder()
                .name("icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();

        Action action = createAction(Action.builder()
                .name("create_first_action_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build());

        Action secondAction = createAction(Action.builder()
                .name("create_second_action_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build());

        Action actualFirstAction = getActionById(action.getId());
        Action actualSecondAction = getActionById(secondAction.getId());
        assertEquals(actualFirstAction.getIconUrl(), actualSecondAction.getIconUrl());
        assertEquals(actualFirstAction.getIconStoreId(), actualSecondAction.getIconStoreId());
    }

    @DisplayName("Проверка существования действия по имени")
    @TmsLink("642432")
    @Test
    public void checkActionExistsTest() {
        String actionName = "action_exist_test_api";
        Action action = createAction(createActionModel(actionName));
        assertTrue(isActionExists(action.getName()), "Действие не существует");
        assertFalse(isActionExists("NoExistsAction"), "Действие существует");
    }

    @DisplayName("Проверка дефолтного значения поля location_restriction в действиях")
    @TmsLink("783425")
    @Test
    public void locationRestrictionCheckDefaultValue() {
        String actionName = "location_restriction_default_value_test_api";
        Action testData = createActionModel(actionName);
        testData.setLocationRestriction(null);
        Action action = createAction(testData);

        Action getAction = getActionById(action.getId());
        assertEquals("", getAction.getLocationRestriction(), "Поле location_restriction не соответсвует ожидаемому");
    }


    @DisplayName("Проверка значения поля location_restriction в действиях")
    @TmsLink("783427")
    @Test
    public void locationRestrictionCheckValue() {
        String actionName = "location_restriction_value_test_api";
        String fieldName = "local";
        Action testData = createActionModel(actionName);
        testData.setLocationRestriction("local");

        Action action = createAction(testData);
        Action getAction = getActionById(action.getId());
        assertEquals(fieldName, getAction.getLocationRestriction());
    }

    @DisplayName("Получение действия по Id")
    @TmsLink("642436")
    @Test
    public void getActionByIdTest() {
        Action action = createAction(createActionModel("get_action_by_id_example_test_api"));
        Action getAction = getActionById(action.getId());
        assertEquals(action, getAction);
    }

    @DisplayName("Получение действия по Id c параметром with_version_fields=true")
    @TmsLink("1284073")
    @Test
    public void getActionByIdWithVersionFieldTest() {
        Action action = createAction(createActionModel("get_action_by_id_with_version_fields_test_api"));
        Action getAction = getActionByIdWithQueryParam(action.getId(), new QueryBuilder().add("with_version_fields", true));
        List<String> versionFields = Arrays.asList("graph_id", "graph_version", "graph_version_pattern", "priority",
                "data_config_path", "data_config_key", "data_config_fields", "item_restriction", "auto_removing_if_failed",
                "ignore_restriction_service", "multiple", "location_restriction", "extra_data", "available_with_cost_reduction",
                "skip_on_prebilling", "available_without_money", "skip_request_resource_pools", "skip_reservation",
                "skip_validate_checker", "skip_restriction_service", "skip_item_change", "object_info");

        assertEqualsList(versionFields, getAction.getVersionFields());
    }

    @DisplayName("Копирование действия по Id")
    @TmsLink("642489")
    @Test
    public void copyActionByIdTest() {
        Action action = createAction(createActionModel("clone_action_test_api"));
        Action cloneAction = copyActionById(action.getId());
        String cloneName = cloneAction.getName();
        assertTrue(isActionExists(cloneName), "Действие не существует");
    }

    @DisplayName("Проверка tag_list при копировании действия")
    @TmsLink("SOUL-7002")
    @Test
    public void copyActionAndCheckTagListTest() {
        Action action = createActionModel("copy_and_check_tag_list_action_test_api");
        action.setTagList(Arrays.asList("api_test", "test"));

        Action createdAction = createAction(action);
        Action cloneAction = copyActionById(createdAction.getId());
        AssertUtils.assertEqualsList(action.getTagList(), cloneAction.getTagList());
    }

    @DisplayName("Копирование действия по Id и проверка на соответствие полей")
    @TmsLink("642493")
    @Test
    public void copyActionByIdAndFieldCheckTest() {
        Action action = createAction(createActionModel("cloned_action_fields_check_test_api"));
        Action cloneAction = copyActionById(action.getId());
        String cloneId = cloneAction.getId();
        Action updatedCloneAction = partialUpdateAction(cloneId, new JSONObject().put("priority", 1))
                .extractAs(Action.class);
        Action actualAction = getActionByIdWithQueryParam(updatedCloneAction.getId(), new QueryBuilder().add("version", updatedCloneAction.getVersion()));
        assertEquals(updatedCloneAction, actualAction);
    }

    @DisplayName("Проверка сортировки по дате создания в действиях")
    @TmsLink("737375")
    @Test
    public void orderingByCreateDataTest() {
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("ordering", "create_dt"));
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
    public void orderingByUpDateDataTest() {
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("ordering", "update_dt"));
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
    public void checkAccessWithPublicTokenTest() {
        String actionName = "action_access_with_public_token_test_api";
        Action action = createAction(createActionModel(actionName));

        getObjectListWithQueryParam(getProductCatalogViewer(), apiV1ActionsList, new QueryBuilder().add("name", actionName));
        AssertResponse.run(() -> createObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsCreate, action.toJson()))
                .status(403);
        AssertResponse.run(() -> partialUpdateObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsPartialUpdate, action.getId(),
                        new JSONObject().put("description", "UpdateDescription")))
                .status(403)
                .responseContains("access_denied");
        AssertResponse.run(() -> putObjectByIdWithPublicToken(getProductCatalogViewer(), apiV1ActionsUpdate, action.getId(), action.toJson()))
                .status(403)
                .responseContains("access_denied");
        AssertResponse.run(() -> deleteObjectWithPublicToken(getProductCatalogViewer(), apiV1ActionsUpdate, action.getId()))
                .status(403)
                .responseContains("access_denied");
    }

    //todo Добавить проверку на allowed groups
    @DisplayName("Проверка независимого от версии поля restricted_groups в действиях")
    @TmsLink("716373")
    @Test
    public void checkVersionWhenIndependentParamUpdated() {
        Action action = createAction(createActionModel("action_check_param_version_test_api"));
        String version = action.getVersion();
        List<String> list = Collections.singletonList("restricted");
        Action updatedAction = partialUpdateAction(action.getId(), new JSONObject().put("restricted_groups", list)).extractAs(Action.class);
        assertEquals(list, updatedAction.getRestrictedGroups(), "Доступные группы не соврадают");
        assertEquals(version, updatedAction.getVersion(), "Версии не совпадают");
    }

    @DisplayName("Обновление действия с указанием версии в граничных значениях")
    @TmsLink("642507")
    @Test
    public void updateActionAndGetVersion() {
        Action action = createActionModel("action_version_test_api");
        action.setVersion("1.0.999");
        action.setPriority(0);
        Action createdAction = createAction(action);
        String currentVersion = partialUpdateAction(createdAction.getId(), new JSONObject().put("priority", 1)).extractAs(Action.class).getVersion();
        Assertions.assertEquals("1.1.0", currentVersion);

        partialUpdateAction(createdAction.getId(), new JSONObject().put("priority", 2)
                .put("version", "1.999.999"));
        currentVersion = partialUpdateAction(createdAction.getId(), new JSONObject().put("priority", 3)).extractAs(Action.class).getVersion();
        Assertions.assertEquals("2.0.0", currentVersion);

        partialUpdateAction(createdAction.getId(), new JSONObject().put("priority", 4)
                .put("version", "999.999.999"));
        AssertResponse.run(() -> partialUpdateAction(createdAction.getId(), new JSONObject().put("priority", 5))).status(400)
                .responseContains("Version counter full [999, 999, 999]");
    }

    @DisplayName("Обновление действия без указания версии, версия должна инкрементироваться")
    @TmsLink("642515")
    @Test
    public void patchActionTest() {
        Action action = createAction(createActionModel("action_patch_test_api"));
        partialUpdateAction(action.getId(), new JSONObject().put("priority", 1));
        assertEquals("1.0.1", getActionById(action.getId()).getVersion(), "Версии не совпадают");
    }

    @DisplayName("Получение ключа graph_version_calculated в ответе на GET запрос в действиях")
    @TmsLink("642524")
    @Test
    public void getKeyGraphVersionCalculatedInResponseTest() {
        Action action = createAction(createActionModel("action_get_key_version_test_api"));
        String graphVersionCalculated = getActionById(action.getId()).getGraphVersionCalculated();
        assertEquals("1.0.0", graphVersionCalculated, "Поле graph_version_calculated не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Удаление действия")
    @TmsLink("642530")
    public void deleteActionTest() {
        Action action = createAction(createActionModel("action_delete_test_api"));
        deleteActionById(action.getId());
        assertFalse(isActionExists(action.getName()));
    }

    @Test
    @DisplayName("Проверка значения current_version в действиях")
    @TmsLink("821963")
    public void checkCurrentVersionActionTest() {
        Action action = createAction(createActionModel("current_version_action_test_api"));
        partialUpdateAction(action.getId(), new JSONObject().put("current_version", "1.0.0"));
        Action getAction = getActionById(action.getId());
        assertEquals("1.0.0", getAction.getCurrentVersion());
    }

    @Test
    @DisplayName("Присвоение значения current_version из списка version_list в действиях")
    @TmsLink("821964")
    public void setCurrentVersionAction() {
        Action action = createAction(createActionModel("set_current_version_action_test_api"));
        String actionId = action.getId();
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
        Action action = createAction(createActionModel("create_current_version_action_test_api"));
        String actionId = action.getId();
        partialUpdateAction(actionId, new JSONObject().put("priority", 2));
        partialUpdateAction(actionId, new JSONObject().put("current_version", "1.0.0"));
        Action getAction = getActionById(actionId);
        assertEquals("1.0.0", getAction.getCurrentVersion());
        assertEquals(action.getPrioritise(), getAction.getPrioritise());
    }

    @Test
    @DisplayName("Получение значения extra_data в действиях")
    @TmsLink("821969")
    public void getExtraDataAction() {
        Action action = createActionModel("extra_data_action_test_api");
        String key = "extra";
        String value = "data";
        action.setExtraData(new LinkedHashMap<String, String>() {{
            put(key, value);
        }});
        Action createdAction = createAction(action);
        Map<String, String> extraData = createdAction.getExtraData();
        assertEquals(extraData.get(key), value);
    }

    @Test
    @DisplayName("Сравнение версий действия")
    @TmsLink("1063081")
    public void compareActionVersionsTest() {
        String actionName = "compare_action_versions_test_api";
        Action action = createAction(createActionModel(actionName));
        partialUpdateAction(action.getId(), new JSONObject().put("priority", 1)
                .put("available_without_money", true));
        Action getAction = getActionByIdWithQueryParam(action.getId(), new QueryBuilder().add("version", "1.0.0").add("compare_with_version", "1.0.1"));
        VersionDiff versionDiff = getAction.getVersionDiff();
        assertAll(
                () -> assertEquals(0, versionDiff.getDiff().get("priority"), "Поле priority не соответствует ожидаемому"),
                () -> assertEquals(false, versionDiff.getDiff().get("available_without_money"), "Поле available_without_money не соответствует ожидаемому"));
    }

    @DisplayName("Создание действия c дефолтным значением number")
    @TmsLink("1143273")
    @Test
    public void createActionWithDefaultNumberTest() {
        Action actionModel = createActionModel("create_action_with_default_number");
        actionModel.setNumber(null);
        Action action = createAction(actionModel);
        Action actualAction = getActionById(action.getId());
        assertEquals(50, actualAction.getNumber(), "Поле number не соответствует ожидаемому");
    }

    @DisplayName("Создание действия с флагом is_safe = true и false")
    @TmsLink("1267102")
    @Test
    public void createActionWithIsSafeTest() {
        Action action = createAction(createActionModel("create_action_with_is_safe_true"));
        Action actualAction = getActionById(action.getId());
        assertTrue(actualAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
        partialUpdateAction(action.getId(), new JSONObject().put("is_safe", false));
        Action updatedAction = getActionById(action.getId());
        assertFalse(updatedAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
    }

    @DisplayName("Создание действия без передачи поля is_safe")
    @TmsLink("1267104")
    @Test
    public void createActionWithoutIsSafeTest() {
        Action actionModel = createActionModel("create_action_without_is_safe_true");
        actionModel.setIsSafe(null);
        Action action = createAction(actionModel);
        Action actualAction = getActionById(action.getId());
        assertFalse(actualAction.getIsSafe(), "Значение флага is_safe не соответсвует ожидаемому");
    }

    @DisplayName("Проверка валидации полей available_without_money, skip_reservation при значении поля skip_on_prebilling = true")
    @TmsLink("1741033")
    @Test
    public void createActionAndCheckFields() {
        Action actionModel = createActionModel(StringUtils.getRandomStringApi(7));
        actionModel.setSkipOnPrebilling(true);
        actionModel.setAvailableWithoutMoney(true);
        actionModel.setSkipReservation(true);
        actionModel.setSkipItemChange(true);
        Action action = createAction(actionModel);
        isActionExists(action.getName());

        AssertResponse.run(() -> createAction(Action.builder()
                        .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "api_test")
                        .skipOnPrebilling(true)
                        .availableWithoutMoney(false)
                        .skipReservation(true)
                        .skipItemChange(true)
                        .build()
                        .toJson())).status(400)
                .responseContains("If the value of the field (skip_on_prebilling) is True," +
                        " the values of the following fields must be True: (available_without_money, skip_reservation)");

        AssertResponse.run(() -> createAction(Action.builder()
                        .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "api_test")
                        .skipOnPrebilling(true)
                        .availableWithoutMoney(true)
                        .skipReservation(false)
                        .skipItemChange(true)
                        .build()
                        .toJson())).status(400)
                .responseContains("If the value of the field (skip_on_prebilling) is True, the values of the following fields must be True: (available_without_money, skip_reservation)");
    }
}

