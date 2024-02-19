package api.cloud.productCatalog.template;

import api.Tests;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.ProductCatalogSteps;

import java.util.stream.Stream;

import static core.helper.StringUtils.format;
import static steps.productCatalog.TemplateSteps.*;

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
                .name(templateName)
                .build()
                .createObject();
        steps.getByIdWithOutToken(String.valueOf(template.getId()));
    }

    @DisplayName("Негатичный тест на копирование шаблона по Id без токена")
    @TmsLink("643559")
    @Test
    public void copyTemplateByIdWithOutToken() {
        String templateName = "copy_by_id_with_out_token_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        steps.copyByIdWithOutToken(String.valueOf(template.getId()));
    }

    @DisplayName("Негативный тест на создание шаблона с недопустимым типом")
    @TmsLink("1468815")
    @Test
    public void createTemplateWithInvalidType() {
        String templateName = "create_template_with_invalid_type_test_api";
        if (isTemplateExists(templateName)) {
            deleteTemplateByName(templateName);
        }
        JSONObject template = Template.builder()
                .name(templateName)
                .type("sdf")
                .build()
                .toJson();
        AssertResponse.run(() -> createTemplate(template)).status(400)
                .responseContains("Template cannot be created with this type");
    }

    @DisplayName("Негативный тест на частичное обновление шаблона по Id без токена")
    @TmsLink("643604")
    @Test
    public void partialUpdateTemplateByIdWithOutToken() {
        String templateName = "partial_update_with_out_token_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        steps.partialUpdateObjectWithOutToken(String.valueOf(template.getId()), new JSONObject()
                .put("description", "UpdateDescription"));
    }

    @DisplayName("Негативный тест на создание шаблона с существующим именем")
    @TmsLink("643606")
    @Test
    public void createTemplateWithSameName() {
        String templateName = "create_template_with_same_name_test_api";
        Template.builder()
                .name(templateName)
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
                .name(templateName)
                .run("")
                .build()
                .toJson();
        AssertResponse.run(() -> createTemplate(json)).status(400)
                .responseContains("\\\"run\\\": Это поле не может быть пустым.");
    }

    @DisplayName("Негативный тест на создание шаблона с недопустимыми символами в имени")
    @TmsLink("643607")
    @ParameterizedTest
    @MethodSource("testData")
    public void createTemplateWithInvalidCharacters(String name, String message) {
        AssertResponse.run(() -> createTemplateByName(name)).status(400)
                .responseContains(format(message, name));
    }

    static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("NameWithUppercase", "Cannot instantiate (Template) named ({})"),
                Arguments.of("nameWithUppercaseInMiddle", "Cannot instantiate (Template) named ({})"),
                Arguments.of("Имя", "Cannot instantiate (Template) named ({})"),
                Arguments.of("a&b&c", "Cannot instantiate (Template) named ({})"),
                Arguments.of("", "Это поле не может быть пустым."),
                Arguments.of(" ", "Это поле не может быть пустым.")
        );
    }

    @DisplayName("Негативный тест на удаление шаблона без токена")
    @TmsLink("643614")
    @Test
    public void deleteTemplateWithOutToken() {
        String templateName = "delete_with_out_token_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        steps.deleteObjectByIdWithOutToken(String.valueOf(template.getId()));
    }
}
