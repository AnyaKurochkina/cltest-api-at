package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;
import ui.t1.pages.ControlPanelIndexPage;

@Feature("Образы")
public class ImagesListTest extends ProductCatalogUITest {

    @Test
    @TmsLink("1292207")
    @DisplayName("Просмотр списка образов")
    public void viewImagesListTest() {
        new ControlPanelIndexPage().goToImagesListPage()
                .checkImagesListHeaders()
                .checkPagination();
    }

    @Test
    @TmsLink("1292220")
    @DisplayName("Просмотр информации по образу")
    public void viewImageTest() {
        new ControlPanelIndexPage().goToImagesListPage()
                .openImageInfoWithoutMarketingInfo()
                .checkImageInfoContains("OS_VERSION")
                .openImageInfoWithMarketingInfo()
                .checkImageInfoContains("NAME");
    }
}
