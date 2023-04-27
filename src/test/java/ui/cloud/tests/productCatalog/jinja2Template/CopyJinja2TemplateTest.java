package ui.cloud.tests.productCatalog.jinja2Template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.jinja2Template.Jinja2TemplatePage;

import static steps.productCatalog.Jinja2Steps.deleteJinjaByName;

@Feature("Создание шаблона Jinja2")
public class CopyJinja2TemplateTest extends Jinja2TemplateBaseTest {

    @Test
    @TmsLink("836591")
    @DisplayName("Копирование шаблона Jinja2")
    public void createJinja2TemplateTest() {
        String cloneName = NAME + "-clone";
        new ControlPanelIndexPage().goToJinja2TemplatesListPage()
                .copy(jinja2Template);
        jinja2Template.setName(cloneName);
        new Jinja2TemplatePage().checkAttributes(jinja2Template);
        deleteJinjaByName(cloneName);
    }
}
