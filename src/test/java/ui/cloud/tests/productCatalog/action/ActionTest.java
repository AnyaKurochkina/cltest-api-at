package ui.cloud.tests.productCatalog.action;

import core.enums.Role;
import core.helper.JsonHelper;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.authorizer.GlobalUser;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ActionSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DiffPage;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;

public class ActionTest extends ActionBaseTest {

    @Test
    @TmsLink("505750")
    @DisplayName("Создание действия")
    public void createAction() {
        Graph graph = Graph.builder()
                .name("graph_for_ui_test")
                .title("graph_for_ui_test")
                .type("action")
                .build()
                .createObject();
        String name = "create_action_test_ui";
        assertTrue(new ControlPanelIndexPage().goToActionsListPage()
                .createAction()
                .fillAndSave(name, "create_action_test_ui", "test",
                        ItemStatus.ON, OrderStatus.DAMAGED, ActionType.ON, "configPath", "configKey",
                        "valueOfData", graph.getTitle(), EventType.VM, EventProvider.VSPHERE)
                .isActionDisplayed(name), "Созданное действие не найдено в списке действий.");
        deleteActionByName(name);
    }

    @Test
    @TmsLink("506469")
    @DisplayName("Копирование действия")
    public void copyAction() {
        String name = "copy_action_test_ui";
        String cloneName = name + "-clone";
        Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .build()
                .createObject();
        assertTrue(new ControlPanelIndexPage().goToActionsListPage()
                .copyAction(name)
                .backToActionsList()
                .isActionDisplayed(cloneName));
        deleteActionByName(cloneName);
    }

    @Test
    @TmsLink("506764")
    @DisplayName("Удаление из формы действия")
    public void deleteActionForm() {
        String name = "delete_action_form_test_ui";
        if (isActionExists(name)) {
            deleteActionByName(name);
        }
        JSONObject json = Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        ActionSteps.createAction(json);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .deleteFromActionForm()
                .inputInvalidId("invalid")
                .inputValidIdAndDelete();
        assertFalse(isActionExists(name));
    }

    @Test
    @TmsLink("506779")
    @DisplayName("Удалить действие из выпадающего меню")
    public void deleteActionMenu() {
        String name = "delete_action_form_menu_test_ui";
        if (isActionExists(name)) {
            deleteActionByName(name);
        }
        JSONObject json = Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        ActionSteps.createAction(json);
        new ControlPanelIndexPage().goToActionsListPage()
                .deleteAction(name)
                .inputInvalidId("invalid")
                .inputValidIdAndDelete();
        assertFalse(isActionExists(name));
    }

    @Test
    @TmsLink("529395")
    @DisplayName("Проверка сохранения версии")
    public void checkActionVersions() {
        String name = "check_action_versions_test_ui";
        Action action = Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
        partialUpdateAction(action.getActionId(), new JSONObject().put("priority", 1));
        String version = getActionById(action.getActionId()).getVersion();
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .setPriority(2)
                .checkSaveWithInvalidVersion("1.0.1", version)
                .checkSaveWithInvalidVersion("1.0.0", version)
                .checkSaveWithInvalidVersionFormat("1/0/2")
                .saveWithVersion("1.0.2")
                .checkVersion("1.0.2");
    }

    @Test
    @TmsLink("506731")
    @DisplayName("Редактировать действие")
    public void editAction() {
        String name = "edit_action_test_ui";
        Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .changeGraphVersion("1.0.0")
                .saveWithNextPatchVersion()
                .checkVersion("1.0.1");
    }

