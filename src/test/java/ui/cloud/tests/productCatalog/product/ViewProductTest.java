package ui.cloud.tests.productCatalog.product;

import core.enums.Role;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.DiffPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Test
    @TmsLink("853400")
    @DisplayName("Просмотр аудита по продукту")
    public void viewProductAuditTest() {
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new IndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "create", "products", "201", "создан");
    }
}
