package ui.cloud.tests.productCatalog.action;

import core.helper.JsonHelper;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ActionSteps;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.IndexPage;
import models.cloud.productCatalog.enums.EventProvider;
import models.cloud.productCatalog.enums.EventType;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;

@DisabledIfEnv("prod")
public class ActionTest extends BaseTest {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/actions/",
            "productCatalog/actions/createAction.json");

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
        assertTrue(new IndexPage().goToActionsPage()
                .createAction()
                .fillAndSave(name, "create_action_test_ui", "test",
                        ItemStatus.ON, OrderStatus.DAMAGED, ActionType.ON, "configPath", "configKey",
                        "valueOfData", graph.getTitle(), EventType.VM, EventProvider.VSPHERE)
                .isActionExist(name), "Созданное действие не найдено в списке действий.");
        deleteActionByName(name);
    }

    @Test
    @TmsLink("506469")
    @DisplayName("Копирование действия")
    public void copyAction() {
        String name = "copy_action_test_ui";
        String cloneName = name + "-clone";
        Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .createObject();
        assertTrue(new IndexPage().goToActionsPage()
                .copyAction(name)
                .reTurnToActionsListPageByCancelButton()
                .isActionExist(cloneName));
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
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        ActionSteps.createAction(json);
        new IndexPage().goToActionsPage()
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
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        ActionSteps.createAction(json);
        new IndexPage().goToActionsPage()
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
                .actionName(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Arrays.asList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
        partialUpdateAction(action.getActionId(), new JSONObject().put("priority", 1));
        String version = getActionById(action.getActionId()).getVersion();
        new IndexPage().goToActionsPage()
                .openActionForm(name)
                .inputByLabel("Приоритет", "2")
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
                .actionName(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Arrays.asList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .build()
                .createObject();
        new IndexPage().goToActionsPage()
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
                .actionName(name)
                .title(name)
                .number(0)
                .eventTypeProvider(Arrays.asList(EventTypeProvider.builder()
                        .event_type(EventType.VM.getValue())
                        .event_provider(EventProvider.VSPHERE.getValue())
                        .build()))
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        assertFalse(new IndexPage().goToActionsPage()
                .openActionForm(name)
                .deleteIcon()
                .saveWithNextPatchVersion()
                .reTurnToActionsListPageByCancelButton()
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
        new IndexPage()
                .goToActionsPage()
                .importAction("src/test/resources/json/productCatalog/actions/importAction.json")
                .openActionForm(name)
                .compareFields(name, title, version);
        deleteActionByName(name);
    }

    @Test
    @TmsLink("807603")
    @DisplayName("Возврат в список со страницы действия")
    public void returnToActionListFromActionPage() {
        new IndexPage()
                .goToActionsPage()
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
    @DisplayName("Баннер при закрытии формы с несохраненными данными, Отмена")
    public void bannerWhenCloseFormAndNotSaveCancel() {
        String name = "action_for_banner_test_ui";
        Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .createObject();
        new IndexPage()
                .goToActionsPage()
                .openActionForm(name)
                .inputByLabel("Приоритет сообщения", "1")
                .backOnBrowserAndAlertCancel()
                .backByActionsLinkAndAlertCancel()
                .closeTabAndAlertCancel();
    }
}
