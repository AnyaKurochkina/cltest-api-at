package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Удаление разрешенного действия")
public class DeleteAllowedActionTest extends AllowedActionBaseTest {

    @Test
    @TmsLink("1247553")
    @DisplayName("Удаление разрешенного действия из списка")
    public void deleteAllowedActionOnList() {
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .delete(NAME)
                .checkAllowedActionNotFound(NAME);
    }

    @Test
    @TmsLink("1599361")
    @DisplayName("Удаление разрешенного действия со страницы")
    public void deleteAllowedActionOnPage() {
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .openAllowedActionPage(NAME)
                .delete()
                .checkAllowedActionNotFound(NAME);
    }
}