    @Test
    @TmsLink("631141")
    @DisplayName("Удаление иконки")
    public void deleteIcon() {
        Icon icon = Icon.builder()
                .name("icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String name = "delete_icon_action_test_ui";
        Action.builder()
                .name(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Collections.singletonList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        assertFalse(new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .deleteIcon()
                .saveWithoutPatchVersion()
                .backToActionsList()
                .openActionForm(name)
                .isIconExist());
    }

    @Test
    @DisplayName("Импорт действия до первого существующего объекта")
    @TmsLink("506795")
    public void importActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        JsonPath json = new JsonPath(data);
        String name = json.get("Action.name");
        String title = json.get("Action.title");
        if (isActionExists(name)) {
            deleteActionByName(name);
        }
        List<Integer> versionArr = json.get("Action.version_arr");
        String version = versionArr.stream().map(Objects::toString).collect(Collectors.joining("."));
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .importAction("src/test/resources/json/productCatalog/actions/importAction.json")
                .openActionForm(name)
                .compareFields(name, title, version);
        deleteActionByName(name);
    }

    @Test
    @TmsLink("807603")
    @DisplayName("Возврат в список со страницы действия")
    public void returnToActionListFromActionPage() {
        new ControlPanelIndexPage()
                .goToActionsListPage()
                .goToNextPageActionList()
                .openActionFormByRowNumber(2)
                .backByBrowserButtonBack()
                .checkActionIsHighlighted(2)
                .openActionFormByRowNumber(3)
                .reTurnToActionsListPageByLink()
                .checkActionIsHighlighted(3);
    }

    @Test
    @TmsLink("1071773")
    @DisplayName("Баннер при закрытии формы с несохраненными данными")
    public void bannerWhenCloseFormAndNotSaveCancel() {
        String name = UUID.randomUUID().toString();
        Action action = createActionByApi(name);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .checkUnsavedChangesAlertAccept(action)
                .checkUnsavedChangesAlertDismiss();
    }

    @Test
    @TmsLink("602488")
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .checkVersion("1.0.0")
                .setPriority(1)
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setPriority(2)
                .saveWithPatchVersion()
                .checkVersion("1.1.0")
                .setPriority(3)
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setPriority(4)
                .saveWithPatchVersion()
                .checkVersion("2.0.0")
                .setPriority(5)
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("602518")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .checkVersion("1.0.0")
                .setPriority(1)
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setPriority(2)
                .checkNextVersionAndSave("1.1.0")
                .checkVersion("1.1.0")
                .setPriority(3)
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setPriority(4)
                .checkNextVersionAndSave("2.0.0")
                .checkVersion("2.0.0")
                .setPriority(5)
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("1205977")
    @DisplayName("Сравнение версий действия")
    public void compareVersionsTest() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .setPriority(1)
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
    @TmsLink("852947")
    @DisplayName("Просмотр JSON действия")
    public void viewJSONTest() {
        String name = UUID.randomUUID().toString();
        Action action = createActionByApi(name);
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .checkJSONcontains(action.getActionId());
    }

    @Test
    @TmsLink("853376")
    @DisplayName("Просмотр аудита по действию")
    public void viewActionAuditTest() {
        String name = UUID.randomUUID().toString();
        createActionByApi(name);
        GlobalUser user = GlobalUser.builder().role(Role.PRODUCT_CATALOG_ADMIN).build().createObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(name)
                .goToAuditTab()
                .checkFirstRecord(LocalDateTime.now().format(formatter), user.getUsername(), "create", "actions", "201", "создан");
    }

    @Test
    @TmsLink("1364724")
    @DisplayName("Проверка фильтрации по типу графа")
    public void checkGraphTypeFilterTest() {
        String actionName = UUID.randomUUID().toString();
        createActionByApi(actionName);
        Graph creatingGraph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .author("AT UI")
                .build()
                .createObject();
        Graph actionGraph = Graph.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Graph")
                .version("1.0.0")
                .type(GraphType.ACTION.getValue())
                .author("AT UI")
                .build()
                .createObject();
        new ControlPanelIndexPage().goToActionsListPage()
                .openActionForm(actionName)
                .setGraph(actionGraph.getName())
                .checkGraphNotFound(creatingGraph.getName());
    }
}
