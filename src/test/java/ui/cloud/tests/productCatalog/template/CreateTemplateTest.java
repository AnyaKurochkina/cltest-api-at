package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.template.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

import java.util.UUID;

@Feature("Создание шаблона")
public class CreateTemplateTest extends TemplateBaseTest {
    private static final String name = "at_ui_create_template_test";

    @Test
    @TmsLink("504210")
    @DisplayName("Создание шаблона узлов")
    public void createTemplateTest() {
        checkTemplateNameValidation();
        createTemplateWithoutRequiredParameters();
        createTemplateWithNonUniqueName();
        createTemplate();
    }

    @Step("Создание шаблона узлов")
    public void createTemplate() {
        template.setName(UUID.randomUUID().toString());
        new IndexPage().goToTemplatesPage()
                .createTemplate(template)
                .checkAttributes(template)
                .deleteTemplate();
    }

    @Step("Создание шаблона без заполнения обязательных полей")
    public void createTemplateWithoutRequiredParameters() {
        new IndexPage().goToTemplatesPage()
                .checkCreateTemplateDisabled(Template.builder().name("").title(TITLE).run(QUEUE_NAME).rollback(QUEUE_NAME)
                        .type(TYPE).build())
                .checkCreateTemplateDisabled(Template.builder().name(name).title("").run(QUEUE_NAME).rollback(QUEUE_NAME)
                        .type(TYPE).build())
                .checkCreateTemplateDisabled(Template.builder().name(name).title(TITLE).run("").rollback(QUEUE_NAME)
                        .type(TYPE).build());
    }

    @Step("Создание шаблона с неуникальным кодом графа")
    public void createTemplateWithNonUniqueName() {
        new IndexPage().goToTemplatesPage()
                .checkNonUniqueNameValidation(template);
    }

    @Step("Создание шаблона с недопустимым кодом")
    public void checkTemplateNameValidation() {
        new IndexPage().goToTemplatesPage()
                .checkTemplateNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }
}
