package api.cloud.references.pagesFilter;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.references.PageFilter;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import api.Tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.references.ReferencesStep.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
@Epic("Справочники")
@Feature("PagesFilter")
public class ReferencesPagesFilterTest extends Tests {
    private static final String PAGES_FILTER_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_page_filter_api.json";
    List<String> deletePageFiltersList = new ArrayList<>();

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String key : deletePageFiltersList) {
            deletePrivatePageFiltersByKey(key);
        }
    }

    @DisplayName("Получение списка page_filters для приватных ролей")
    @TmsLink("851645")
    @Test
    public void getPrivatePageFilterList() {
        String key = "create_page_filter_for_get_list_private_test_api";
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        deletePageFiltersList.add(pageFilter.getKey());
        assertTrue(getPrivatePageFiltersList().size() > 0);
    }

    @DisplayName("Создание page_filters для приватных ролей")
    @TmsLink("851648")
    @Test
    public void createPrivatePageFilterTest() {
        String key = "create_page_filter_test_api";
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        deletePageFiltersList.add(pageFilter.getKey());
        assertEquals(pageFilter.getKey(), key);
        assertEquals(pageFilter.getValue().size(), 2);
    }

    @DisplayName("Получение page_filter по ключу для приватных ролей")
    @TmsLink("851649")
    @Test
    public void getPrivatePageFilterTest() {
        String key = "get_page_filter_private_test_api";
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter getPage = getPrivatePageFilter(key);
        deletePageFiltersList.add(pageFilter.getKey());
        assertEquals(getPage.getKey(), key);
        assertEquals(getPage.getValue().size(), 2);
    }

    @DisplayName("Обновление page_filters для приватных ролей")
    @TmsLink("851650")
    @Test
    public void updatePrivatePageFilterTest() {
        String key = "update_page_filter_test_api";
        createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter updatedFilter = updatePrivatePageFilter(key, new JSONObject()
                .put("key", "updated_key_test")
                .put("value", Collections.singletonList("update")));
        deletePageFiltersList.add(updatedFilter.getKey());
        assertEquals(updatedFilter.getKey(), "updated_key_test");
        assertEquals(updatedFilter.getValue().size(), 1);
    }

    @DisplayName("Частичное обновление page_filters для приватных ролей")
    @TmsLink("851652")
    @Test
    public void partialUpdatePrivatePageFilterTest() {
        String key = "partial_update_page_filter_test_api";
        createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter partialUpdatedFilter = partialUpdatePrivatePageFilter(key, new JSONObject()
                .put("value", Collections.singletonList("partial_update")));
        deletePageFiltersList.add(partialUpdatedFilter.getKey());
        assertEquals(partialUpdatedFilter.getValue().get(0), "partial_update");
    }

    @DisplayName("Получение списка page_filters")
    @TmsLink("851654")
    @Test
    public void getPageFilters() {
        String key = "create_page_filter_for_get_list_test_api";
        deletePageFiltersList.add(key);
        createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        assertTrue(getPageFiltersList().size() > 0);
    }

    @DisplayName("Получение page_filter по ключу")
    @TmsLink("851659")
    @Test
    public void getPageFilterTest() {
        String key = "get_page_filter_test_api";
        deletePageFiltersList.add(key);
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter getPage = getPageFilter(pageFilter.getKey());
        assertEquals(getPage.getKey(), pageFilter.getKey());
        assertEquals(getPage.getValue().size(), pageFilter.getValue().size());
    }

    @DisplayName("Импорт page_filter")
    @TmsLink("851661")
    @Test
    public void importPageFilter() {
        String data = JsonHelper.getStringFromFile("/references/import_page_filter_api.json");
        String key = new JsonPath(data).get("PageFilterValues.key");
        importPrivatePageFilter(PAGES_FILTER_IMPORT_PATH);
        deletePageFiltersList.add(key);
    }

    @DisplayName("Экспорт page_filter")
    @TmsLink("851662")
    @Test
    public void exportPageFilter() {
        String key = "export_page_filter_test_api";
        deletePageFiltersList.add(key);
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        exportPrivatePageFilter(pageFilter.getKey());
    }
}
