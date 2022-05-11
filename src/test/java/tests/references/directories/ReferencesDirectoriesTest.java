package tests.references.directories;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.references.Directories;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
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
@Feature("Directories")
@DisabledIfEnv("prod")
public class ReferencesDirectoriesTest extends Tests {

    private static final String DIRECTORIES_DESCRIPTION = "test_api";
    private static final String DIR_IMPORT_PATH = RESOURCE_PATH + "/json/references/import_directories.json";

    List<String> deleteList = new ArrayList<>();

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @DisplayName("Получение списка Directories для приватных ролей")
    @TmsLink("843057")
    @Test
    public void getPrivateDirList() {
        assertTrue(getPrivateDirectoriesList().size() > 0);
    }

    @DisplayName("Получение Directory по имени для приватных ролей")
    @TmsLink("843060")
    @Test
    public void getPrivateDirectoryByNameTest() {
        String dirName = "get_by_name_directories_test_api";
        createDirectory(createDirectoriesJsonObject(dirName, DIRECTORIES_DESCRIPTION));
        deleteList.add(dirName);
        Directories getDirectory = getPrivateDirectoryByName(dirName);
        assertEquals(dirName, getDirectory.getName());
    }

    @DisplayName("Изменение Directory по имени для приватных ролей")
    @TmsLink("843063")
    @Test
    public void updatePrivateDirectoryByNameTest() {
        String name = "update_private_directory_by_name_test_api";
        String expectedName = "updateName_test";
        String expectedDisc = "updateDisc";
        createDirectory(createDirectoriesJsonObject(name, DIRECTORIES_DESCRIPTION));
        Directories updatedDir = updatePrivateDirectoryByName(name, createDirectoriesJsonObject(expectedName, expectedDisc));
        deleteList.add(updatedDir.getName());
        assertEquals(expectedDisc, updatedDir.getDescription());
        assertEquals(expectedName, updatedDir.getName());
    }

    @DisplayName("Частичное изменение Directory по имени для приватных ролей")
    @TmsLink("843064")
    @Test
    public void partialUpdatePrivateDirectoryByNameTest() {
        String name = "partial_update_private_directory_by_name_test_api";
        String expectedDisc = "updateDisc";
        Directories createdDir = createDirectory(createDirectoriesJsonObject(name, DIRECTORIES_DESCRIPTION));
        Directories updatedDir = partialUpdatePrivateDirectoryByName(name, new JSONObject()
                .put("description", expectedDisc));
        deleteList.add(updatedDir.getName());
        assertEquals(expectedDisc, updatedDir.getDescription());
        assertEquals(createdDir.getName(), updatedDir.getName());
    }

    @DisplayName("Получение списка Directories")
    @TmsLink("843066")
    @Test
    public void getDirectionsListTest() {
        assertTrue(getDirectoriesList().size() > 0);
    }

    @DisplayName("Получение Directory по имени")
    @TmsLink("843094")
    @Test
    public void getDirectoryByNameTest() {
        String dirName = "directory_get_by_name_test_api";
        createDirectory(createDirectoriesJsonObject(dirName, DIRECTORIES_DESCRIPTION));
        Directories directories = getDirectoryByName(dirName);
        deleteList.add(dirName);
        assertEquals(dirName, directories.getName());
    }

    @DisplayName("Импорт Directories")
    @TmsLink("843096")
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
    @TmsLink("851277")
    @Test
    public void exportDirectories() {
        String dirName = "export_directory_test_api";
        createDirectory(createDirectoriesJsonObject(dirName, DIRECTORIES_DESCRIPTION));
        exportPrivateDirectories(dirName);
        deleteList.add(dirName);
    }

    @DisplayName("Создание Directories со всеми допустимыми символами")
    @TmsLink("851305")
    @Test
    public void createDirectoriesWithAllValidCharacters() {
        String expectedName = "A-z_2022_:_test_api";
        Directories directories = createDirectory(createDirectoriesJsonObject(expectedName, DIRECTORIES_DESCRIPTION));
        deleteList.add(directories.getName());
        assertEquals(expectedName, directories.getName());
    }

    @DisplayName("Удаление Directories по имени")
    @TmsLink("851314")
    @Test
    public void deleteDirectories() {
        String name = "delete_directories_test_api";
        createDirectory(createDirectoriesJsonObject(name, DIRECTORIES_DESCRIPTION));
        deletePrivateDirectoryByName(name);
    }
}
