package ui.cloud.tests.productCatalog.visualTemplate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.BaseTest;

public class OrderTemplatesListTests extends BaseTest {

    @Test
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkHeadersAndSorting() {
        new IndexPage().goToOrderTemplatesPage()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate();
    }
}
