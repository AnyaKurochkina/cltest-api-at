package ui.cloud.tests.productCatalog.forbiddenAction;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.enums.EventProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.forbiddenAction.ForbiddenActionsListPage;
import ui.elements.Table;

import static core.utils.AssertUtils.assertHeaders;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Список запрещенных действий")
public class ForbiddenActionsListTest extends ForbiddenActionBaseTest {

    private final String nameColumn = "Код запрещенного действия";

    @Test
    @TmsLink("946628")
    @DisplayName("Просмотр списка запрещенных действий, сортировка, пагинация")
    public void viewForbiddenActionsList() {
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage();
        assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Тип", "Провайдер", "Описание", "", "");
        ForbiddenActionsListPage page = new ForbiddenActionsListPage();
        page.checkSorting().checkPagination();
    }

    @Test
    @TmsLink("946629")
    @DisplayName("Поиск в списке запрещенных действий")
    public void searchForbiddenActions() {
        new ControlPanelIndexPage().goToForbiddenActionsListPage()
                .findForbiddenActionByValue(NAME, forbiddenAction)
                .findForbiddenActionByValue(TITLE, forbiddenAction)
                .findForbiddenActionByValue(NAME.substring(1).toUpperCase(), forbiddenAction)
                .findForbiddenActionByValue(TITLE.substring(1).toUpperCase(), forbiddenAction);
    }

    @Test
    @TmsLink("948243")
    @DisplayName("Фильтрация списка запрещенных действий")
    public void filterForbiddenActions() {
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage()
                .getProviderSelect()
                .set(EventProvider.VSPHERE.getValue());
        ForbiddenActionsListPage page = new ForbiddenActionsListPage();
        page.getApplyFiltersButton().click();
        page.checkForbiddenActionNotFound(forbiddenAction.getName());
        page.getSearchInput().clear();
        page.getClearFiltersButton().click();
        page.getTypeSelect().set(forbiddenAction.getEventTypeProvider().get(0).getEvent_type());
        assertTrue(page.isForbiddenActionDisplayed(forbiddenAction.getName()));
        page.getProviderSelect().set(forbiddenAction.getEventTypeProvider().get(0).getEvent_provider());
        page.getApplyFiltersButton().click();
        assertTrue(page.isForbiddenActionDisplayed(forbiddenAction.getName()));
        page.getProviderSelect().set(EventProvider.OPENSTACK.getValue());
        page.getApplyFiltersButton().click();
        assertFalse(page.isForbiddenActionDisplayed(forbiddenAction.getName()));
    }
}
