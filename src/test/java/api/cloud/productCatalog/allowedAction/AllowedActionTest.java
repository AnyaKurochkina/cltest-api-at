package api.cloud.productCatalog.allowedAction;

import api.Tests;
import core.helper.StringUtils;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        AllowedAction action = AllowedAction.builder()
                .title("create_allowed_action_test_api")
                .build()
                .createObject();
        Action actionById = getActionById(action.getActionId());
        AllowedAction actualAction = getAllowedActionById(action.getId());
        assertEquals(action, actualAction);
        assertEquals(actionById.getName() + "__parent_to_child", actualAction.getName());
    }

    @DisplayName("Проверка существования разрешенного действия")
    @TmsLink("1243008")
    @Test
    public void checkAllowedActionExistTest() {
        AllowedAction allowedAction = AllowedAction.builder()
                .title("check_allowed_action_exist_test_api")
                .build()
                .createObject();
        assertTrue(isAllowedActionExists(allowedAction.getName()));
    }

    @DisplayName("Частичное обновление разрешенного действия")
    @TmsLink("1243254")
    @Test
    public void partialUpdateAllowedActionTest() {
        AllowedAction action = AllowedAction.builder()
                .title("partial_update_allowed_action_test_api")
                .build()
                .createObject();
        String description = "update";
        AllowedAction updatedAction = partialUpdateAllowedAction(action.getId(), new JSONObject().put("description", description));
        assertEquals(description, updatedAction.getDescription());
    }

    @DisplayName("Обновление разрешенного действия")
    @TmsLink("1243306")
    @Test
    public void updateAllowedActionTest() {
        AllowedAction action = AllowedAction.builder()
                .title("update_allowed_action_test_api")
                .build()
                .createObject();
        String updatedTitle = "new_update_allowed_action_test_api";
        JSONObject jsonObject = AllowedAction.builder()
                .title("new_update_allowed_action_test_api")
                .build()
                .init().toJson();
        String updatedName = getActionById(jsonObject.get("action").toString()).getName() + "__parent_to_child";
        AllowedAction updatedAction = updateAllowedAction(action.getId(), jsonObject);
        assertEquals(updatedTitle, updatedAction.getTitle());
        assertEquals(updatedName, updatedAction.getName());
    }

    @DisplayName("Удаление по id разрешенного действия")
    @TmsLink("1243320")
    @Test
    public void deleteAllowedActionTest() {
        AllowedAction action = AllowedAction.builder()
                .title("delete_allowed_action_test_api")
                .build()
                .createObject();
        deleteAllowedActionById(action.getId());
        assertFalse(isAllowedActionExists(action.getName()));
    }

    @DisplayName("Копирование разрешенного действия по id")
    @TmsLink("SOUL-7074")
    @Test
    public void copyByIdAllowedActionTest() {
        AllowedAction action = AllowedAction.builder()
                .title("copy_by_id_allowed_action_test_api")
                .build()
                .createObject();
        Action action1 = createAction(StringUtils.getRandomStringApi(7));
        AllowedAction copiedAllowedAction = copyAllowedActionById(action.getId(), new JSONObject().put("action_id", action1.getId()));
        assertTrue(isAllowedActionExists(copiedAllowedAction.getName()));
        assertEquals(action1.getName() + "__parent_to_child", copiedAllowedAction.getName());
    }

    @Test
    @Disabled
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
    @Disabled
    @DisplayName("Выгрузка разрешенного действия из GitLab")
    @TmsLink("1243402")
    public void loadFromGitlabAllowedAction() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
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
