package ui.cloud.tests.productCatalog.orderTemplate;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.enums.AuditChangeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Просмотр страницы шаблона отображения")
public class ViewOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("SOUL-9037")
    @DisplayName("Просмотр аудита шаблона отображения")
    public void viewAuditTest() {
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        new ControlPanelIndexPage()
                .goToOrderTemplatesPage()
                .findAndOpenTemplatePage(NAME)
                .goToAuditTab()
                .checkFirstRecord(user.getEmail(), AuditChangeType.CREATE, "");
    }
}
