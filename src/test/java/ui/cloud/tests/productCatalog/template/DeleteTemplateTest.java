package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;

@Feature("Удаление шаблона")
public class DeleteTemplateTest extends TemplateBaseTest {

    @AfterEach
    public void tearDown() {
    }

    @Test
    @TmsLink("504788")
    @DisplayName("Удаление шаблона из списка")
    public void deleteTemplateFromList() {
        new IndexPage().goToTemplatesPage()
                .deleteTemplate(NAME)
                .checkTemplateNotFound(NAME);
    }

    @Test
    @TmsLink("504726")
    @DisplayName("Удаление со страницы шаблона")
    public void deleteTemplateFromPage() {
        new IndexPage().goToTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .openDeleteDialog()
                .inputInvalidId("test")
                .inputValidIdAndDelete();
        new TemplatesListPage()
                .checkTemplateNotFound(NAME);
    }
}
