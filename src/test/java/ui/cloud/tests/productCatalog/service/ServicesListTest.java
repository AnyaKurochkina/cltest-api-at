package ui.cloud.tests.productCatalog.service;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.service.ServicesListPagePC;

@Feature("Действия со списком сервисов")
public class ServicesListTest extends ServiceBaseTest {

    @Test
    @TmsLink("504604")
    @DisplayName("Проверка заголовков списка, сортировка")
    public void checkHeadersAndSorting() {
        new ControlPanelIndexPage().goToServicesListPagePC()
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
        new ControlPanelIndexPage().goToServicesListPagePC()
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
        new ControlPanelIndexPage().goToServicesListPagePC()
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
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .goToServicesList()
                .checkServiceIsHighlighted(NAME);
        new ServicesListPagePC().openServicePage(NAME);
        Selenide.back();
        new ServicesListPagePC().checkServiceIsHighlighted(NAME);
        new ServicesListPagePC().openServicePage(NAME)
                .backToServicesList()
                .checkServiceIsHighlighted(NAME);
    }
}
