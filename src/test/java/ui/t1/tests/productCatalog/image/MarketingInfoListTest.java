package ui.t1.tests.productCatalog.image;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;
import ui.t1.pages.productCatalog.image.MarketingInfoListPage;

@Feature("Образы.Маркетинговая информация")
public class MarketingInfoListTest extends BaseTest {

    @Test
    @TmsLink("1362592")
    @DisplayName("Просмотр списка маркетинговой информации")
    public void viewMarketingInfoListTest() {
        new ControlPanelIndexPage().goToMarketingInfoListPage()
                .checkHeaders()
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
    }

    @Test
    @TmsLink("1292933")
    @DisplayName("Просмотр маркетинговой информации")
    public void viewMarketingInfoTest() {
        String info = "ubuntu";
        new ControlPanelIndexPage().goToMarketingInfoListPage()
                .view(info)
                .checkInfoTitleContains(info);
        Selenide.back();
        new MarketingInfoListPage();
    }
}
