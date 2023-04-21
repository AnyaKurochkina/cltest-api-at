package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

@Feature("Действия со списком шаблонов отображения")
public class OrderTemplatesListTests extends OrderTemplateBaseTest {

    @Test
    @TmsLink("646721")
    @DisplayName("Проверка заголовков списка, сортировка, пагинация")
    public void checkHeadersAndSorting() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate()
                .checkSortingByState()
                .checkPagination();
    }

    @Test
    @TmsLink("1206221")
    @DisplayName("Поиск в списке шаблонов")
    public void searchOrderTemplateTest() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .findTemplateByValue(NAME, orderTemplate)
                .findTemplateByValue(TITLE, orderTemplate)
                .findTemplateByValue(NAME.substring(1).toUpperCase(), orderTemplate)
                .findTemplateByValue(TITLE.substring(1).toUpperCase(), orderTemplate);
    }

    @Test
    @TmsLink("770483")
    @DisplayName("Фильтрация списка шаблонов")
    public void filterOrderTemplatesTest() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .setTypeFilter("vm")
                .setProviderFilter("vsphere")
                .setStateFilter("Выключено")
                .applyFilters()
                .checkTemplateIsDisplayed(orderTemplate)
                .removeFilterTag("vsphere")
                .checkTemplateIsDisplayed(orderTemplate)
                .setStateFilter("Включено")
                .applyFilters()
                .checkTemplateIsNotDisplayed(orderTemplate)
                .clearFilters()
                .checkTemplateIsDisplayed(orderTemplate);
    }
}
