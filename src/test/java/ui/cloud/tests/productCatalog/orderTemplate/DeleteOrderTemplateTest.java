package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;

@Feature("Удаление шаблона отображения")
public class DeleteOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("679073")
    @DisplayName("Удаление шаблона из списка")
    public void deleteTemplateFromList() {
        new IndexPage().goToOrderTemplatesPage()
                .deleteTemplate(NAME)
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("687065")
    @DisplayName("Удаление со страницы шаблона")
    public void deleteTemplateFromPage() {
        new IndexPage().goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .deleteTemplate();
        new TemplatesListPage()
                .checkTemplateNotFound(NAME);
    }
}
