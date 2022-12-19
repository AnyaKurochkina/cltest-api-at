package ui.t1.tests.productCatalog.image;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;

public class ImageGroupsListTest extends BaseTest {

    @Test
    @TmsLink("1292898")
    @DisplayName("Просмотр списка групп образов")
    public void viewImageGroupsListTest() {
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .checkImageGroupsListHeaders()
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
    }

    @Test
    @TmsLink("1292907")
    @DisplayName("Просмотр информации по группе")
    public void viewImageGroupInfoTest() {
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .showImageGroupInfo(0)
                .hideImageGroupInfo(0);
    }
}
