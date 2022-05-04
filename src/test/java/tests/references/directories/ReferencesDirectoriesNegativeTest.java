package tests.references.directories;

import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.references.ReferencesStep.createDirectoryWithInvalidName;

@Tag("references")
@Epic("Справочники")
@Feature("Directories")
@DisabledIfEnv("prod")
public class ReferencesDirectoriesNegativeTest extends Tests {
    private static final String DIRECTORIES_JSON_TEMPLATE = "references/createDirectory.json";

    @DisplayName("Негативный тест на создание directory c именем содержащим недопустимые символы")
    @TmsLink("")
    @Test
    public void createDirectoryWithInvalidNameTest() {
        String dirName = "create_dir_test_api@";
        JSONObject jsonObject = JsonHelper.getJsonTemplate(DIRECTORIES_JSON_TEMPLATE)
                .set("name", dirName)
                .set("description", "description")
                .build();
        Response response = createDirectoryWithInvalidName(jsonObject);
        assertEquals(Collections.singletonList("Can't create object with name: " + dirName), response.jsonPath().get());
    }
}
