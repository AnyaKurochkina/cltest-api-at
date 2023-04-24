package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Удаление продукта")
public class DeleteProductTest extends ProductBaseTest {

    @Test
    @TmsLink("507458")
    @DisplayName("Удаление продукта из списка")
    public void deleteProductFromListTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .delete(NAME);
    }

    @Test
    @TmsLink("508483")
    @DisplayName("Удаление продукта со страницы")
    public void deleteProductFromPageTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .delete();
    }

    @Test
    @TmsLink("766473")
    @DisplayName("Недоступность удаления открытого продукта")
    public void deleteOpenProductTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkDeleteOpenProduct();
    }
}
