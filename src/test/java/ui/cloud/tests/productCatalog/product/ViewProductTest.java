package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.DiffPage;

public class ViewProductTest extends ProductBaseTest {

    @Test
    @TmsLink("852949")
    @DisplayName("Просмотр JSON продукта")
    public void viewJSONTest() {
        new IndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkJSONcontains(product.getProductId());
    }

    @Test
    @TmsLink("1205980")
    @DisplayName("Сравнение версий продукта")
    public void compareVersionsTest() {
        new IndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .setAuthor("QA")
                .saveWithPatchVersion()
                .goToVersionComparisonTab();
        new DiffPage()
                .checkCurrentVersionInDiff("1.0.1")
                .compareWithVersion("1.0.0")
                .selectVersion("1.0.0")
                .checkCurrentVersionInDiff("1.0.0")
                .compareWithVersion("1.0.0")
                .compareWithVersion("1.0.1");
    }
}
