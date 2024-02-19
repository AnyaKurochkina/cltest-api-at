package api.cloud.productCatalog.action;

import core.helper.StringUtils;
import core.helper.http.QueryBuilder;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionsTagTest extends ActionBaseTest {

    @DisplayName("Добавление/Удаление списка Тегов в действиях")
    @TmsLinks({@TmsLink("1700365"), @TmsLink("1700417")})
    @Test
    public void addTagActionTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Action action1 = createAction(createActionModel("add_tag1_test_api"));
        Action action2 = createAction(createActionModel("add_tag2_test_api"));
        addTagListToAction(tagList, new QueryBuilder().add("name__in", format("{},{}", action1.getName(), action2.getName())));
        assertEquals(tagList, getActionById(action1.getId()).getTagList());
        assertEquals(tagList, getActionById(action2.getId()).getTagList());
        removeTagListToAction(tagList, new QueryBuilder().add("name__in", format("{},{}", action1.getName(), action2.getName())));
        assertTrue(getActionById(action1.getId()).getTagList().isEmpty());
        assertTrue(getActionById(action2.getId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в действиях")
    @TmsLink("1700488")
    @Test
    public void checkActionTagListValueTest() {
        List<String> tagList = Arrays.asList("action_tag_test_value", "action_tag_test_value2");
        Action actionModel = createActionModel("at_api_check_tag_list_value");
        actionModel.setTagList(tagList);
        Action action = createAction(actionModel);
        List<String> actionTagList = action.getTagList();
        AssertUtils.assertEqualsList(tagList, actionTagList);
        tagList = Collections.singletonList("action_tag_test_value3");
        partialUpdateAction(action.getId(), new JSONObject().put("tag_list", tagList));
        Action createdAction = getActionById(action.getId());
        AssertUtils.assertEqualsList(tagList, createdAction.getTagList());
    }

    @DisplayName("Проверка не версионности поля tag_list в действиях")
    @TmsLink("1700491")
    @Test
    public void checkActionTagListVersioning() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Action actionModel = createActionModel("at_api_action_check_tag_list_versioning");
        actionModel.setTagList(tagList);
        Action action = createAction(actionModel);
        tagList = Collections.singletonList("test_api3");
        partialUpdateAction(action.getId(), new JSONObject().put("tag_list", tagList));
        Action updatedAction = getActionById(action.getId());
        assertEquals("1.0.0", updatedAction.getVersion());
    }

    @DisplayName("Создание действия с двумя одинаковыми Тегами")
    @TmsLink("1710329")
    @Test
    public void createActionWithSameTagsTest() {
        List<String> tagList = Arrays.asList("same_tag", "same_tag");
        Action actionModel = createActionModel(StringUtils.getRandomStringApi(7));
        actionModel.setTagList(tagList);
        Action action = createAction(actionModel);
        List<String> actionTagList = action.getTagList();
        assertEquals(1, actionTagList.size());
        assertEquals("same_tag", actionTagList.get(0));
    }
}

