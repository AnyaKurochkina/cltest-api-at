package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;

@Feature("Удаление сервиса")
public class DeleteServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("504829")
    @DisplayName("Удаление сервиса из списка")
    public void deleteTemplateFromList() {
        new IndexPage().goToServicesListPagePC()
                .deleteService(NAME)
                .checkServiceNotFound(NAME);
    }

    @Test
    @TmsLink("508545")
    @DisplayName("Удаление со страницы сервиса")
    public void deleteTemplateFromPage() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .deleteService();
        new ServicesListPagePC()
                .checkServiceNotFound(NAME);
    }

    @Test
    @TmsLink("766478")
    @DisplayName("Недоступность удаления опубликованного сервиса")
    public void deletePublishedServiceTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkDeleteOpenProduct();
    }
}
