package tests.references;

import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.references.Directories;
import models.references.PageFilter;
import models.references.Pages;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.references.ReferencesStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
@Epic("Справочники")
@Feature("Справочники")
public class ReferencesTest extends ReferencesStep {
    private static final String NAME = "create_directory_api_test";
    private static final String DESCRIPTION = "description_create_directory_api_test";
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";
    private static final String DIR_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_directories.json";
    private static final String PAGES_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_pages_api.json";
    private static final String PAGES_FILTER_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_page_filter_api.json";
    List<String> deleteList = new ArrayList<>();
    List<String> deletePageFiltersList = new ArrayList<>();
    Directories directories;
    Pages page;
    PageFilter pageFilter;


    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(new JSONObject()
                .put("name", NAME)
                .put("description", DESCRIPTION));
        deleteList.add(directories.getName());
        String expectedName = "create_pages_test_api";
        String expectedDirectory = directories.getId();
        page = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", expectedName)
                .set("directory", expectedDirectory)
                .build());
        assertEquals(expectedName, page.getName());
        assertEquals(expectedDirectory, page.getDirectoryId());
        pageFilter = createPrivatePageFilter(new JSONObject().put("key", "create_page_filter_api")
                .put("value", Arrays.asList("value", "value2")));
        deletePageFiltersList.add(pageFilter.getKey());

    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
        for (String key : deletePageFiltersList) {
            deletePrivatePageFiltersByKey(key);
        }
    }

    @DisplayName("Получение списка Directories для приватных ролей")
    @Test
    public void getPrivateDirList() {
        assertTrue(getPrivateDirectoriesList().size() > 0);
    }

    @DisplayName("Получение Directory по имени для приватных ролей")
    @Test
    public void getPrivateDirectoryByName() {
        Directories getDirectory = getPrivateDirectoryByName(directories.getName());
        assertEquals(getDirectory.getName(), directories.getName());
    }

    @DisplayName("Изменение Directory по имени для приватных ролей")
    @Test
    public void updatePrivateDirectoryByName() {
        String expectedName = "updateName";
        String expectedDisc = "updateDisc";
        Directories createdDir = createDirectory(new JSONObject()
                .put("name", "created_dir")
                .put("description", "desc"));
        Directories updatedDir = updatePrivateDirectoryByName(createdDir.getName(), new JSONObject()
                .put("name", expectedName)
                .put("description", expectedDisc));
        deleteList.add(updatedDir.getName());
        assertEquals(expectedDisc, updatedDir.getDescription());
        assertEquals(expectedName, updatedDir.getName());
    }

    @DisplayName("Частичное изменение Directory по имени для приватных ролей")
    @Test
    public void partialUpdatePrivateDirectoryByName() {
        String expectedDisc = "updateDisc";
        Directories createdDir = createDirectory(new JSONObject()
                .put("name", "created_dir")
                .put("description", "desc"));
        Directories updatedDir = partialUpdatePrivateDirectoryByName(createdDir.getName(), new JSONObject()
                .put("description", expectedDisc));
        deleteList.add(updatedDir.getName());
        assertEquals(expectedDisc, updatedDir.getDescription());
        assertEquals(createdDir.getName(), updatedDir.getName());
    }

    @DisplayName("Получение списка Pages по имени Directory для приватных ролей")
    @Test
    public void getPrivatePagesList() {
        assertTrue(getPrivatePagesListByDirectoryName(directories.getName()).size() > 0);
    }

    @DisplayName("Получение Pages по Id для приватных ролей")
    @Test
    public void getPrivatePagesById() {
        String name = "get_pages_test_api";
        String directory = page.getDirectoryId();
        Pages createPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", name)
                .set("directory", directory)
                .build());
        Pages getPage = getPrivatePagesById(directories.getName(), createPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectoryId(), directory);
    }

    @DisplayName("Обновление Pages по Id для приватных ролей")
    @Test
    public void updatePrivatePagesById() {
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "update_test_api")
                .set("directory", directories.getName())
                .build());
        String expectedName = "updated_pages_test_api";
        Pages updatedPage = updatePrivatePagesById(directories.getName(), createdPage.getId(), JsonHelper
                .getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "updated_pages_test_api")
                .set("directory", createdPage.getDirectoryId())
                .build());
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(updatedPage.getDirectoryId(), createdPage.getDirectoryId());
    }

    @DisplayName("Частичное обновление Pages по Id для приватных ролей")
    @Test
    public void partialUpdatePrivatePagesById() {
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "partialUpdate_test_api")
                .set("directory", directories.getName())
                .build());
        String expectedName = "partial_updated_pages_test_api";
        String expectedDirectory = createdPage.getDirectoryId();
        Pages updatedPage = partialUpdatePrivatePagesById(directories.getName(), createdPage.getId(), new JSONObject()
                .put("name", expectedName)
                .put("directory", expectedDirectory));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(expectedDirectory, updatedPage.getDirectoryId());
    }

    @DisplayName("Обновление Data в Pages по Id для приватных ролей")
    @Test
    public void updateDataPrivatePagesById() {
        Response response = createPrivatePages(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "updateDataPage")
                .set("directory", directories.getName())
                .set("data", new JSONObject()
                        .put("key", "value"))
                .build());
        String id = response.jsonPath().get("id");
        String updateExpectedKeyValue = "updateValue";
        updateDataPrivatePagesById(directories.getName(), id, new JSONObject()
                .put("key", updateExpectedKeyValue));
        Response updateValue = getPrivateResponsePagesById(directories.getName(), id);
        String getUpdateKeyValue = updateValue.jsonPath().get("data.key").toString();
        assertEquals(updateExpectedKeyValue, getUpdateKeyValue);

        String str = "secondValue";
        updateDataPrivatePagesById(directories.getName(), response.jsonPath().get("id"), new JSONObject()
                .put("secondKey", str));
        Response getResponse = getPrivateResponsePagesById(directories.getName(), id);
        String data = getResponse.jsonPath().get("data").toString();
        assertEquals("{key=updateValue, secondKey=secondValue}", data);
    }

    @DisplayName("Удаление Pages для приватных полей")
    @Test
    public void deletePrivatePages() {
        Pages pages = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "delete_pages_test_api")
                .set("directory", directories.getName())
                .build());
        deletePrivatePagesById(directories.getName(), pages.getId());
    }

    @DisplayName("Получение списка page_filters для приватных ролей")
    @Test
    public void getPrivatePageFilterList() {
        assertTrue(getPrivatePageFiltersList().size() > 0);
    }

    @DisplayName("Создание page_filters для приватных ролей")
    @Test
    public void createPrivatePageFilter() {
        String key = "create_page_filter_test_api";
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        deletePageFiltersList.add(pageFilter.getKey());
        assertEquals(pageFilter.getKey(), key);
        assertEquals(pageFilter.getValue().size(), 2);
    }

    @DisplayName("Получение page_filter по ключу для приватных ролей")
    @Test
    public void getPrivatePageFilter() {
        String key = "get_page_filter_test_api";
        PageFilter pageFilter = createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter getPage = getPrivatePageFilter(key);
        deletePageFiltersList.add(pageFilter.getKey());
        assertEquals(getPage.getKey(), key);
        assertEquals(getPage.getValue().size(), 2);
    }

    @DisplayName("Обновление page_filters для приватных ролей")
    @Test
    public void updatePrivatePageFilter() {
        String key = "update_page_filter_test_api";
        createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter updatedFilter = updatePrivatePageFilter(key, new JSONObject()
                .put("key", "updated_key")
                .put("value", Collections.singletonList("update")));
        deletePageFiltersList.add(updatedFilter.getKey());
        assertEquals(updatedFilter.getKey(), "updated_key");
        assertEquals(updatedFilter.getValue().size(), 1);
    }

    @DisplayName("Частичное обновление page_filters для приватных ролей")
    @Test
    public void partialUpdatePrivatePageFilter() {
        String key = "partial_update_page_filter_test_api";
        createPrivatePageFilter(new JSONObject().put("key", key)
                .put("value", Arrays.asList("data", "data2")));
        PageFilter partialUpdatedFilter = partialUpdatePrivatePageFilter(key, new JSONObject()
                .put("value", Collections.singletonList("partial_update")));
        deletePageFiltersList.add(partialUpdatedFilter.getKey());
        assertEquals(partialUpdatedFilter.getValue().get(0), "partial_update");
    }

    @DisplayName("Получение списка Directories")
    @Test
    public void getDirectionsList() {
        assertTrue(getDirectoriesList().size() > 0);
    }

    @DisplayName("Получение Directory по имени")
    @Test
    public void getDirectoryByName() {
        Directories directories = getDirectoryByName("create_directory_api_test");
        assertEquals("create_directory_api_test", directories.getName());
    }

    @DisplayName("Получение списка page_filters")
    @Test
    public void getPageFilters() {
        assertTrue(getPageFiltersList().size() > 0);
    }

    @DisplayName("Получение page_filter по ключу")
    @Test
    public void getPageFilter() {
        PageFilter getPage = getPageFilter(pageFilter.getKey());
        assertEquals(getPage.getKey(), pageFilter.getKey());
        assertEquals(getPage.getValue().size(), pageFilter.getValue().size());
    }

    @DisplayName("Получение списка pages")
    @Test
    public void getPagesFilters() {
        assertTrue(getPagesList().size() > 0);
    }

    @DisplayName("Получение Pages по Id")
    @Test
    public void getPagesById() {
        String name = "get_pages_by_id_test_api";
        String directory = page.getDirectoryId();
        Pages createPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", name)
                .set("directory", directory)
                .build());
        Pages getPage = getPagesById(createPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectoryId(), directory);
    }

    @DisplayName("Импорт Directories")
    @Test
    public void importDirectories() {
        String data = JsonHelper.getStringFromFile("/references/import_directories.json");
        String directoryName = new JsonPath(data).get("Directory.name");
        importPrivateDirectories(DIR_IMPORT_PATH);
        Directories directories = getPrivateDirectoryByName(directoryName);
        deleteList.add(directories.getName());
        assertEquals(directories.getName(), directoryName);
    }

    @DisplayName("Экспорт Directories")
    @Test
    public void exportDirectories() {
        exportPrivateDirectories(directories.getName());
    }

    @DisplayName("Импорт Pages")
    @Test
    public void importPages() {
        String data = JsonHelper.getStringFromFile("/references/import_pages_api.json");
        String directoryName = new JsonPath(data).get("rel_foreign_models.directory.Directory.name");
        String pageName = new JsonPath(data).get("Page.name");
        importPrivatePages(PAGES_IMPORT_PATH, directoryName);
        List<Pages> list = getPrivatePagesListByDirectoryNameAndPageName(directoryName, pageName);
        deletePrivatePagesById(directoryName, list.get(0).getId());
        assertEquals(1, list.size());
        assertEquals(pageName, list.get(0).getName());
    }

    @DisplayName("Экспорт Pages")
    @Test
    public void exportPages() {
        exportPrivatePages(directories.getName(), page.getId());
    }

    @DisplayName("Импорт Page_filter")
    @Test
    public void importPageFilter() {
        String data = JsonHelper.getStringFromFile("/references/import_page_filter_api.json");
        String key = new JsonPath(data).get("PageFilterValues.key");
        importPrivatePageFilter(PAGES_FILTER_IMPORT_PATH);
        deletePageFiltersList.add(key);
    }
    @DisplayName("Экспорт Page_filter")
    @Test
    public void exportPageFilter() {
        exportPrivatePageFilter(pageFilter.getKey());
    }
}
