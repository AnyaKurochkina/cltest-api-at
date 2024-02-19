package api.cloud.productCatalog.jinja;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.Jinja2Steps.*;

@Tag("product_catalog")
@Tag("Jinja")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaNegativeTest extends Tests {

    @DisplayName("Негативный тест на копирование jinja по Id без токена")
    @TmsLink("660110")
    @Test
    public void copyJinjaByIdWithOutToken() {
        String jinjaName = "copy_with_out_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String errorMessage = copyJinja2ByIdWithOutToken(jinja2.getId()).jsonPath().getString("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на получение jinja по Id без токена")
    @TmsLink("660106")
    @Test
    public void getJinjaByIdWithOutToken() {
        String jinjaName = "get_by_id_with_out_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String errorMessage = getJinja2ByIdWithOutToken(jinja2.getId()).jsonPath().getString("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на обновление jinja по Id без токена")
    @TmsLink("660185")
    @Test
    public void updateJinjaByIdWithOutToken() {
        String jinjaName = "update_with_out_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String errorMessage = partialUpdateJinja2WithOutToken(jinja2.getId(),
                new JSONObject().put("description", "UpdateDescription")).jsonPath().getString("error.message");
        assertEquals("Unauthorized", errorMessage);
    }

    @DisplayName("Негативный тест на создание jinja с неуникальным именем")
    @TmsLink("660125")
    @Test
    public void createJinjaWithNonUniqueName() {
        String jinjaName = "create_jinja_with_not_unique_name_test_api";
        createJinja(jinjaName);
        String errorMessage = createJinja(Role.PRODUCT_CATALOG_ADMIN, Jinja2Template.builder()
                .name(jinjaName)
                .build()
                .toJson())
                .assertStatus(400)
                .extractAs(ErrorMessage.class)
                .getMessage();
        assertEquals("\"name\": jinja2 template с таким name уже существует.", errorMessage);
    }

    @DisplayName("Негативный тест на создание jinja с недопустимыми символами в имени")
    @TmsLink("660126")
    @ParameterizedTest
    @ValueSource(strings = {"NameWithUppercase", "nameWithUppercaseInMiddle", "имя", "Имя", "a&b&c"})
    public void createJinjaWithInvalidCharacters(String name) {
        JSONObject jsonObject = Jinja2Template.builder()
                .name(name)
                .build()
                .toJson();
        String errorMessage = createJinjaResponse(jsonObject)
                .assertStatus(400)
                .extractAs(ErrorMessage.class)
                .getMessage();
        assertEquals(String.format("Cannot instantiate (Jinja2Template) named (%s)", name), errorMessage);
    }

    @DisplayName("Негативный тест на создание jinja с пустым полем name")
    @TmsLink("1620525")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    public void createJinjaWithEmptyNameTest(String name) {
        JSONObject jsonObject = Jinja2Template.builder()
                .name(name)
                .build()
                .toJson();
        String errorMessage = createJinjaResponse(jsonObject)
                .assertStatus(400)
                .extractAs(ErrorMessage.class)
                .getMessage();
        assertEquals("\"name\": Это поле не может быть пустым.", errorMessage);
    }

    @DisplayName("Негативный тест на удаление jinja без токена")
    @TmsLink("660179")
    @Test
    public void deleteJinjaWithOutToken() {
        String jinjaName = "delete_with_out_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String errorMessage = deleteJinja2ByIdWithOutToken(jinja2.getId()).jsonPath().getString("error.message");
        assertEquals("Unauthorized", errorMessage);
    }
}
