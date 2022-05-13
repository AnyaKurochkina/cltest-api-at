package tests.references.pages;

import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.references.Directories;
import models.references.Pages;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import tests.Tests;

import java.util.ArrayList;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.references.ReferencesStep.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
public class ReferencesPageTest extends Tests {
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";
    private static final String PAGES_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_pages_api.json";
    List<String> deleteList = new ArrayList<>();
    Directories directories;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_page_test_api",
                "test_api"));
        deleteList.add(directories.getName());
    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @DisplayName("Получение списка Pages по имени Directory для приватных ролей")
    @TmsLink("851370")
    @Test
    public void getPrivatePagesList() {
        String pageName = "get_private_page_list_test_api";
        createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(pageName, directories.getId()));
        List<Pages> pagesList = getPrivatePagesListByDirectoryName(directories.getName());
        assertTrue(pagesList.size() > 0);
        assertTrue(isPageExist(pagesList, pageName, directories.getId()));
    }

    @DisplayName("Получение Pages по Id для приватных ролей")
    @TmsLink("851381")
    @Test
    public void getPrivatePagesByIdTest() {
        String name = "get_pages_private_by_id_test_api";
        Pages createPage = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        Pages getPage = getPrivatePagesById(directories.getName(), createPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectory(), createPage.getDirectory());
    }

    @DisplayName("Обновление Pages по Id для приватных ролей")
    @TmsLink("851382")
    @Test
    public void updatePrivatePagesByIdTest() {
        String name = "update_test_api";
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        String expectedName = "updated_pages_test_api";
        Pages updatedPage = updatePrivatePagesById(directories.getName(), createdPage.getId(),
                createPagesJsonObject(expectedName, directories.getId()));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(updatedPage.getDirectory(), createdPage.getDirectory());
    }

    @DisplayName("Частичное обновление Pages по Id для приватных ролей")
    @TmsLink("851383")
    @Test
    public void partialUpdatePrivatePagesByIdTest() {
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "partial_update_test_api")
                .set("directory", directories.getName())
                .build());
        String expectedName = "expected_partial_updated_pages_test_api";
        String expectedDirectory = createdPage.getDirectory();
        Pages updatedPage = partialUpdatePrivatePagesById(directories.getName(), createdPage.getId(), new JSONObject()
                .put("name", expectedName)
                .put("directory", expectedDirectory));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(expectedDirectory, updatedPage.getDirectory());
    }

    @DisplayName("Получение Pages по Id")
    @TmsLink("851384")
    @Test
    public void getPagesByIdTest() {
        String name = "get_pages_by_id_test_api";
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        Pages getPage = getPagesById(createdPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectory(), directories.getId());
    }

    @DisplayName("Импорт Pages")
    @TmsLink("851386")
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

    @DisplayName("Удаление Pages для приватных полей")
    @TmsLink("851387")
    @Test
    public void deletePrivatePages() {
        String name = "delete_page_test_api";
        Pages pages = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        deletePrivatePagesById(directories.getName(), pages.getId());
    }

    @DisplayName("Обновление Data в Pages по Id для приватных ролей")
    @TmsLink("851405")
    @Test
    public void updateDataPrivatePagesByIdTest() {
        Response response = createPrivatePages(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "updateDataPage_test")
                .set("directory", directories.getName())
                .set("data", new JSONObject().put("key", "value"))
                .build());
        String id = response.jsonPath().get("id");
        String updateExpectedKeyValue = "updateValue";
        updateDataPrivatePagesById(directories.getName(), id, new JSONObject().put("key", updateExpectedKeyValue));
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

    @DisplayName("Получение списка pages")
    @TmsLink("851406")
    @Test
    public void getPagesFilters() {
        String name = "get_pages_list_test_api";
        createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        assertTrue(getPagesList().size() > 0);
    }

    @DisplayName("Экспорт Pages")
    @TmsLink("851407")
    @Test
    public void exportPages() {
        String name = "export_page_test_api";
        Pages page = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        exportPrivatePages(directories.getName(), page.getId());
    }
}
