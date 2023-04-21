package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.orders.ProductsPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.services.ServicesListPage;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;

@Getter
public class IndexPage {

    private final SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще')]");
    private final SelenideElement createOrderButton = $x("//div[@data-testid='order-list-add-button']//button");
    private final SelenideElement instrumentsMenuItem = $x("//div[text()='Инструменты']");
    private final SelenideElement allServicesMenuItem = $x("//div[text()='Все сервисы']");
    private final SelenideElement analyticsMenuItem = $x("//div[text()='Аналитика']");
    private final SelenideElement auditMenuItem = $x("//div[text()='Аудит']");
    private final SelenideElement allResourcesMenuItem = $x("//div[text()='Все ресурсы']");
    private final SelenideElement servicesMenuItem = $x("//div[text()='Сервисы']");
    private final SelenideElement servicesListMenuItem = $x("//div[text()='Список сервисов']");
    private final SelenideElement mainLogo = $x("//img");

    public IndexPage() {
        mainLogo.hover();
        allServicesMenuItem.shouldBe(Condition.visible);
    }

    public ProductsPage clickOrderMore() {
        allResourcesMenuItem.click();
        createOrderButton.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover().click();
        return new ProductsPage();
    }

    @Step("Переход на страницу 'Список сервисов'")
    public ServicesListPage goToServicesListPage() {
        servicesMenuItem.hover();
        servicesListMenuItem.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return new ServicesListPage();
    }

    @Step("Переход на страницу Аналитика.Аудит")
    public AuditPage goToPortalAuditPage() {
        instrumentsMenuItem.click();
        analyticsMenuItem.hover();
        auditMenuItem.click();
        Waiting.sleep(500);
        return new AuditPage();
    }
}