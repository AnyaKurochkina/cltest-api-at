package ui.cloud.tests.productCatalog.allowedAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.enums.EventProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.allowedAction.AllowedActionsListPage;
import ui.elements.Table;

import static core.utils.AssertUtils.assertHeaders;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Список разрешенных действий")
public class AllowedActionsListTest extends AllowedActionBaseTest {

    private final String nameColumn = "Код разрешенного действия";

    @Test
    @TmsLink("1247444")
    @DisplayName("Просмотр списка разрешенных действий, сортировка, пагинация")
    public void viewAllowedActionsList() {
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage();
        assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Дата изменения", "Тип", "Провайдер", "Описание", "", "");
        AllowedActionsListPage page = new AllowedActionsListPage();
        page.checkSorting().checkPagination();
    }

    @Test
    @TmsLink("1247483")
    @DisplayName("Поиск в списке разрешенных действий")
    public void searchAllowedActions() {
        new ControlPanelIndexPage().goToAllowedActionsListPage()
                .findAllowedActionByValue(NAME, allowedAction)
                .findAllowedActionByValue(TITLE, allowedAction)
                .findAllowedActionByValue(NAME.substring(1).toUpperCase(), allowedAction)
                .findAllowedActionByValue(TITLE.substring(1).toUpperCase(), allowedAction);
    }

    @Test
    @TmsLink("1247484")
    @DisplayName("Фильтрация списка разрешенных действий")
    public void filterAllowedActions() {
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .getProviderSelect()
                .set(EventProvider.VSPHERE.getValue());
        AllowedActionsListPage page = new AllowedActionsListPage();
        page.getApplyFiltersButton().click();
        page.checkAllowedActionNotFound(allowedAction.getName());
        page.getSearchInput().clear();
        page.getClearFiltersButton().click();
        page.getTypeSelect().set(allowedAction.getEventTypeProvider().get(0).getEvent_type());
        assertTrue(page.isAllowedActionDisplayed(allowedAction.getName()));
        page.getProviderSelect().set(allowedAction.getEventTypeProvider().get(0).getEvent_provider());
        page.getApplyFiltersButton().click();
        assertTrue(page.isAllowedActionDisplayed(allowedAction.getName()));
        page.getProviderSelect().set(EventProvider.OPENSTACK.getValue());
        page.getApplyFiltersButton().click();
        assertFalse(page.isAllowedActionDisplayed(allowedAction.getName()));
    }
}
