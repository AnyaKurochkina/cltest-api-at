package ui.cloud.tests.productCatalog.action;

import core.helper.JsonHelper;
import httpModels.productCatalog.action.getAction.response.GetActionResponse;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.action.Action;
import models.productCatalog.graph.Graph;
import models.productCatalog.icon.IconStorage;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.deleteActionByName;
import static steps.productCatalog.ActionSteps.isActionExists;

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
                        "valueOfData", graph.getTitle())
                .isActionExist(name), "Созданное действие не найдено в списке действий.");
        steps.deleteByName(name, GetActionsListResponse.class);
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
        steps.deleteByName(cloneName, GetActionsListResponse.class);
    }

    @Test
    @TmsLink("506764")
    @DisplayName("Удаление из формы действия")
    public void deleteActionForm() {
        String name = "delete_action_form_test_ui";
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetActionsListResponse.class);
        }
        JSONObject json = Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        steps.createProductObject(json);
        new IndexPage().goToActionsPage()
                .openActionForm(name)
                .deleteFromActionForm()
                .inputInvalidId("invalid")
                .inputValidIdAndDelete();
        assertFalse(new ActionsListPage().isActionExist(name));
    }

    @Test
    @TmsLink("506779")
    @DisplayName("Удалить действие из выпадающего меню")
    public void deleteActionMenu() {
        String name = "delete_action_form_menu_test_ui";
        if (steps.isExists(name)) {
            steps.deleteByName(name, GetActionsListResponse.class);
        }
        JSONObject json = Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .init().toJson();
        steps.createProductObject(json);
        new IndexPage().goToActionsPage()
                .deleteAction(name)
                .inputInvalidId("invalid")
                .inputValidIdAndDelete();
        assertFalse(new ActionsListPage().isActionExist(name));
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
                .build()
                .createObject();
        steps.partialUpdateObject(action.getActionId(), new JSONObject().put("priority", 1));
        String version = steps.getById(action.getActionId(), GetActionResponse.class).getVersion();
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
        String name = "delete_icon_action_test_ui";
        Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .icon(IconStorage.ICON_FOR_AT_TEST)
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
