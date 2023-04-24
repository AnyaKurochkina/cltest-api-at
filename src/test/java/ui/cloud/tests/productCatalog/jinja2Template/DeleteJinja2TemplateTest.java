package ui.cloud.tests.productCatalog.jinja2Template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Удаление шаблона Jinja2")
public class DeleteJinja2TemplateTest extends Jinja2TemplateBaseTest {

    @Test
    @TmsLink("713851")
    @DisplayName("Удаление шаблона Jinja2 из списка")
    public void deleteJinja2TemplateFromList() {
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .delete(NAME);
    }

    @Test
    @TmsLink("713319")
    @DisplayName("Удаление шаблона Jinja2 со страницы")
    public void deleteJinja2TemplateFromPage() {
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .findAndOpenJinja2TemplatePage(NAME)
                .delete();
    }
}
