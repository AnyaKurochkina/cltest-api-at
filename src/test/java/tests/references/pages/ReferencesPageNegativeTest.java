package tests.references.pages;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.references.Directories;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;
import tests.Tests;

import java.util.ArrayList;
import java.util.List;

import static steps.references.ReferencesStep.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("references")
@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
public class ReferencesPageNegativeTest extends Tests {

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

    @DisplayName("Негативный тест на создание pages c именем содержащим недопустимые символы")
    @TmsLink("851368")
    @Test
    public void createPageWithInvalidName() {
        String name = "create_pages_with_invalid_name_test_!";
        createPrivatePagesAndGetResponse(directories.getName(),
                createPagesJsonObject(name, directories.getId())).assertStatus(400);
    }
}
