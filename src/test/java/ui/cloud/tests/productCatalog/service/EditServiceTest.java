package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

public class EditServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("504801")
    @DisplayName("Редактирование сервиса")
    public void editServiceTest() {
        service.setDescription("new description");
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(service.getServiceName())
                .setAttributes(service)
                .saveWithPatchVersion();
        service.setVersion("1.0.1");
        new ServicePage().checkAttributes(service);
    }
}
