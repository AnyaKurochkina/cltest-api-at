package ui.cloud.tests.productCatalog.action;

import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionsListTest extends ActionBaseTest {

    @Test
    @DisplayName("Просмотр списка действий, сортировка")
    @TmsLink("505701")
    public void viewActionsListTest() {
        new IndexPage()
                .goToActionsListPage()
                .checkHeaders()
                .checkSorting();
    }

    @Test
    @DisplayName("Поиск действия")
    @TmsLink("1425598")
    public void searchActionTest() {
        String name = UUID.randomUUID().toString();
        Action action = createActionByApi(name);
        new IndexPage()
                .goToActionsListPage()
                .findActionByValue(name, action)
                .findActionByValue(TITLE, action)
                .findActionByValue(name.substring(1).toUpperCase(), action)
                .findActionByValue(TITLE.substring(1).toLowerCase(), action);
    }

    @Test
    @DisplayName("Фильтрация списка действий")
    @TmsLink("1514146")
    public void filterActions() {
        String name = UUID.randomUUID().toString();
        Action action = createActionByApi(name);
        new IndexPage()
                .goToActionsListPage()
                .getTypeSelect()
                .set(EventType.CLUSTER.getValue());
        ActionsListPage page = new ActionsListPage();
        page.getApplyFiltersButton().click();
        page.checkActionNotFound(action.getName());
        page.getSearchInput().clear();
        page.getClearFiltersButton().click();
        page.getTypeSelect().set(action.getEventTypeProvider().get(0).getEvent_type());
        assertTrue(page.isActionDisplayed(action.getName()));
        page.getProviderSelect().set(action.getEventTypeProvider().get(0).getEvent_provider());
        page.getApplyFiltersButton().click();
        assertTrue(page.isActionDisplayed(action.getName()));
        page.getProviderSelect().set(EventProvider.OPENSTACK.getValue());
        page.getApplyFiltersButton().click();
        assertTrue(page.isActionDisplayed(action.getName()));
    }
}
