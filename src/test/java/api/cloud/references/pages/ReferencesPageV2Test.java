package api.cloud.references.pages;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.references.Directories;
import models.cloud.references.Pages;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesPageV2Test extends Tests {
    private static final String PAGES_JSON_TEMPLATE = "references/createPages.json";

    List<String> deleteList = new ArrayList<>();
    Directories directories;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        directories = createDirectory(createDirectoriesJsonObject("directories_for_page_v2_test_api",
                "test_api_v2"));
        deleteList.add(directories.getName());
    }

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @DisplayName("Получение Pages по name для приватных ролей")
    @TmsLink("SOUL-8500")
    @Test
    public void getPrivatePagesByNameTest() {
        String name = "get_pages_private_by_id_test_api_v2";
        Pages createPage = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        Pages getPage = getPrivatePagesByName(directories.getName(), createPage.getName());
        assertEquals(getPage.getName(), name);
        assertEquals(getPage.getDirectory(), createPage.getDirectory());
    }

    @DisplayName("Обновление Pages по name для приватных ролей")
    @TmsLink("SOUL-8501")
    @Test
    public void updatePrivatePagesByNameTest() {
        String name = "update_test_api_v2";
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        String expectedName = "updated_pages_test_api_v2";
        Pages updatedPage = updatePrivatePagesByName(directories.getName(), createdPage.getName(),
                createPagesJsonObject(expectedName, directories.getId()));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(updatedPage.getDirectory(), createdPage.getDirectory());
    }

    @DisplayName("Частичное обновление Pages по name для приватных ролей")
    @TmsLink("SOUL-8502")
    @Test
    public void partialUpdatePrivatePagesByNameTest() {
        Pages createdPage = createPrivatePagesAndGet(directories.getName(), JsonHelper.getJsonTemplate(PAGES_JSON_TEMPLATE)
                .set("name", "partial_update_test_api_v2")
                .set("directory", directories.getName())
                .build());
        String expectedName = "expected_partial_updated_pages_test_api_v2";
        String expectedDirectory = createdPage.getDirectory();
        Pages updatedPage = partialUpdatePrivatePagesByName(directories.getName(), createdPage.getName(), new JSONObject()
                .put("name", expectedName)
                .put("directory", expectedDirectory));
        assertEquals(expectedName, updatedPage.getName());
        assertEquals(expectedDirectory, updatedPage.getDirectory());
    }

    @DisplayName("Удаление Pages по name для приватных полей")
    @TmsLink("SOUL-8503")
    @Test
    public void deletePrivatePages() {
        String name = "delete_page_test_api_v2";
        Pages pages = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        deletePrivatePagesByName(directories.getName(), pages.getName());
        assertFalse(isPageExist(getPagesList(), pages.getName(), directories.getId()));
    }

    @DisplayName("Экспорт Pages по name")
    @TmsLink("SOUL-8504")
    @Test
    public void exportPages() {
        String name = "export_page_test_api_v2";
        Pages page = createPrivatePagesAndGet(directories.getName(), createPagesJsonObject(name, directories.getId()));
        exportPrivatePagesByName(directories.getName(), page.getName());
    }
}
