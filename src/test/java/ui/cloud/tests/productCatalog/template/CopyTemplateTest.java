package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.template.TemplatePage;

@Feature("Копирование шаблона")
public class CopyTemplateTest extends TemplateBaseTest {

    @Test
    @TmsLink("487491")
    @DisplayName("Копирование шаблона")
    public void copyTemplateTest() {
        String copyName = NAME + "-clone";
        new ControlPanelIndexPage().goToTemplatesPage()
                .findTemplateByValue(NAME, template)
                .copy(template.getName());
        template.setName(copyName);
        new TemplatePage()
                .checkAttributes(template);
        deleteTemplate(copyName);
    }
}
