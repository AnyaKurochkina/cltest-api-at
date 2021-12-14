package tests.productCatalog;

import core.helper.Deleted;
import io.qameta.allure.Feature;
import models.productCatalog.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.productCatalog.TemplateSteps;
import tests.Tests;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: шаблоны")
public class TemplatesTest extends Tests {

    Template template;
    TemplateSteps templateSteps = new TemplateSteps();

    @Order(1)
    @DisplayName("Создание шаблона в продуктовом каталоге")
    @Test
    public void createTemplate() {
        template = Template.builder()
                .templateName("template_for_at1")
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка шаблонов")
    @Test
    public void getTemplateList() {
        Assertions.assertTrue(templateSteps.getTemplateList().size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка на существование шаблона по имени")
    @Test
    public void existTemplateByName() {
        Assertions.assertTrue(templateSteps.isExist(template.getTemplateName()));
        Assertions.assertFalse(templateSteps.isExist("NoExistsAction"));
    }

    @Order(4)
    @DisplayName("Получение шаблона по ID")
    @Test
    public void getTemplateById() {
        templateSteps.getTemplateById(template.getTemplateId());
    }

    @Order(5)
    @DisplayName("Копирование шаблона по ID и удаление этого клона.")
    @Test
    public void copyTemplateById() {
        templateSteps.copyTemplateById(template.getTemplateId());
        templateSteps.deleteTemplateByName(template.getTemplateName() + "-clone");
    }

    @Order(6)
    @DisplayName("Обновление шаблона по ID.")
    @Test
    public void updateTemplateById() {
        templateSteps.updateTemplateById("Black", template.getTemplateName(), template.getTemplateId());
    }

    @Order(12)
    @DisplayName("Негативный тест на создание действия с существующим именем")
    @Test
    public void createActionWithSameName() {
        templateSteps.createProduct(templateSteps.createJsonObject(template.getTemplateName())).assertStatus(400);
    }

    @Order(13)
    @ParameterizedTest
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени.")
    @MethodSource("dataName")
    public void createTemplateWithInvalidCharacters(String name) {
        JSONObject object = templateSteps.createJsonObject(name);
        templateSteps.createProduct(object).assertStatus(400);
    }

    private static Stream<Arguments> dataName() {
        return Stream.of(
                Arguments.of("NameWithUppercase"),
                Arguments.of("nameWithUppercaseInMiddle"),
                Arguments.of("имя"),
                Arguments.of("Имя"),
                Arguments.of("a&b&c")
        );
    }

    @Order(100)
    @Test
    @DisplayName("Удаление шаблона")
    @Deleted
    public void deleteTemplate() {
        try (Template template = Template.builder()
                .templateName("template_for_at1")
                .build()
                .createObjectExclusiveAccess()) {
            template.deleteObject();
        }
    }
}
