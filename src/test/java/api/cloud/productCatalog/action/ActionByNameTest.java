package api.cloud.productCatalog.action;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionByNameTest extends ActionBaseTest {

    @DisplayName("Получение действия по имени")
    @TmsLink("1358558")
    @Test
    public void getActionByNameTest() {
        String actionName = "get_action_by_name_example_test_api";
        Action action = createAction(actionName);
        Action getAction = getActionByName(actionName);
        assertEquals(action, getAction);
    }

    @DisplayName("Обновление действия по имени")
    @TmsLink("1358561")
    @Test
    public void patchTest() {
        String actionName = "action_patch_by_name_test_api";
        Action action = createAction(actionName);
        partialUpdateActionByName(actionName, new JSONObject().put("priority", 1));
        assertEquals("1.0.1", getActionById(action.getId()).getVersion(), "Версии не совпадают");
    }

    @Test
    @DisplayName("Удаление действия по имени")
    @TmsLink("1358562")
    public void deleteActionByNameTest() {
        String actionName = "action_delete_by_name_test_api";
        JSONObject jsonObject = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .toJson();
        createAction(jsonObject);
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName));
    }

    @DisplayName("Копирование действия по имени")
    @TmsLink("1358563")
    @Test
    public void copyActionByNameTest() {
        String actionName = "clone_action_by_name_test_api";
        createAction(actionName);
        Action cloneAction = copyActionByName(actionName);
        String cloneName = cloneAction.getName();
        assertTrue(isActionExists(cloneName), "Действие не существует");
        deleteActionByName(cloneName);
        assertFalse(isActionExists(cloneName), "Действие существует");
    }

    @DisplayName("Проверка tag_list при копировании действия v2")
    @TmsLink("SOUL-7001")
    @Test
    public void copyActionAndCheckTagListV2Test() {
        String actionName = "clone_action_v2_test_api";
        Action actionModel = createActionModel(actionName);
        actionModel.setTagList(Arrays.asList("api_test", "test"));
        Action action = createAction(actionModel);
        Action cloneAction = copyActionByName(actionName);
        deleteActionById(cloneAction.getId());
        assertEquals(action.getTagList(), cloneAction.getTagList());
    }

    @Test
    @Disabled
    @DisplayName("Загрузка action в GitLab по имени")
    @TmsLink("1358565")
    public void dumpToGitlabActionByNameTest() {
        String actionName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Action action = createAction(actionName);
        String tag = "action_" + actionName + "_" + action.getVersion();
        Response response = dumpActionToGitByName(actionName);
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @DisplayName("Экспорт действия по имени")
    @TmsLink("1358566")
    @Test
    public void exportActionByNameTest() {
        String actionName = "action_export_by_name_test_api";
        createAction(actionName);
        exportActionByName(actionName);
    }
}
