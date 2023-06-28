package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.orders.ProductsPage;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.cloud.pages.services.ServicesListPage;

import java.time.Duration;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static com.codeborne.selenide.Selenide.$x;

@Getter
public class IndexPage {

    private final SelenideElement createOrderButton = $x("//div[@data-testid='order-list-add-button']//button");
    private final SelenideElement instrumentsMenuItem = $x("//div[text()='Инструменты']");
    private final SelenideElement allServicesMenuItem = $x("//div[text()='Все сервисы']");
    private final SelenideElement analyticsMenuItem = $x("//div[text()='Аналитика']");
    private final SelenideElement auditMenuItem = $x("//div[text()='Аудит']");
    private final SelenideElement allResourcesMenuItem = $x("//div[text()='Все ресурсы']");
    private final SelenideElement servicesMenuItem = $x("//div[text()='Сервисы']");
    private final SelenideElement servicesListMenuItem = $x("//div[text()='Список сервисов']");
    private final SelenideElement collapseMenuItem = $x("//div[text()='Свернуть меню']");
    private final SelenideElement expandMenuIcon = $x("//div[contains(@class,'Footer')]//*[name()='svg']");

    public IndexPage() {
        collapseMenuItem.should(Condition.exist);
        if (!Waiting.sleep(collapseMenuItem::isDisplayed, Duration.ofSeconds(3))) {
            expandMenuIcon.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }
        collapseMenuItem.shouldBe(Condition.visible);
    }

    @Step("Переход на страницу заказа продуктов")
    public ProductsPage clickOrderMore() {
        allResourcesMenuItem.click();
        createOrderButton.shouldBe(Condition.visible).shouldBe(Condition.enabled).hover().click();
        return new ProductsPage();
    }

    @Step("Переход на страницу 'Список сервисов'")
    public ServicesListPage goToServicesListPage() {
        instrumentsMenuItem.click();
        servicesMenuItem.hover();
        servicesListMenuItem.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return new ServicesListPage();
    }

    @Step("Переход на страницу Аналитика.Аудит")
    public AuditPage goToPortalAuditPage() {
        instrumentsMenuItem.click();
        analyticsMenuItem.hover();
        auditMenuItem.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Waiting.sleep(500);
        return new AuditPage();
    }
}