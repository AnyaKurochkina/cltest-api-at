package ui.cloud.pages;

import com.codeborne.selenide.Condition;
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

    final SelenideElement linkServicesList = StringUtils.$x("//a[.='Список сервисов']");
    private final SelenideElement orderMoreBtn = $x("//button[contains(., 'Заказать еще')]");
    private final SelenideElement portalAuditLink = $x("//a[@href='/analytics/audit']");

    @Step("Переход в маркетплейс продуктов")
    public ProductsPage clickOrderMore() {
        orderMoreBtn.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return new ProductsPage();
    }

    @Step("Переход на страницу 'Список сервисов'")
    public ServicesListPage goToServicesListPage() {
        linkServicesList.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        return new ServicesListPage();
    }

    @Step("Переход на страницу Аналитика.Аудит")
    public AuditPage goToPortalAuditPage() {
        portalAuditLink.shouldBe(Condition.visible).click();
        Waiting.sleep(500);
        return new AuditPage();
    }
}