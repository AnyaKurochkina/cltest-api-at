package api.cloud.stateService.event;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.EventStateService;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.StateServiceSteps.createBulkAddEvent;
import static steps.stateService.StateServiceSteps.getEventListByFilter;

@Tag("state_service")
@Epic("State Service")
@Feature("Events")
@DisabledIfEnv("prod")
public class BulkAddEventTest extends Tests {

    @DisplayName("Создание Bulk-add-event")
    @TmsLink("1596575")
    @Test
    public void createBulkAddEventTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String uuid = UUID.randomUUID().toString();
        Action action = createAction("bulk_add_event_api_test");
        Graph graph = createGraph("bulk_add_event_graph_api_test");
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEvent.json")
                .set("$.order_id", uuid)
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.events[0].item_id", uuid)
                .set("$.events[0].graph_id", uuid)
                .set("$.events[0].action_id", uuid)
                .set("$.events[1].item_id", uuid)
                .set("$.events[1].graph_id", uuid)
                .set("$.events[1].action_id", uuid)
                .set("$.events[2].item_id", uuid)
                .set("$.events[2].graph_id", uuid)
                .set("$.events[2].action_id", uuid)
                .build();
        EventStateService expectedEvent = createBulkAddEvent(project.getId(), json2)
                .assertStatus(201)
                .jsonPath()
                .getObject("[0]", EventStateService.class);
        EventStateService actualEvent = getEventListByFilter("action_id", action.getActionId()).get(2);
        assertEquals(expectedEvent, actualEvent);
    }

    @DisplayName("Негативный тест на создание Bulk-add-event")
    @TmsLink("1597271")
    @Test
    public void negativeCreateBulkAddEventTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String uuid = UUID.randomUUID().toString();
        Action action = createAction();
        Graph graph = createGraph();
        String subtype = RandomStringUtils.randomAlphabetic(6).toLowerCase();
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEvent.json")
                .set("$.order_id", uuid)
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.events[0].item_id", uuid)
                .set("$.events[0].subtype", subtype)
                .set("$.events[1].item_id", uuid)
                .set("$.events[1].graph_id", graph.getGraphId())
                .set("$.events[1].action_id", action.getActionId())
                .set("$.events[2].item_id", uuid)
                .set("$.events[2].graph_id", graph.getGraphId())
                .set("$.events[2].action_id", action.getActionId())
                .build();
        String error = createBulkAddEvent(project.getId(), json2)
                .assertStatus(400)
                .jsonPath()
                .getList("", String.class)
                .get(0);
        String expectedErrorMsg = String.format("'%s' is not one of ['acls', 'build', 'state', 'config', 'parent', 'env_type', 'provider', 'warnings', 'inventory', 'on_support', 'host_groups', 'config_audit', 'src_order_id', 'inventory_status']\n" +
                        "\n" +
                        "Failed validating 'enum' in schema['properties']['subtype']:\n" +
                        "    {'enum': ['acls',\n" +
                        "              'build',\n" +
                        "              'state',\n" +
                        "              'config',\n" +
                        "              'parent',\n" +
                        "              'env_type',\n" +
                        "              'provider',\n" +
                        "              'warnings',\n" +
                        "              'inventory',\n" +
                        "              'on_support',\n" +
                        "              'host_groups',\n" +
                        "              'config_audit',\n" +
                        "              'src_order_id',\n" +
                        "              'inventory_status'],\n" +
                        "     'type': 'string'}\n" +
                        "\n" +
                        "On instance['subtype']:\n" +
                        "    '%s'",
                subtype, subtype);
        assertEquals(expectedErrorMsg, error);

    }
}
