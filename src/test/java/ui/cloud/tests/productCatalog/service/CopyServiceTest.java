package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

import static steps.productCatalog.ServiceSteps.deleteServiceByName;

@Feature("Копирование сервиса")
public class CopyServiceTest extends ServiceBaseTest {
    @Test
    @TmsLink("504830")
    @DisplayName("Копирование сервиса")
    public void copyServiceTest() {
        String copyName = NAME + "-clone";
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findServiceByValue(NAME, service)
                .copyService(service);
        service.setName(copyName);
        new ServicePage()
                .checkAttributes(service);
        deleteServiceByName(copyName);
    }
}
