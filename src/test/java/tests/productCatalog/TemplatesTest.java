package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Template;
import org.junit.jupiter.api.*;
import steps.productCatalog.TemplateSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

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
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени.")
    @Test
    public void createTemplateWithInvalidCharacters() {
        assertAll("Шаблон создался с недопустимым именем",
                () -> templateSteps.createProduct(templateSteps.createJsonObject("NameWithUppercase")).assertStatus(400),
                () -> templateSteps.createProduct(templateSteps.createJsonObject("nameWithUppercaseInMiddle")).assertStatus(400),
                () -> templateSteps.createProduct(templateSteps.createJsonObject("имя")).assertStatus(400),
                () -> templateSteps.createProduct(templateSteps.createJsonObject("Имя")).assertStatus(400),
                () -> templateSteps.createProduct(templateSteps.createJsonObject("a&b&c")).assertStatus(400)
        );
    }

    @Order(14)
    @DisplayName("Импорт шаблона")
    @Test
    public void importTemplate() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.json.name");
        templateSteps.importTemplate(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        Assertions.assertTrue(templateSteps.isExist(templateName));
        templateSteps.deleteTemplateByName(templateName);
        Assertions.assertFalse(templateSteps.isExist(templateName));
    }

    @Order(100)
    @Test
    @DisplayName("Удаление шаблона")
    @MarkDelete
    public void deleteTemplate() {
        try (Template template = Template.builder()
                .templateName("template_for_at1")
                .build()
                .createObjectExclusiveAccess()) {
            template.deleteObject();
        }
    }
}
