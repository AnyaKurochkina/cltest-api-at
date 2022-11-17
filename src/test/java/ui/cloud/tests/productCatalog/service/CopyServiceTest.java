package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

public class CopyServiceTest extends ServiceBaseTest {
    @Test
    @TmsLink("504830")
    @DisplayName("Копирование сервиса")
    public void copyServiceTest() {
        String copyName = NAME + "-clone";
        new IndexPage().goToServicesListPagePC()
                .findServiceByValue(NAME, service)
                .copyService(service);
        service.setName(copyName);
        new ServicePage()
                .checkAttributes(service);
        deleteService(copyName);
    }
}
