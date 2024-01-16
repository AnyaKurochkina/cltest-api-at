package api.cloud.references.pages;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.references.Directories;
import models.cloud.references.Pages;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static core.helper.JsonHelper.toJson;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.*;
import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesPageTest extends Tests {
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";
    private static final String PAGES_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_pages_api.json";

    List<String> deleteList = new ArrayList<>();
    Directories directories;
    String directoryName;
    String directoryId;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_page_test_api",
                "test_api"));
        directoryName = directories.getName();
        directoryId = directories.getId();
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
        createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(pageName, directoryId));
        List<Pages> pagesList = getPrivatePagesListByDirectoryName(directoryName);
        assertTrue(pagesList.size() > 0, format("Список page в directory {} пустой", directoryName));
        assertTrue(isPageExist(pagesList, pageName, directoryId),
                format("Page с именем {} не найден в directories {}", pageName, directoryName));
    }

    @DisplayName("Получение Pages по Id для приватных ролей")
    @TmsLink("851381")
    @Test
    public void getPrivatePagesByIdTest() {
        String name = "get_pages_private_by_id_test_api";
        Pages createPage = createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        Pages getPage = getPrivatePagesById(directoryName, createPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectory(), createPage.getDirectory());
    }

    @DisplayName("Обновление Pages по Id для приватных ролей")
    @TmsLink("851382")
    @Test
    public void updatePrivatePagesByIdTest() {
        String name = "update_test_api";
        Pages createdPage = createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        String expectedName = "updated_pages_test_api";
        Pages updatedPage = updatePrivatePagesById(directoryName, createdPage.getId(),
                createPagesJsonObject(expectedName, directoryId));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(updatedPage.getDirectory(), createdPage.getDirectory());
    }

    @DisplayName("Частичное обновление Pages по Id для приватных ролей")
    @TmsLink("851383")
    @Test
    public void partialUpdatePrivatePagesByIdTest() {
        Pages createdPage = createPrivatePagesAndGet(directoryName, JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "partial_update_test_api")
                .set("directory", directoryName)
                .build());
        String expectedName = "expected_partial_updated_pages_test_api";
        String expectedDirectory = createdPage.getDirectory();
        Pages updatedPage = partialUpdatePrivatePagesById(directoryName, createdPage.getId(), new JSONObject()
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
        Pages createdPage = createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        Pages getPage = getPagesById(createdPage.getId());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectory(), directoryId);
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
        assertEquals(1, list.size(), "Кол-во page в Directory не соответствует ожидаемому.");
        assertEquals(pageName, list.get(0).getName(), "Имя page не соответствует ожидаемому.");
    }

    @DisplayName("Удаление Pages для приватных полей")
    @TmsLink("851387")
    @Test
    public void deletePrivatePages() {
        String name = "delete_page_test_api";
        Pages pages = createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        deletePrivatePagesById(directoryName, pages.getId());
        assertFalse(isPageExist(getPrivatePagesListByDirectoryName(directoryName), name, directoryId),
                format("Page с именем {} найден в directories {}", name, directoryName));
    }

    @DisplayName("Получение списка pages")
    @TmsLink("851406")
    @Test
    public void getPagesFilters() {
        String name = "get_pages_list_test_api";
        createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        assertTrue(getPagesList().size() > 0, format("Список page пустой"));
    }

    @DisplayName("Получение списка pages по фильтру data_environment_contains")
    @TmsLink("1740872")
    @Test
    public void getPagesFiltersByDataEnvironmentContains() {
        List<String> testData = Arrays.asList("DEV", "TEST", "PROD");
        for (String str : testData) {
            JSONObject jsonObject = JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                    .set("name", RandomStringUtils.randomAlphabetic(3).toLowerCase() + "_data_environments_filter_test")
                    .set("directory", directoryName)
                    .set("data", new JSONObject().put("environment", Collections.singletonList(str)))
                    .build();
            createPrivatePages(directoryName, jsonObject);
            List<Pages> result = getPagesList(String.format("data__environment__contains=%s", str));
            result.forEach(pages -> assertTrue(new JsonPath(toJson(pages.getData())).getList("environment").contains(str)
                    , String.format("Список не содержит %s", str)));
        }
    }

    @DisplayName("Экспорт Pages")
    @TmsLink("851407")
    @Test
    public void exportPages() {
        String name = "export_page_test_api";
        Pages page = createPrivatePagesAndGet(directoryName, createPagesJsonObject(name, directoryId));
        exportPrivatePages(directories.getName(), page.getId());
    }
}
