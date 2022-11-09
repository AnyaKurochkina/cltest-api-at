package api.cloud.productCatalog.template;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.TemplateSteps.createTemplate;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplateNegativeTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");

    @DisplayName("Негативный тест на получение шаблона по Id без токена")
    @TmsLink("643556")
    @Test
    public void getTemplateByIdWithOutToken() {
        String templateName = "get_by_id_with_out_token_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.getByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }

    @DisplayName("Негатичный тест на копирование шаблона по Id без токена")
    @TmsLink("643559")
    @Test
    public void copyTemplateByIdWithOutToken() {
        String templateName = "copy_by_id_with_out_token_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }

    @DisplayName("Негативный тест на частичное обновление шаблона по Id без токена")
    @TmsLink("643604")
    @Test
    public void partialUpdateTemplateByIdWithOutToken() {
        String templateName = "partial_update_with_out_token_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(String.valueOf(template.getTemplateId()), new JSONObject()
                .put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание шаблона с существующим именем")
    @TmsLink("643606")
    @Test
    public void createTemplateWithSameName() {
        String templateName = "create_template_with_same_name_test_api";
        Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.createProductObject(steps.createJsonObject(templateName)).assertStatus(400);
    }

    @DisplayName("Негативный тест на создание шаблона с пустым полем run")
    @TmsLink("1202400")
    @Test
    public void createTemplateWithEmptyFieldRun() {
        String templateName = "create_template_with_empty_name_run_test_api";
        JSONObject json = Template.builder()
                .templateName(templateName)
                .run("")
                .build()
                .init()
                .toJson();
        String errorMessage = createTemplate(json).assertStatus(400).jsonPath().getList("run", String.class).get(0);
        assertEquals("Это поле не может быть пустым.", errorMessage);
    }

    @DisplayName("Негативный тест на создание шаблона с недопустимыми символами в имени")
    @TmsLink("643607")
    @Test
    public void createTemplateWithInvalidCharacters() {
        Template.builder()
                .templateName("NameWithUppercase")
                .build()
                .negativeCreateRequest(500);
        Template.builder()
                .templateName("nameWithUppercaseInMiddle")
                .build()
                .negativeCreateRequest(500);
        Template.builder()
                .templateName("имя")
                .build()
                .negativeCreateRequest(500);
        Template.builder()
                .templateName("Имя")
                .build()
                .negativeCreateRequest(500);
        Template.builder()
                .templateName("a&b&c")
                .build()
                .negativeCreateRequest(500);
        Template.builder()
                .templateName("")
                .build()
                .negativeCreateRequest(400);
        Template.builder()
                .templateName(" ")
                .build()
                .negativeCreateRequest(400);
    }

    @DisplayName("Негативный тест на удаление шаблона без токена")
    @TmsLink("643614")
    @Test
    public void deleteTemplateWithOutToken() {
        String templateName = "delete_with_out_token_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }
}
