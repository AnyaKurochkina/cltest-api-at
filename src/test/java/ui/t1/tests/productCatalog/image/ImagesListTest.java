package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;

@Feature("Образы")
public class ImagesListTest extends BaseTest {

    @Test
    @TmsLink("1292207")
    @DisplayName("Просмотр списка образов")
    public void viewImagesListTest() {
        new ControlPanelIndexPage().goToImagesListPage()
                .checkImagesListHeaders()
                .goToNextPage()
                .checkPageNumber(2)
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
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
