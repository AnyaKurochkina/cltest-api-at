package api.cloud.stateService;

import api.Tests;
import core.helper.JsonHelper;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.Item;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static core.helper.DateValidator.currentTimeInFormat;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.StateServiceSteps.*;

@Tag("state_service")
@Epic("State Service")
@Feature("State Service api")
public class StateServiceTest extends Tests {

    @Test
    @Disabled("Для вычисления длительности выполнения узлов при заказе/действии")
    public void getStateServiceNodesDuration() {
        String[] actions = new String[] {"e16629d7-7645-443c-881f-5b3dc73cd634",
                "44727fe8-562e-41f9-a486-6e80a0edd129",
                "6687783b-e609-44db-aa96-3789200456dc",
                "1b76b2cf-c6dc-429e-a980-ef01c3615169",
                "999b0e8b-247e-4fce-824b-47cbc0084207",
                "26e0e7d1-e64d-49a8-a935-6b544916db75",
                "d33d8070-ebf7-4a1f-824e-025538eddb97",
                "2cb97f0b-74a8-491e-9d35-430282082c05",
                "3cceb3b5-8dc5-4a9e-b152-63d9156bf0ed",
                "c490b62b-0410-40a0-9c42-7ee36ba53659",
                "28036e31-ad51-42ff-8bfb-b67e5b60df37",
                "7c3df685-b9af-4921-82bb-0516997100ed",
                "a85f5192-03d3-4d2e-891c-6e5e5f3745a4",
                "5049edf8-954b-4ae2-a3c7-3c13dd2619e3",
                "c14ce18d-63f4-4543-a8c3-5ac532ea6a9d",
                "2a04aae1-6167-46da-b18a-5dc422304e66",
                "e7ac5286-9d97-4091-a56d-c6fd46d54f24",
                "8af3546f-76f2-4c83-a0a6-dc814183c292",
                "5473d97a-3a13-4ad3-b172-2a89743874bd",
                "4ed5d6b6-a1be-430c-8b0e-392e06905f72"
        };
        getNodesDuration(actions);
    }

    @Test
    @DisplayName("Получение данных версии State Service")
    @TmsLink("1080847")
    public void getStateServiceVersionTest() {
        Response resp = getStateServiceVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
        assertNotNull(resp.jsonPath().get("stage"));
    }

    @Test
    @DisplayName("Передача в поле status пустой строки в Events")
    @TmsLink("1129758")
    public void createEventsWithStatusEmptyString() {
        Response response = createEventStateService(new JSONObject("{\n" +
                "            \"created_row_dt\": \"2022-08-25T11:37:50.346352+03:00\",\n" +
                "            \"create_dt\": \"2022-08-25T11:37:50.346362+03:00\",\n" +
                "            \"order_id\": \"7e2f2f29-2071-448c-86d8-ebe0dae77ddd\",\n" +
                "            \"action_id\": \"5037e95b-9100-4e94-897a-3c235f791f97\",\n" +
                "            \"graph_id\": \"7e2f2f29-2071-448c-86d8-ebe0dae77ddd\",\n" +
                "            \"type\": \"vm\",\n" +
                "            \"subtype\": \"parent\",\n" +
                "            \"status\": \"\",\n" +
                "            \"data\": null,\n" +
                "            \"item_id\": \"379d1263-e3d3-4fd9-92c9-d955922779ea\",\n" +
                "            \"update_data\": null\n" +
                "        }"));
        assertTrue(response.jsonPath().getString("status").isEmpty(), "Поле status не пустое");
    }

    @DisplayName("Получение статуса health")
    @TmsLink("1139586")
    @Test
    public void healthTest() {
        assertEquals("ok", getHealthStateService());
    }

    @Test
    @DisplayName("Получение item с параметром with_folder=true")
    @TmsLink("1283840")
    public void getItemWithFolderTrueTest() {
        String id = getItemsList().get(0).getItemId();
        assertFalse(getItemByIdAndFilter(id, "with_folder=true").getFolder().isEmpty(), "Значение поля folder пустое");
    }

    @DisplayName("Проверка обновления LastFolder")
    @TmsLink("1597282")
    @Test
    public void createBulkTest() {
        Project project = Project.builder().isForOrders(true).build().createObject();
        String uuid = UUID.randomUUID().toString();
        Action action = createAction();
        Graph graph = createGraph();
        JSONObject json = JsonHelper.getJsonTemplate("stateService/createAction.json")
                .set("$.order_ids", Collections.singletonList(uuid))
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.create_dt", currentTimeInFormat())
                .build();
        String expectedFolder = json.getJSONObject("data").get("folder").toString();
        JSONObject json2 = JsonHelper.getJsonTemplate("stateService/createBulkAddEvent.json")
                .set("$.order_id", uuid)
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.events[0].item_id", uuid)
                .set("$.events[0].graph_id", graph.getGraphId())
                .set("$.events[0].action_id", action.getActionId())
                .set("$.events[1].item_id", uuid)
                .set("$.events[1].graph_id", graph.getGraphId())
                .set("$.events[1].action_id", action.getActionId())
                .set("$.events[2].item_id", uuid)
                .set("$.events[2].graph_id", graph.getGraphId())
                .set("$.events[2].action_id", action.getActionId())
                .build();
        createBulkAddAction(project.getId(), json);
        createBulkAddEvent(project.getId(), json2);
        Item item = getItemsWithActionsByFilter("order_id", uuid).get(0);
        assertEquals(expectedFolder, item.getFolder());
        String newFolder = "/organization/vtb/folder/folder/fold-test/project/proj-test/";
        JSONObject newAction = JsonHelper.getJsonTemplate("stateService/createAction.json")
                .set("$.order_ids", Collections.singletonList(uuid))
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.data.folder", newFolder)
                .set("$.create_dt", currentTimeInFormat())
                .build();
        createBulkAddAction(project.getId(), newAction);
        Item newItem = getItemsWithActionsByFilter("order_id", uuid).get(0);
        assertEquals(newFolder, newItem.getFolder());
    }
}
