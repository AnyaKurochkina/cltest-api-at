package api.cloud.references.pages;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static steps.references.ReferencesStep.createPagesJsonObject;
import static steps.references.ReferencesStep.createPrivatePagesAndGetResponse;

@Epic("Справочники")
@Feature("Pages")
@DisabledIfEnv("prod")
public class ReferencesPageNegativeTest extends ReferencesPageBaseTest {

    @DisplayName("Негативный тест на создание pages c именем содержащим недопустимые символы")
    @TmsLink("851368")
    @Test
    public void createPageWithInvalidName() {
        String name = "create_pages_with_invalid_name_test_!";
        createPrivatePagesAndGetResponse(directories.getName(),
                createPagesJsonObject(name, directories.getId())).assertStatus(400);
    }
}
