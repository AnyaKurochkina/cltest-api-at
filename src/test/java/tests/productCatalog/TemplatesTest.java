package tests.productCatalog;

import core.helper.Deleted;
import io.qameta.allure.Feature;
import models.productCatalog.Template;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.productCatalog.TemplateSteps;
import tests.Tests;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
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
                .templateName("TemplateForAT")
                .build()
                .createObject();
    }

    @Order(100)
    @Test
    @DisplayName("Удаление шаблона")
    @Deleted
    public void deleteTemplate() {
        try (Template template = Template.builder()
                .templateName("TemplateForAT")
                .build()
                .createObjectExclusiveAccess()) {
            template.deleteObject();
        }
    }
}
