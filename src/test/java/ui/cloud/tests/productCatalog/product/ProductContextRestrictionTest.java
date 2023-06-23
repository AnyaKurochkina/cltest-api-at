package ui.cloud.tests.productCatalog.product;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.RestrictionsPage;
import ui.cloud.pages.productCatalog.product.ProductPage;
import ui.elements.Alert;

@Feature("Контекстное ограничение продукта")
public class ProductContextRestrictionTest extends ProductBaseTest {

    @Test
    @TmsLink("1733614")
    @DisplayName("Добавить контекстное ограничение")
    public void addContextRestriction() {
        String org = "org-sandbox", infSystem = "crux", criticalCategory = "Other";
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(NAME);
        new ProductPage().getRestrictionsTab().switchTo();
        RestrictionsPage page = new RestrictionsPage();
        page.getAddContextRestrictionButton().click();
        page.getInfSystemSelect().getElement().$x("select").shouldBe(Condition.disabled);
        page.getOrgSelect().set(org);
        page.getInfSystemSelect().setContains(infSystem);
        page.getCriticalCategorySelect().set(criticalCategory);
        page.getEnvTypeSelect().set("dev", "test");
        page.getEnvSelect().set("DEV", "IFT");
        page.getAddButton().click();
        Alert.green("Контекстное ограничение добавлено. Сохраните объект");
        page.saveWithoutPatchVersion("Продукт успешно изменен");
        page.checkContextRestrionsRecord(org, infSystem, criticalCategory,
                "dev\ntest", "DEV\nIFT");
    }
}
