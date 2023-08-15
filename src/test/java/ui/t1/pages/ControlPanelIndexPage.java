package ui.t1.pages;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Button;
import ui.t1.pages.IAM.organization.OrganizationsPage;
import ui.t1.pages.productCatalog.image.ImageGroupsListPage;
import ui.t1.pages.productCatalog.image.ImagesListPage;
import ui.t1.pages.productCatalog.image.LogoListPage;
import ui.t1.pages.productCatalog.image.MarketingInfoListPage;

import static com.codeborne.selenide.Selenide.$x;

public class ControlPanelIndexPage {

    private final SelenideElement imagesLink = $x("//*[@href='/meccano/images']");
    private final SelenideElement imageGroupsLink = $x("//*[@href='/meccano/image_groups']");
    private final SelenideElement marketingInfoLink = $x("//*[@href='/meccano/marketing']");
    private final SelenideElement logosLink = $x("//*[@href='/meccano/logotypes']");
    private final SelenideElement organizationLink = $x("//a[@href='/iam/organizations']");
    private final SelenideElement iamMenuItem = $x("//div[text()='IAM и Управление']");
    private final Button mainMenuButton = Button.byXpath("//div[contains(@class, 'MainMenu')]//button");

    @Step("Переход на страницу Образы")
    public ImagesListPage goToImagesListPage() {
        imagesLink.click();
        return new ImagesListPage();
    }

    @Step("Переход на страницу Образы.Группы образов")
    public ImageGroupsListPage goToImageGroupsListPage() {
        imagesLink.click();
        imageGroupsLink.click();
        return new ImageGroupsListPage();
    }

    @Step("Переход на страницу Образы.Маркетинговая информация")
    public MarketingInfoListPage goToMarketingInfoListPage() {
        imagesLink.scrollIntoView(true).click();
        marketingInfoLink.click();
        return new MarketingInfoListPage();
    }

    @Step("Переход на страницу Образы.Логотипы")
    public LogoListPage goToLogoListPage() {
        imagesLink.scrollIntoView(true).click();
        logosLink.click();
        return new LogoListPage();
    }

    @Step("Переход на страницу IAM и Управление. Организации")
    public OrganizationsPage goToOrganizationPage() {
        mainMenuButton.click();
        iamMenuItem.hover();
        organizationLink.click();
        Waiting.sleep(500);
        return new OrganizationsPage();
    }
}
