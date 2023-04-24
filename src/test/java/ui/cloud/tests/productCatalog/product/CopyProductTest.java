package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.product.ProductPage;

@Feature("Копирование продукта")
public class CopyProductTest extends ProductBaseTest {
    @Test
    @TmsLink("507363")
    @DisplayName("Копирование продукта")
    public void copyProductTest() {
        String copyName = NAME + "-clone";
        new ControlPanelIndexPage().goToProductsListPage()
                .findProductByValue(NAME, product)
                .copy(product);
        product.setName(copyName);
        new ProductPage()
                .checkAttributes(product);
        deleteProductByApi(copyName);
    }
}
