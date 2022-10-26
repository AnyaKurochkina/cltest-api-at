package tests.productCatalog.allowedAction;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.allowedAction.AllowedAction;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.AllowedActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
public class AllowedActionTest extends Tests {

    @DisplayName("Создание разрешенного действия в продуктовом каталоге")
    @TmsLink("1242726")
    @Test
    public void createAllowedActionTest() {
        String actionName = "create_allowed_action_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        AllowedAction actualAction = getAllowedActionById(action.getId());
        assertEquals(action, actualAction);
    }

    @DisplayName("Проверка существования разрешенного действия")
    @TmsLink("1243008")
    @Test
    public void checkAllowedActionExistTest() {
        String actionName = "check_allowed_action_exist_test_api";
        AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        assertTrue(isAllowedActionExists(actionName));
    }

    @DisplayName("Проверка существования разрешенного действия")
    @TmsLink("1243008")
    @Test
    public void checkAllowedAction() {
        String actionName = "check_allowed_action_exist_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        checkAllowedActionEvents(action.getId(), action.getEventType().get(0), action.getEventProvider().get(0));
    }

    @DisplayName("Частичное обновление разрешенного действия")
    @TmsLink("1243254")
    @Test
    public void partialUpdateAllowedActionTest() {
        String actionName = "partial_update_allowed_action_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        List<String> expectedEventType = Collections.singletonList("bm");
        AllowedAction updatedAction = partialUpdateAllowedAction(action.getId(), new JSONObject().put("event_type", expectedEventType));
        assertEquals(updatedAction.getEventType(), expectedEventType);
    }

    @DisplayName("Обновление разрешенного действия")
    @TmsLink("1243306")
    @Test
    public void updateAllowedActionTest() {
        String actionName = "update_allowed_action_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String updatedName = "new_update_allowed_action_test_api";
        JSONObject jsonObject = AllowedAction.builder()
                .name(updatedName)
                .title(updatedName)
                .build()
                .init().toJson();
        AllowedAction updatedAction = updateAllowedAction(action.getId(), jsonObject);
        assertEquals(updatedAction.getName(), updatedName);
    }

    @DisplayName("Удаление по id разрешенного действия")
    @TmsLink("1243320")
    @Test
    public void deleteAllowedActionTest() {
        String actionName = "delete_allowed_action_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        deleteAllowedActionById(action.getId());
        assertFalse(isAllowedActionExists(actionName));
    }

    @Test
    @DisplayName("Загрузка разрешенного действия в GitLab")
    @TmsLink("1243400")
    public void dumpToGitlabAllowedAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String tag = "allowedaction_" + actionName;
        Response response = dumpAllowedActionToGit(action.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка разрешенного действия из GitLab")
    @TmsLink("1243402")
    public void loadFromGitlabAllowedAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .init()
                .toJson();
        AllowedAction action = createAllowedAction(jsonObject).extractAs(AllowedAction.class);
        Response response = dumpAllowedActionToGit(action.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteAllowedActionById(action.getId());
        String path = "allowedaction_" + actionName;
        loadAllowedActionFromGit(new JSONObject().put("path", path));
        assertTrue(isAllowedActionExists(actionName));
        deleteAllowedActionByName(actionName);
        assertFalse(isActionExists(actionName));
    }
}
