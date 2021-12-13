package tests.productCatalog;

import core.helper.Deleted;
import io.qameta.allure.Feature;
import models.productCatalog.Template;
import org.junit.jupiter.api.*;
import steps.productCatalog.TemplateSteps;
import tests.Tests;

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
