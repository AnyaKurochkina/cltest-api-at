package ui.cloud.tests.productCatalog.allowedAction;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.AllowedActionSteps.deleteAllowedActionByName;
import static steps.productCatalog.AllowedActionSteps.isAllowedActionExists;
import static steps.productCatalog.GraphSteps.deleteGraphById;

@Feature("Импорт из файла")
public class ImportAllowedActionTest extends AllowedActionBaseTest {

    @Override
    @BeforeEach
    public void setUp() {
    }

    @Test
    @TmsLink("1247563")
    @DisplayName("Импорт разрешенного действия до первого существующего объекта")
    public void importAllowedAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/allowedAction/importAllowedAction.json");
        JsonPath json = new JsonPath(data);
        String name = json.getString("AllowedAction.name");
        if (isAllowedActionExists(name)) deleteAllowedActionByName(name);
        String actionName = json.getString("rel_foreign_models.action.Action.name");
        if (isActionExists(actionName)) deleteActionByName(actionName);
        new ControlPanelIndexPage()
                .goToAllowedActionsListPage()
                .importAllowedAction("src/test/resources/json/productCatalog/allowedAction/importAllowedAction.json")
                .findAndOpenAllowedActionPage(name)
                .checkAttributes(AllowedAction.builder()
                        .name(name)
                        .title(json.getString("AllowedAction.title"))
                        .description(json.getString("AllowedAction.description"))
                        .actionId(getActionByName(actionName).getActionId())
                        .eventTypeProvider(json.getList("AllowedAction.event_type_provider", EventTypeProvider.class))
                        .build());
        Action action = getActionByName(actionName);
        deleteAllowedActionByName(name);
        deleteActionByName(actionName);
        deleteGraphById(action.getGraphId());
    }
}
