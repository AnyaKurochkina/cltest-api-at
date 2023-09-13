package api.cloud.references.pages;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.references.Directories;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import ru.testit.annotations.Title;

import java.util.ArrayList;
import java.util.List;

import static steps.references.ReferencesStep.*;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReferencesPageNegativeTest extends Tests {

    List<String> deleteList = new ArrayList<>();
    Directories negativeDirectories;

    @Title("Создание тестовых данных")
    @DisplayName("Создание тестовых данных")
    @BeforeAll
    public void createTestData() {
        negativeDirectories = createDirectory(createDirectoriesJsonObject("directories_negative_for_page_test_api",
                "test_api"));
        deleteList.add(negativeDirectories.getName());
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
        createPrivatePagesAndGetResponse(negativeDirectories.getName(),
                createPagesJsonObject(name, negativeDirectories.getId())).assertStatus(400);
    }
}
