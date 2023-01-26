package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

public class ProductsListTest extends ProductBaseTest {

    @Test
    @TmsLink("507341")
    @DisplayName("Просмотр списка, сортировка")
    public void viewListTest() {
        new IndexPage().goToProductsListPage()
                .checkHeaders()
                .checkSorting();
    }

    @Test
    @TmsLink("1419142")
    @DisplayName("Поиск в списке продуктов")
    public void searchProductTest() {
        new IndexPage().goToProductsListPage()
                .findProductByValue(NAME, product)
                .findProductByValue(TITLE, product)
                .findProductByValue(NAME.substring(1).toUpperCase(), product)
                .findProductByValue(TITLE.substring(1).toLowerCase(), product);
    }
}
