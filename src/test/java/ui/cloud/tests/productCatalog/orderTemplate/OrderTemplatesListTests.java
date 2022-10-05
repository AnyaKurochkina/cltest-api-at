package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

public class OrderTemplatesListTests extends OrderTemplateBaseTest {

    @Test
    @TmsLink("646721")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkHeadersAndSorting() {
        new IndexPage().goToOrderTemplatesPage()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate()
                .checkSortingByState();
    }

    @Test
    @TmsLink("1206221")
    @DisplayName("Поиск в списке шаблонов")
    public void searchOrderTemplateTest() {
        new IndexPage().goToOrderTemplatesPage()
                .findTemplateByValue(NAME, orderTemplate)
                .findTemplateByValue(TITLE, orderTemplate)
                .findTemplateByValue(NAME.substring(1).toUpperCase(), orderTemplate)
                .findTemplateByValue(TITLE.substring(1).toUpperCase(), orderTemplate);
    }
}
