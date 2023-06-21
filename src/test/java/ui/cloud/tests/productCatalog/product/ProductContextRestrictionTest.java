package ui.cloud.tests.productCatalog.product;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.RestrictionsPage;
import ui.cloud.pages.productCatalog.product.ProductPage;

@Feature("Контекстное ограничение продукта")
public class ProductContextRestrictionTest extends ProductBaseTest {

    @Test
    @TmsLink("")
    @DisplayName("Добавление контекстного ограничения")
    public void addContextRestriction() {
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(NAME);
        new ProductPage().getRestrictionsTab().switchTo();
        RestrictionsPage page = new RestrictionsPage();
        page.getAddContextRestrictionButton().click();
        page.getInfSystemSelect().getElement().$x("select").shouldBe(Condition.disabled);
        page.getOrgSelect().set("org-sandbox");
        page.getInfSystemSelect().setContains("crux");
        page.getCriticalitySelect().set("Other");
        page.getEnvTypeSelect().set("dev", "test");
    }
}
