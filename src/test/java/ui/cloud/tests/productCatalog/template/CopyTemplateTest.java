package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.models.Template;

@Feature("Копирование шаблона")
public class CopyTemplateTest extends TemplateBaseTest {

    @Test
    @TmsLink("487491")
    @DisplayName("Копирование шаблона")
    public void copyTemplateTest() {
        Template template = new Template(NAME);
        String copyName = NAME + "-clone";
        new IndexPage().goToTemplatesPage()
                .findTemplateByValue(NAME, template)
                .copyTemplate(NAME)
                .findAndOpenTemplatePage(copyName)
                .checkTemplateAttributes(new Template(copyName));
        deleteTemplate(copyName);
    }
}
