package tests.references.directories;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.references.Directories;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.references.ReferencesStep.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
@Epic("Справочники")
@Feature("Directories")
@DisabledIfEnv("prod")
public class ReferencesDirectoriesTest extends Tests {

    private static final String DIRECTORIES_JSON_TEMPLATE = "references/createDirectory.json";
    private static final String DIRECTORIES_DESCRIPTION = "test_api";
    List<String> deleteList = new ArrayList<>();

    @DisplayName("Удаление тестовых данных")
    @AfterAll
    public void deleteTestData() {
        for (String name : deleteList) {
            deletePrivateDirectoryByName(name);
        }
    }

    @DisplayName("Получение списка Directories для приватных ролей")
    @TmsLink("")
    @Test
    public void getPrivateDirList() {
        assertTrue(getPrivateDirectoriesList().size() > 0);
    }

    @DisplayName("Получение Directory по имени для приватных ролей")
    @Test
    public void getPrivateDirectoryByNameTest() {
        String dirName = "get_by_name_directories_test_api";
        JSONObject jsonObject = JsonHelper.getJsonTemplate(DIRECTORIES_JSON_TEMPLATE)
                .set("name", dirName)
                .set("description", DIRECTORIES_DESCRIPTION)
                .build();
        Directories directories = createDirectory(jsonObject);
        deleteList.add(dirName);
        Directories getDirectory = getPrivateDirectoryByName(dirName);
        assertEquals(dirName, getDirectory.getName());
    }
}
