package ui.cloud.tests.productCatalog.forbiddenAction;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.productCatalog.enums.AuditChangeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Просмотр запрещенного действия")
public class ViewForbiddenActionsTest extends ForbiddenActionBaseTest {

    @Test
    @TmsLink("1427830")
    @DisplayName("Просмотр JSON запрещенного действия")
    public void viewJSON() {
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage()
                .findAndOpenForbiddenActionPage(NAME)
                .checkJSONcontains(forbiddenAction.getActionId());
    }

    @Test
    @TmsLink("1258592")
    @DisplayName("Просмотр аудита по запрещенному действию")
    public void viewAudit() {
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .findAndOpenForbiddenActionPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(user.getEmail(), AuditChangeType.CREATE,
                        "");
    }
}
