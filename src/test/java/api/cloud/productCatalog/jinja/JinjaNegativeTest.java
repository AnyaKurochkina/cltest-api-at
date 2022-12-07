package api.cloud.productCatalog.jinja;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.jinja2.Jinja2;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

@Tag("product_catalog")
@Tag("Jinja")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaNegativeTest extends Tests {

    String template = "productCatalog/jinja2/createJinja.json";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/jinja2_templates/", template);

    @DisplayName("Негативный тест на копирование jinja по Id без токена")
    @TmsLink("660110")
    @Test
    public void copyJinjaByIdWithOutToken() {
        String jinjaName = "copy_with_out_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(jinja2.getId());
    }

    @DisplayName("Негативный тест на получение jinja по Id без токена")
    @TmsLink("660106")
    @Test
    public void getJinjaByIdWithOutToken() {
        String jinjaName = "get_by_id_with_out_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.getByIdWithOutToken(jinja2.getId());
    }

    @DisplayName("Негативный тест на обновление jinja по Id без токена")
    @TmsLink("660185")
    @Test
    public void updateJinjaByIdWithOutToken() {
        String jinjaName = "update_with_out_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(jinja2.getId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание jinja с неуникальным именем")
    @TmsLink("660125")
    @Test
    public void createJinjaWithNonUniqueName() {
        String jinjaName = "update_with_out_jinja_test_api";
        Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(jinjaName)).assertStatus(400);
    }

    @DisplayName("Негативный тест на создание jinja с недопустимыми символами в имени")
    @TmsLink("660126")
    @Test
    public void createJinjaWithInvalidCharacters() {
        Action.builder()
                .actionName("NameWithUppercase")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName("nameWithUppercaseInMiddle")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName("имя")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName("Имя")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName("a&b&c")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName("")
                .build()
                .negativeCreateRequest(400);
        Action.builder()
                .actionName(" ")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на удаление jinja без токена")
    @TmsLink("660179")
    @Test
    public void deleteJinjaWithOutToken() {
        String jinjaName = "delete_with_out_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(jinja2.getId());
    }
}
