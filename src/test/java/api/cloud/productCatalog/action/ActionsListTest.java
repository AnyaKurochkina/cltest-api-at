package api.cloud.productCatalog.action;

import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.getAppProp;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionsListTest extends ActionBaseTest {

    @DisplayName("Получение списка действий. Список отсортирован по number и title без учета спец. символов")
    @TmsLink("642429")
    @Test
    public void getActionListTest() {
        createAction(createActionModel("create_action_example_for_get_list_test_api"));
        List<Action> list = getActionList().getList();
        assertTrue(isActionListSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка действий")
    @TmsLink("679025")
    @Test
    public void getMeta() {
        String nextPage = getActionList().getMeta().getNext();
        String url = getAppProp("url.kong");
        if (nextPage != null) {
            assertTrue(nextPage.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка действий по имени")
    @TmsLink("642526")
    @Test
    public void getActionListByNameTest() {
        String actionName = "create_action_example_for_get_list_by_name_test_api";
        createAction(createActionModel(actionName));
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("name", actionName));
        assertEquals(1, list.size());
        assertEquals(actionName, list.get(0).getName());
    }

    @DisplayName("Получение списка действий по именам")
    @TmsLink("783457")
    @Test
    public void getActionListByNamesTest() {
        String actionName = "create_action_example_for_get_list_by_names_1_test_api";
        createAction(createActionModel(actionName));
        String secondAction = "create_action_example_for_get_list_by_names_2_test_api";
        createAction(createActionModel(secondAction));
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("name__in", format("{},{}", actionName, secondAction)));
        assertEquals(2, list.size(), "Список не содержит значений");
        assertEquals(secondAction, list.get(1).getName());
        assertEquals(actionName, list.get(0).getName());
    }

    @DisplayName("Получение списка действий по типу")
    @TmsLink("783463")
    @Test
    public void getActionListByTypeTest() {
        String actionType = "delete";
        Action actionModel = createActionModel("create_action_example_for_get_list_by_type_test_api");
        actionModel.setType(actionType);
        createAction(actionModel);
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("type", actionType));
        list.forEach(item -> assertEquals(actionType, item.getType()));
    }

    @DisplayName("Получение списка действий по title используя multisearch")
    @TmsLink("783469")
    @Test
    public void getActionListByTitleWithMultiSearch() {
        Action actionModel = createActionModel("create_action_example_for_get_list_by_title_with_multi_search_test_api");
        String actionTitle = "action_title";
        actionModel.setTitle(actionTitle);
        createAction(actionModel);
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("multisearch", actionTitle));
        list.forEach(item -> assertTrue(item.getTitle().contains(actionTitle)));
    }

    @DisplayName("Поиск действия по имени, с использованием multiSearch")
    @TmsLink("642503")
    @Test
    public void searchActionByName() {
        Action action = createAction(createActionModel("action_multisearch_test_api"));
        List<Action> list = getActionListWithQueryParam(new QueryBuilder().add("multisearch", action.getName()));
        String id = list.get(0).getId();
        assertEquals(id, action.getId(), "Id действия не совпадают");
    }

    @DisplayName("Получение списка действий с флагом for_items=true")
    @TmsLink("982796")
    @Test
    public void getActionListForItems() {
        List<Action> productObjectList = getActionListWithQueryParam(new QueryBuilder().add("for_items", true));
        productObjectList.forEach(x -> assertNotNull(x.getPrioritise()));
    }

    @DisplayName("Получение списка действий по type_provider_list")
    @TmsLink("1284714")
    @Test
    public void getActionListByTypeProviderTest() {
        JSONObject obj = JsonHelper.getJsonTemplate("/productCatalog/typeProvider.json")
                .set("$.type__provider__list[0].event_type", "vm")
                .set("$.type__provider__list[0].event_provider", "vsphere")
                .build();
        List<Action> actionList = getActionListByTypeProvider(obj);
        checkEventProvider(actionList, "vm", "vsphere");
    }

    @DisplayName("Получение списка действий c Тегами")
    @TmsLink("1700564")
    @Test
    public void getActionListWithTagListTest() {
        Action actionModel = createActionModel("at_api_action_check_tag_list_versioning");
        actionModel.setTagList(Collections.singletonList("api_test"));
        createAction(actionModel);
        List<Action> actionList = getActionListWithQueryParam(new QueryBuilder().add("with_tag_list", true));
        actionList.forEach(x -> assertNotNull(x.getTagList()));
    }

    @DisplayName("Получение списка действий отфильтрованном по Тегам с полным совпадением")
    @TmsLink("1700655")
    @Test
    public void getActionListFilteredByTagsTest() {
        String tag1 = "api_test";
        String tag2 = "complete";
        Action actionModel = createActionModel("at_api_action_check_tag_list_filtered_by_tags");
        actionModel.setTagList(Arrays.asList(tag1, tag2));
        createAction(actionModel);

        Action actionModel2 = createActionModel("action_for_list_filtered_by_tags");
        actionModel2.setTagList(Arrays.asList(tag1, tag2));
        createAction(actionModel2);
        List<Action> actionList = getActionListWithQueryParam(new QueryBuilder().add("with_tag_list", true).add("tags_complete_match", true)
                .add("tags", format("{},{}", tag1, tag2)));

        assertEquals(2, actionList.size());
        actionList.forEach(x -> assertEquals(x.getTagList(), Arrays.asList(tag1, tag2)));
    }

    @DisplayName("Получение списка действий отфильтрованном по Тегам с не полным совпадением")
    @TmsLink("1700662")
    @Test
    public void getActionListFilteredByTagsAndCompleteMatchFalseTest() {
        Action actionModel = createActionModel(StringUtils.getRandomStringApi(9));
        actionModel.setTagList(Arrays.asList("api_test", "api_test_action"));
        createAction(actionModel);
        List<Action> actionList = getActionListWithQueryParam(new QueryBuilder().add("with_tag_list", true).add("tags_complete_match", false)
                .add("tags", "api_test"));
        actionList.forEach(x -> assertTrue(x.getTagList().stream().anyMatch(y -> y.equals("api_test"))));
    }
}
