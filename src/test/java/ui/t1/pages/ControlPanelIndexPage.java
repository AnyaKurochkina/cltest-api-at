package ui.t1.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.t1.pages.productCatalog.image.ImageGroupsListPage;
import ui.t1.pages.productCatalog.image.ImagesListPage;
import ui.t1.pages.productCatalog.image.MarketingInfoListPage;

import static com.codeborne.selenide.Selenide.$x;

public class ControlPanelIndexPage {

    private final SelenideElement imagesLink = $x("//*[@href='/meccano/images']");
    private final SelenideElement imageGroupsLink = $x("//*[@href='/meccano/image_groups']");
    private final SelenideElement marketingInfoLink = $x("//*[@href='/meccano/marketing']");

    @Step("Переход на страницу Конструктор.Образы")
    public ImagesListPage goToImagesListPage() {
        imagesLink.click();
        return new ImagesListPage();
    }

    @Step("Переход на страницу Конструктор.Группы образов")
    public ImageGroupsListPage goToImageGroupsListPage() {
        imagesLink.click();
        imageGroupsLink.click();
        return new ImageGroupsListPage();
    }

    @Step("Переход на страницу Конструктор.Маркетинговая информация")
    public MarketingInfoListPage goToMarketingInfoListPage() {
        imagesLink.click();
        marketingInfoLink.click();
        return new MarketingInfoListPage();
    }
}
