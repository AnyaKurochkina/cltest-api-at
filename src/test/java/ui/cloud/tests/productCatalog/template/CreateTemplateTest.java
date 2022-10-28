package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.uiModels.Template;

@Epic("Шаблоны узлов")
@Feature("Создание шаблона")
public class CreateTemplateTest extends TemplateBaseTest {
    private static final String name = "at_ui_create_template_test";

    @Test
    @TmsLink("504210")
    @DisplayName("Создание шаблона узлов")
    public void createTemplateTest() {
        checkTemplateNameValidation();
        createTemplate();
        createTemplateWithoutRequiredParameters();
        createTemplateWithNonUniqueName();
    }

    @Step("Создание шаблона узлов")
    public void createTemplate() {
        Template template = new Template(name);
        new IndexPage().goToTemplatesPage()
                .createTemplate(template)
                .checkTemplateAttributes(template)
                .deleteTemplate();
    }

    @Step("Создание шаблона без заполнения обязательных полей")
    public void createTemplateWithoutRequiredParameters() {
        new IndexPage().goToTemplatesPage()
                .checkCreateTemplateDisabled(new Template("", TITLE, QUEUE_NAME, QUEUE_NAME, TYPE))
                .checkCreateTemplateDisabled(new Template(name, "", QUEUE_NAME, QUEUE_NAME, TYPE))
                .checkCreateTemplateDisabled(new Template(name, TITLE, "", QUEUE_NAME, TYPE));
    }

    @Step("Создание шаблона с неуникальным кодом графа")
    public void createTemplateWithNonUniqueName() {
        new IndexPage().goToTemplatesPage()
                .checkNonUniqueNameValidation(new Template(NAME));
    }

    @Step("Создание шаблона с недопустимым кодом")
    public void checkTemplateNameValidation() {
        new IndexPage().goToTemplatesPage()
                .checkTemplateNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }
}
