package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
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

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionsListTest extends Tests {

    @DisplayName("Получение списка действий. Список отсортирован по number и title без учета спец. символов")
    @TmsLink("642429")
    @Test
    public void getActionListTest() {
        String actionName = "create_action_example_for_get_list_test_api";
        Action.builder()
                .name(actionName)
                .build()
                .createObject();
        List<Action> list = getActionList();
        assertTrue(isActionListSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка действий")
    @TmsLink("679025")
    @Test
    public void getMeta() {
        String nextPage = getMetaActionList().getNext();
        String url = getAppProp("url.kong");
        if (!(nextPage == null)) {
            assertTrue(nextPage.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка действий по имени")
    @TmsLink("642526")
    @Test
    public void getActionListByNameTest() {
        String actionName = "create_action_example_for_get_list_by_name_test_api";
        Action.builder()
                .name(actionName)
                .build()
                .createObject();
        List<Action> list = getActionListByName(actionName);
        assertEquals(1, list.size());
        assertEquals(actionName, list.get(0).getName());
    }

    @DisplayName("Получение списка действий по именам")
    @TmsLink("783457")
    @Test
    public void getActionListByNamesTest() {
        String actionName = "create_action_example_for_get_list_by_names_1_test_api";
        Action.builder()
                .name(actionName)
                .title("api_test")
                .build()
                .createObject();
        String secondAction = "create_action_example_for_get_list_by_names_2_test_api";
        Action.builder()
                .name(secondAction)
                .title("test")
                .build()
                .createObject();
        List<Action> list = getActionListByNames(actionName, secondAction);
        assertEquals(2, list.size(), "Список не содержит значений");
        assertEquals(secondAction, list.get(1).getName());
        assertEquals(actionName, list.get(0).getName());
    }

    @DisplayName("Получение списка действий по типу")
    @TmsLink("783463")
    @Test
    public void getActionListByTypeTest() {
        String actionName = "create_action_example_for_get_list_by_type_test_api";
        String actionType = "delete";
        Action.builder()
                .name(actionName)
                .type(actionType)
                .build()
                .createObject();
        List<Action> list = getActionListByType(actionType);
        for (Action item : list) {
            assertEquals(actionType, item.getType());
        }
    }

    @DisplayName("Получение списка действий по title используя multisearch")
    @TmsLink("783469")
    @Test
    public void getActionListByTitleWithMutisearch() {
        String actionName = "create_action_example_for_get_list_by_title_with_multisearch_test_api";
        String actionTitle = "action_title";
        Action.builder()
                .name(actionName)
                .title(actionTitle)
                .build()
                .createObject();
        List<Action> list = getActionListWithMultiSearch(actionTitle);
        for (Action item : list) {
            assertTrue(item.getTitle().contains(actionTitle));
        }
    }

    @DisplayName("Поиск действия по имени, с использованием multiSearch")
    @TmsLink("642503")
    @Test
    public void searchActionByName() {
        String actionName = "action_multisearch_test_api";
        Action action = Action.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        String actionIdWithMultiSearch = getActionIdByNameWithMultiSearch(action.getName());
        assertAll(
                () -> assertNotNull(actionIdWithMultiSearch, String.format("Действие с именем: %s не найден", actionName)),
                () -> assertEquals(action.getActionId(), actionIdWithMultiSearch, "Id действия не совпадают"));
    }

    @DisplayName("Получение списка действий с флагом for_items=true")
    @TmsLink("982796")
    @Test
    public void getActionListForItems() {
        List<Action> productObjectList = getActionListByFilter("for_items", true);
        productObjectList.forEach(x -> assertNotNull(x.getPriority()));
    }

    @DisplayName("Получение списка действий по type_provider_list")
    @TmsLink("1284714")
    @Test
    public void getActionListByTypeProviderTest() {
        JSONObject obj = JsonHelper.getJsonTemplate("/productCatalog/typeProvider.json")
                .set("$.type__provider__list[0].event_type", "vm")
                .set("$.type__provider__list[0].event_provider", "vsphere")
                .build();
        List<Action> actionList = getActionListByTypeProvider(obj );
        checkEventProvider(actionList, "vm", "vsphere");
    }

    @DisplayName("Получение списка действий c Тегами")
    @TmsLink("1700564")
    @Test
    public void getActionListWithTagListTest() {
        Action.builder()
                .name("at_api_action_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Collections.singletonList("api_test"))
                .build()
                .createObject();
        List<Action> actionList = getActionListByFilter("with_tag_list", true);
        actionList.forEach(x -> assertNotNull(x.getTagList()));
    }

    @DisplayName("Получение списка действий отфильтрованном по Тегам с полным совпадением")
    @TmsLink("1700655")
    @Test
    public void getActionListFilteredByTagsTest() {
        String tag1 = "api_test";
        String tag2 = "complete";
        Action.builder()
                .name("at_api_action_check_tag_list_filtered_by_tags")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList(tag1, tag2))
                .build()
                .createObject();
        Action.builder()
                .name("action_for_list_filtered_by_tags")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList(tag1, tag2))
                .build()
                .createObject();
        List<Action> actionList = getActionListByFilters("with_tag_list=true", "tags_complete_match=true",
                String.format("tags=%s,%s", tag1, tag2));
        assertEquals(2, actionList.size());
        actionList.forEach(x-> assertEquals(x.getTagList(), Arrays.asList(tag1, tag2)));
    }

    @DisplayName("Получение списка действий отфильтрованном по Тегам с не полным совпадением")
    @TmsLink("1700662")
    @Test
    public void getActionListFilteredByTagsAndCompleteMatchFalseTest() {
        Action.builder()
                .name(RandomStringUtils.randomAlphabetic(10).toLowerCase()+ "action_at_ui")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList("api_test", "api_test_action"))
                .build()
                .createObject();
        List<Action> actionList = getActionListByFilters("with_tag_list=true", "tags_complete_match=false", "tags=api_test");
        actionList.forEach(x -> assertTrue(x.getTagList().stream().anyMatch(y -> y.equals("api_test"))));
    }
}
