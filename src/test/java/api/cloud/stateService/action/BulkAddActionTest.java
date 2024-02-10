package api.cloud.stateService.action;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.ActionStateService;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static core.helper.DateValidator.currentTimeInFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.StateServiceSteps.createBulkAddAction;
import static steps.stateService.StateServiceSteps.getActionListByFilter;

@Tag("state_service")
@Epic("State Service")
@Feature("Actions")
@DisabledIfEnv("prod")
public class BulkAddActionTest extends Tests {

    @DisplayName("Создание Bulk-add-action")
    @TmsLink("1596569")
    @Test
    public void createBulkAddActionTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String uuid = UUID.randomUUID().toString();
        Action action = createAction("bulk_add_action_api_test");
        Graph graph = createGraph("bulk_add_action_graph_api_test");
        JSONObject json = JsonHelper.getJsonTemplate("stateService/createAction.json")
                .set("$.order_ids", Collections.singletonList(uuid))
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getId())
                .set("$.create_dt", currentTimeInFormat())
                .build();
        ActionStateService expectedActionService = createBulkAddAction(project.getId(), json)
                .assertStatus(201)
                .jsonPath()
                .getObject("[0]", ActionStateService.class);
        ActionStateService actualActionService = getActionListByFilter("action_id", action.getId()).get(0);
        assertEquals(expectedActionService, actualActionService);
    }

    @DisplayName("Негативный тест на создание Bulk-add-action")
    @TmsLink("1596917")
    @Test
    public void negativeCreateBulkAddActionTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String uuid = UUID.randomUUID().toString();
        Action action = createAction("bulk_add_action_api_test");
        Graph graph = createGraph("bulk_add_action_graph_api_test");
        String subtype = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        JSONObject json = JsonHelper.getJsonTemplate("stateService/createAction.json")
                .set("$.order_ids", Collections.singletonList(uuid))
                .set("$.subtype", subtype)
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getId())
                .build();
        String error = createBulkAddAction(project.getId(), json)
                .assertStatus(400)
                .jsonPath().getList("", String.class).get(0);
        String expectedErrorMsg = String.format("'%s' is not one of ['folder', 'tariff_plan']\n\nFailed validating 'enum' in schema['allOf'][1]['then']['properties']['subtype']:\n    {'enum': ['folder', 'tariff_plan'], 'type': 'string'}\n\nOn instance['subtype']:\n    '%s'",
                subtype, subtype);
        assertEquals(expectedErrorMsg, error);
    }
}
