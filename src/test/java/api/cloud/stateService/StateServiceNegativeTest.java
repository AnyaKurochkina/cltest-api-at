package api.cloud.stateService;

import api.Tests;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.Item;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.StateServiceSteps.*;

@Tag("state_service")
@Epic("State Service")
@Feature("State Service api")
@DisabledIfEnv("prod")
public class StateServiceNegativeTest extends Tests {

    @Test
    @DisplayName("Получение списка связок order_id и item_id отфильтрованного по невалидным item_id")
    @TmsLink("1429736")
    public void getItemsIdOrderIdListFilteredByItemId() {
        List<Item> itemsIdOrderIdList = getItemIdOrderIdListByItemsIds("d08ae2aa-b1c6-11ed-afa1-0242ac120002").assertStatus(200)
                .jsonPath()
                .getList("list", Item.class);
        assertEquals(0, itemsIdOrderIdList.size());
    }

    @DisplayName("Попытка создать event с order_id отличный от item order_id")
    @TmsLink("SOUL-8454")
    @Test
    public void createEventWithDifferentOrderIdThanItem() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String itemId = createItem(project).getItemId();
        Action action = createAction();
        Graph graph = createGraph();
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEvent.json")
                .set("$.order_id", UUID.randomUUID().toString())
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.events[0].item_id", itemId)
                .set("$.events[0].graph_id", graph.getGraphId())
                .set("$.events[0].action_id", action.getActionId())
                .set("$.events[1].item_id", itemId)
                .set("$.events[1].graph_id", graph.getGraphId())
                .set("$.events[1].action_id", action.getActionId())
                .set("$.events[2].item_id", itemId)
                .set("$.events[2].graph_id", graph.getGraphId())
                .set("$.events[2].action_id", action.getActionId())
                .build();
        String errorMessage = createBulkAddEvent(project.getId(), json2).assertStatus(400).jsonPath()
                .getList("", String.class).get(0);
        assertTrue(errorMessage.contains("Changing order without request event"));
    }
}
