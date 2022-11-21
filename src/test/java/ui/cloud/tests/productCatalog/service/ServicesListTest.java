package ui.cloud.tests.productCatalog.service;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;

@Feature("Действия со списком сервисов")
public class ServicesListTest extends ServiceBaseTest {

    @Test
    @TmsLink("504604")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkHeadersAndSorting() {
        new IndexPage().goToServicesListPagePC()
                .checkHeaders()
                .checkSortingByTitle()
                .checkSortingByName()
                .checkSortingByCreateDate()
                .checkSortingByStatus();
    }

    @Test
    @TmsLink("1240134")
    @DisplayName("Поиск в списке сервисов")
    public void searchServiceTest() {
        new IndexPage().goToServicesListPagePC()
                .findServiceByValue(NAME, service)
                .findServiceByValue(TITLE, service)
                .findServiceByValue(NAME.substring(1).toUpperCase(), service)
                .findServiceByValue(TITLE.substring(1).toUpperCase(), service)
                .findServiceByValue(DESCRIPTION.substring(1).toUpperCase(), service);
    }

    @Test
    @TmsLink("770468")
    @DisplayName("Фильтрация списка сервисов")
    public void filterOrderTemplatesTest() {
        new IndexPage().goToServicesListPagePC()
                .setStatusFilter("Опубликован")
                .applyFilters()
                .checkServiceIsNotDisplayed(service)
                .removeFilterTag("опубликован")
                .checkServiceIsDisplayed(service)
                .setStatusFilter("Скрыт")
                .applyFilters()
                .checkServiceIsDisplayed(service)
                .clearFilters()
                .checkServiceIsDisplayed(service);
    }

    @Test
    @TmsLink("808412")
    @DisplayName("Проверка подсветки ранее открытого сервиса")
    public void returnToListFromServicePageTest() {
        new IndexPage().goToServicesListPagePC()
                .sortByCreateDate()
                .lastPage()
                .openServicePage(NAME)
                .goToServicesList()
                .checkServiceIsHighlighted(NAME);
        new ServicesListPagePC().openServicePage(NAME);
        Selenide.back();
        new ServicesListPagePC().checkServiceIsHighlighted(NAME);
        new ServicesListPagePC().openServicePage(NAME)
                .cancel()
                .checkServiceIsHighlighted(NAME);
    }
}
