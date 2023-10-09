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
import org.junit.DisabledIfEnv;
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
@DisabledIfEnv("prod")
public class StateServiceTest extends Tests {

    @Test
    @Disabled("Для вычисления длительности выполнения узлов при заказе/действии")
    public void getStateServiceNodesDuration() {
        String[] actions = new String[] {"de2fb5b7-9211-43ca-afac-8c2166dee386", "5ee959c1-253d-4669-94a6-5de3fedceeab", "d0c6ef6b-5965-4f79-a9c0-f71f2f8a64f4",
                "b742cda5-48ec-4b41-ba4e-7d118df134e6", "a5cb7230-d5ad-4203-8ef3-37fdea373b97", "b6a72062-07e5-48f9-9bb6-77a29ae1ed34", "02216196-46c5-42a4-a2f0-e694425e90b9",
                "f0d577a6-e663-4a68-8edc-28da87b29ec9", "6b71cf6f-5599-43ef-b85c-d28c4f8fdddf", "1facc47d-79b2-4b55-9b8e-e1736358354f", "5cd1539b-6a02-4959-b9a8-c22e38303fb2",
                "30b82f8d-0e9f-4b3f-8994-13a101025d57", "7ec1ffba-1307-496a-bf59-d0aa3d5d8962", "e715f1fb-51e7-43ee-8280-acffdf013c90", "aab6cfb3-17b8-4785-8d68-9aed2fff7bd3",
                "3519e4e3-4692-4b1e-9795-6699630dd28d", "521e508e-5100-4276-b9f2-4fd00f12befe", "a8e40fda-619b-47ff-b555-2bcf0a0cef8d", "cbfb8672-fd37-4d0b-88ac-d7b87f9b5b7f",
                "e481d162-1303-44ef-bc72-fe84500e41c0"};
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
