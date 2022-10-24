package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

public class EditOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("1073593")
    @DisplayName("Баннер при возврате с формы с несохраненными данными (Отмена)")
    public void checkUnsavedChangesAlertAndCancel() {
        orderTemplate.setDescription("test");
        new IndexPage().goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .setDescription("test")
                .backAndDismissAlert()
                .goToTemplatesListAndDismissAlert()
                .checkAttributes(orderTemplate);
    }
}
