package api.cloud.references.pages;

import api.Tests;
import models.cloud.references.Directories;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.List;

import static steps.references.ReferencesStep.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
public class ReferencesPageBaseTest extends Tests {

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
}
