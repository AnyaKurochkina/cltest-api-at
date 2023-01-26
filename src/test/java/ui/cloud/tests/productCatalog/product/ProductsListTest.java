package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Categories;
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

    @Test
    @TmsLink("769598")
    @DisplayName("Фильтрация списка продуктов")
    public void filterProductsTest() {
        new IndexPage().goToProductsListPage()
                .setCategoryFilter(Categories.CONTAINER.getValue())
                .applyFilters()
                .checkProductIsNotDisplayed(product)
                .setCategoryFilter(Categories.VM.getValue())
                .applyFilters()
                .checkProductIsDisplayed(product)
                .setStatusFilter("Открыт")
                .applyFilters()
                .checkProductIsNotDisplayed(product)
                .setStatusFilter("Закрыт")
                .applyFilters()
                .checkProductIsDisplayed(product)
                .removeFilterTag(Categories.VM.getValue())
                .checkProductIsNotDisplayed(product)
                .clearFilters()
                .checkProductIsDisplayed(product);
    }
}
