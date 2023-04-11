package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

public class DeleteForbiddenActionTest extends ForbiddenActionBaseTest {

    @Test
    @TmsLink("947068")
    @DisplayName("Удаление запрещенного действия из списка")
    public void deleteForbiddenActionOnList() {
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage()
                .delete(NAME)
                .checkForbiddenActionNotFound(NAME);
    }

    @Test
    @TmsLink("1581875")
    @DisplayName("Удаление запрещенного действия со страницы")
    public void deleteForbiddenActionOnPage() {
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage()
                .openForbiddenActionPage(NAME)
                .delete()
                .checkForbiddenActionNotFound(NAME);
    }
}
