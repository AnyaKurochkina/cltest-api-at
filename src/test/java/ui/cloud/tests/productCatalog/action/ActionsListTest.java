package ui.cloud.tests.productCatalog.action;

import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import models.cloud.productCatalog.tag.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;

import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TagSteps.createTag;
import static steps.productCatalog.TagSteps.deleteTagByName;
import static ui.cloud.pages.productCatalog.actions.ActionsListPage.ACTION_NAME_COLUMN;

public class ActionsListTest extends ActionBaseTest {

    @Test
    @DisplayName("Просмотр списка действий, сортировка")
    @TmsLink("505701")
    public void viewActionsListTest() {
        new ControlPanelIndexPage()
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
        new ControlPanelIndexPage()
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
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .getTypeSelect()
                .set(EventType.CLUSTER.getValue());
        ActionsListPage page = new ActionsListPage();
        page.getApplyFiltersButton().click();
        page.checkActionNotFound(action.getName());
        page.getSearchInput().clear();
        page.getClearFiltersButton().click();
        page.getTypeSelect().set(action.getEventTypeProvider().get(0).getEvent_type());
        page.checkActionIsDisplayed(action.getName());
        page.getProviderSelect().set(action.getEventTypeProvider().get(0).getEvent_provider());
        page.getApplyFiltersButton().click();
        page.checkActionIsDisplayed(action.getName());
        page.getProviderSelect().set(EventProvider.OPENSTACK.getValue());
        page.getApplyFiltersButton().click();
        page.checkActionNotFound(action.getName());
    }

    @Test
    @TmsLinks({@TmsLink("SOUL-6066"), @TmsLink("SOUL-6067")})
    @DisplayName("Добавить и удалить тег из списка действий")
    public void addAndDeleteTagFromList() {
        Action action = createActionByApi(randomAlphanumeric(8).toLowerCase());
        String tag1 = "qa_at_" + randomAlphanumeric(6).toLowerCase();
        Tag.builder().name(tag1).build().createObjectPrivateAccess();
        Action action2 = createActionByApi(action.getName() + "_2");
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .search(action.getName())
                .switchToGroupOperations()
                .selectAllRows()
                .editTags()
                .addTag(tag1)
                .closeDialog()
                .checkTags(ACTION_NAME_COLUMN, action.getName(), tag1.substring(0, 7))
                .checkTags(ACTION_NAME_COLUMN, action2.getName(), tag1.substring(0, 7))
                .editTags()
                .removeTag(tag1)
                .closeDialog()
                .checkTags(ACTION_NAME_COLUMN, action.getName(), "")
                .checkTags(ACTION_NAME_COLUMN, action2.getName(), "");
    }
}
