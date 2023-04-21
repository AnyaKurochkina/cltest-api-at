package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.orderTemplate.OrderTemplatesListPage;

@Feature("Удаление шаблона отображения")
public class DeleteOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("679073")
    @DisplayName("Удаление шаблона из списка")
    public void deleteTemplateFromList() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .deleteTemplate(NAME)
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("687065")
    @DisplayName("Удаление со страницы шаблона")
    public void deleteTemplateFromPage() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .deleteTemplate();
        new OrderTemplatesListPage()
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("766513")
    @DisplayName("Недоступность удаления включенного шаблона")
    public void deleteEnabledTemplateTest() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .checkDeleteEnabledTemplate();
    }
}
