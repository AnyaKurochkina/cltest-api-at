package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.AllowedActionSteps.createAllowedAction;

@Feature("Удаление разрешенного действия")
public class DeleteAllowedActionTest extends AllowedActionBaseTest {

    @Override
    @BeforeEach
    public void setUp() {
    }

    @Test
    @TmsLink("1247553")
    @DisplayName("Удаление разрешенного действия из списка")
    public void deleteAllowedActionOnList() {
        String name = createAllowedAction(TITLE).getName();
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .delete(name)
                .checkAllowedActionNotFound(name);
    }

    @Test
    @TmsLink("1599361")
    @DisplayName("Удаление разрешенного действия со страницы")
    public void deleteAllowedActionOnPage() {
        String name = createAllowedAction(TITLE).getName();
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .openAllowedActionPage(name)
                .delete()
                .checkAllowedActionNotFound(name);
    }
}
