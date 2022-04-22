package tests.productCatalog.template;

import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplateNegativeTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json", Configure.ProductCatalogURL);

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

    @DisplayName("Негативный тест на создание шаблона с недопустимыми символами в имени")
    @TmsLink("643607")
    @Test
    public void createTemplateWithInvalidCharacters() {
        assertAll("Шаблон создался с недопустимым именем",
                () -> steps.createProductObject(steps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("a&b&c")).assertStatus(500),
                () -> steps.createProductObject(steps
                        .createJsonObject("")).assertStatus(400),
                () -> steps.createProductObject(steps
                        .createJsonObject(" ")).assertStatus(400)
        );
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
