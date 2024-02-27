package ui.cloud.tests.productCatalog.product;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.product.ProductsListPage;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;
import static ui.cloud.pages.productCatalog.product.ProductsListPage.PRODUCT_NAME_COLUMN;

public class ProductsListTest extends ProductBaseTest {

    @Test
    @TmsLink("507341")
    @DisplayName("Просмотр списка, сортировка")
    public void viewListTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .checkHeaders()
                .checkSorting();
    }

    @Test
    @TmsLink("1419142")
    @DisplayName("Поиск в списке продуктов")
    public void searchProductTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findProductByValue(NAME, product)
                .findProductByValue(TITLE, product)
                .findProductByValue(NAME.substring(1).toUpperCase(), product)
                .findProductByValue(TITLE.substring(1).toLowerCase(), product);
    }

    @Test
    @TmsLink("769598")
    @DisplayName("Фильтрация списка продуктов")
    public void filterProductsTest() {
        new ControlPanelIndexPage().goToProductsListPage()
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

    @Test
    @TmsLink("807411")
    @DisplayName("Возврат в список со страницы продукта")
    public void returnToListFromProductPageTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .goToProductsList()
                .checkProductIsHighlighted(NAME);
        new ProductsListPage().openProductPage(NAME);
        Selenide.back();
        new ProductsListPage().checkProductIsHighlighted(NAME);
        new ProductsListPage().openProductPage(NAME)
                .backToProductsList()
                .checkProductIsHighlighted(NAME);
    }

    @Test
    @TmsLinks({@TmsLink("SOUL-5045"), @TmsLink("SOUL-5046")})
    @DisplayName("Добавить и удалить тег из списка продуктов")
    public void addAndDeleteTagFromList() {
        String tag1 = "qa_at_" + randomAlphanumeric(6).toLowerCase();
        createTag(tag1);
        Product product2 = ProductSteps.createProduct(product.getName() + "_2", TITLE);
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .search(product.getName())
                .switchToGroupOperations()
                .selectAllRows()
                .editTags()
                .addTag(tag1)
                .closeDialog()
                .checkTags(PRODUCT_NAME_COLUMN, product.getName(), tag1.substring(0, 7))
                .checkTags(PRODUCT_NAME_COLUMN, product2.getName(), tag1.substring(0, 7))
                .editTags()
                .removeTag(tag1)
                .closeDialog()
                .checkTags(PRODUCT_NAME_COLUMN, product.getName(), "")
                .checkTags(PRODUCT_NAME_COLUMN, product2.getName(), "");
        deleteTagByName(tag1);
    }
}
