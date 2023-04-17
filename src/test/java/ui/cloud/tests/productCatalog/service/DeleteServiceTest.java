package ui.cloud.tests.productCatalog.service;

import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.LoginPageControlPanel;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;

@Feature("Удаление сервиса")
public class DeleteServiceTest extends ServiceBaseTest {

    @Override
    @BeforeEach
    public void init() {
        new LoginPageControlPanel().signIn(Role.SUPERADMIN);
    }

    @Test
    @TmsLink("504829")
    @DisplayName("Удаление сервиса из списка")
    public void deleteServiceFromList() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .deleteService(NAME)
                .checkServiceNotFound(NAME);
    }

    @Test
    @TmsLink("508545")
    @DisplayName("Удаление со страницы сервиса")
    public void deleteServiceFromPage() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .deleteService();
        new ServicesListPagePC()
                .checkServiceNotFound(NAME);
    }

    @Test
    @TmsLink("766478")
    @DisplayName("Недоступность удаления опубликованного сервиса")
    public void deletePublishedServiceTest() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkDeletePublishedService();
    }
}
