package ui.cloud.tests.productCatalog.action;

import httpModels.productCatalog.action.getAction.response.GetActionResponse;
import httpModels.productCatalog.action.getActionList.response.GetActionsListResponse;
import io.qameta.allure.TmsLink;
import models.productCatalog.action.Action;
import models.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.actions.ActionsListPage;
import ui.cloud.pages.productCatalog.enums.action.ActionType;
import ui.cloud.pages.productCatalog.enums.action.ItemStatus;
import ui.cloud.pages.productCatalog.enums.action.OrderStatus;
import ui.cloud.tests.productCatalog.BaseTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisabledIfEnv("prod")
public class ActionTest extends BaseTest {
    static Graph graph;
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/actions/",
            "productCatalog/actions/createAction.json");

    @BeforeAll
    static void createGraph() {
        graph = Graph.builder()
                .name("graph_for_ui_test")
                .title("graph_for_ui_test")
                .type("action")
                .build()
                .createObject();
    }

    @Test
    @TmsLink("505750")
    @DisplayName("Создание действия")
    public void createAction() {
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
                .reTurnToActionsListPage()
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
        assertTrue(new IndexPage().goToActionsPage()
                .openActionForm(name)
                .inputByLabel("Приоритет", "2")
                .saveAction()
                .setInvalidVersion("1.0.1", version)
                .saveAction()
                .setInvalidVersion("1.0.0", version)
                .saveAction()
                .setInvalidFormatVersion("1/0/2")
                .saveAction()
                .setVersion("1.0.2")
                .checkVersion("1.0.2"));
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
        assertTrue(new IndexPage().goToActionsPage()
                .openActionForm(name)
                .changeGraphVersion("1.0.0")
                .saveAction()
                .saveAsNextVersion()
                .checkVersion("1.0.1"));
    }

    @Test
    @TmsLink("")
    @DisplayName("Удаление иконки")
    public void deleteIcon() {
        String name = "delete_icon_action_test_ui";
        Action.builder()
                .actionName(name)
                .title(name)
                .number(0)
                .build()
                .createObject();
        assertTrue(new IndexPage().goToActionsPage()
                .openActionForm(name)
                .changeGraphVersion("1.0.0")
                .saveAction()
                .saveAsNextVersion()
                .checkVersion("1.0.1"));
    }
}
