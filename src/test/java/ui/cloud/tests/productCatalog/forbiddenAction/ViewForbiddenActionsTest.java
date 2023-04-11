package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

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
}
