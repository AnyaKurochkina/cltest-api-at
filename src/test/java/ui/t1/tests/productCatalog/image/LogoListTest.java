package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;

@Feature("Образы.Логотипы")
public class LogoListTest extends BaseTest {

    @Test
    @TmsLink("1364689")
    @DisplayName("Просмотр списка логотипов")
    public void viewLogoListTest() {
        new ControlPanelIndexPage().goToLogoListPage()
                .checkHeaders()
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
    }
}
