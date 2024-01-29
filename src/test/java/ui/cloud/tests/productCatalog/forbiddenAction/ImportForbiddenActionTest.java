package ui.cloud.tests.productCatalog.forbiddenAction;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.feedService.action.EventTypeProvider;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.ForbiddenActionSteps.deleteForbiddenActionByName;
import static steps.productCatalog.ForbiddenActionSteps.isForbiddenActionExists;
import static steps.productCatalog.GraphSteps.deleteGraphById;

@Feature("Импорт из файла")
public class ImportForbiddenActionTest extends ForbiddenActionBaseTest {

    @Override
    @BeforeEach
    public void setUp() {
    }

    @Test
    @TmsLink("947207")
    @DisplayName("Импорт запрещенного действия до первого существующего объекта")
    public void importForbiddenAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/forbiddenAction/importForbiddenAction.json");
        JsonPath json = new JsonPath(data);
        String name = json.getString("ForbiddenAction.name");
        if (isForbiddenActionExists(name)) deleteForbiddenActionByName(name);
        String actionName = json.getString("rel_foreign_models.action.Action.name");
        if (isActionExists(actionName)) deleteActionByName(actionName);
        new ControlPanelIndexPage()
                .goToForbiddenActionsListPage()
                .importForbiddenAction("src/test/resources/json/productCatalog/forbiddenAction/importForbiddenAction.json")
                .findAndOpenForbiddenActionPage(name)
                .checkAttributes(ForbiddenAction.builder()
                        .name(name)
                        .title(json.getString("ForbiddenAction.title"))
                        .description(json.getString("ForbiddenAction.description"))
                        .actionId(getActionByName(actionName).getId())
                        .eventTypeProvider(json.getList("ForbiddenAction.event_type_provider", EventTypeProvider.class))
                        .direction(json.getString("ForbiddenAction.direction"))
                        .build());
        Action action = getActionByName(actionName);
        deleteForbiddenActionByName(name);
        deleteActionByName(actionName);
        deleteGraphById(action.getGraphId());
    }
}
