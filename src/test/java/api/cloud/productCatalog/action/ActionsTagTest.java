package api.cloud.productCatalog.action;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.action.Action;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в действиях")
    @TmsLinks({@TmsLink("1700365"), @TmsLink("1700417")})
    @Test
    public void addTagActionTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Action action1 = createAction("add_tag1_test_api");
        Action action2 = createAction("add_tag2_test_api");
        addTagListToAction(tagList, action1.getName(), action2.getName());
        assertEquals(tagList, getActionById(action1.getActionId()).getTagList());
        assertEquals(tagList, getActionById(action2.getActionId()).getTagList());
        removeTagListToAction(tagList, action1.getName(), action2.getName());
        assertTrue(getActionById(action1.getActionId()).getTagList().isEmpty());
        assertTrue(getActionById(action2.getActionId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в действиях")
    @TmsLink("1700488")
    @Test
    public void checkActionTagListValueTest() {
        List<String> tagList = Arrays.asList("action_tag_test_value", "action_tag_test_value2");
        Action action = Action.builder()
                .name("at_api_check_tag_list_value")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        List<String> actionTagList = action.getTagList();
        assertTrue(tagList.size() == actionTagList.size() && tagList.containsAll(actionTagList) && actionTagList.containsAll(tagList));
        tagList = Collections.singletonList("action_tag_test_value3");
        partialUpdateAction(action.getActionId(), new JSONObject().put("tag_list", tagList));
        Action createdAction = getActionById(action.getActionId());
        AssertUtils.assertEqualsList(tagList, createdAction.getTagList());
    }

    @DisplayName("Проверка не версионности поля tag_list в действиях")
    @TmsLink("1700491")
    @Test
    public void checkActionTagListVersioning() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Action action = Action.builder()
                .name("at_api_action_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        tagList = Collections.singletonList("test_api3");
        partialUpdateAction(action.getActionId(), new JSONObject().put("tag_list", tagList));
        Action updatedAction = getActionById(action.getActionId());
        assertEquals("1.0.0", updatedAction.getVersion());
    }

    @DisplayName("Создание действия с двумя одинаковыми Тегами")
    @TmsLink("1710329")
    @Test
    public void createActionWithSameTagsTest() {
        List<String> tagList = Arrays.asList("same_tag", "same_tag");
        Action action = Action.builder()
                .name(RandomStringUtils.randomAlphabetic(10).toLowerCase() + "action_at_api")
                .title("AT API Product")
                .tagList(tagList)
                .build()
                .createObject();
        List<String> actionTagList = action.getTagList();
        assertEquals(1, actionTagList.size());
        assertEquals("same_tag", actionTagList.get(0));
    }
}

