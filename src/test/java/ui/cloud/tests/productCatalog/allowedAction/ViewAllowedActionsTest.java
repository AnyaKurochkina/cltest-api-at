package ui.cloud.tests.productCatalog.allowedAction;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.enums.AuditChangeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Просмотр разрешенного действия")
public class ViewAllowedActionsTest extends AllowedActionBaseTest {

    @Test
    @TmsLink("1427827")
    @DisplayName("Просмотр JSON разрешенного действия")
    public void viewJSON() {
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .findAndOpenAllowedActionPage(NAME)
                .checkJSONcontains(allowedAction.getActionId());
    }

    @Test
    @TmsLink("1258600")
    @DisplayName("Просмотр аудита по разрешенному действию")
    public void viewAudit() {
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .findAndOpenAllowedActionPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(user.getEmail(), AuditChangeType.CREATE,
                        "");
    }
}
