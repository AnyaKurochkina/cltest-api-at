package api.cloud.stateService.item;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.stateService.Item;
import models.cloud.stateService.extRelations.ExtRelation;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.ExtRelationsStep.createExtRelation;
import static steps.stateService.ExtRelationsStep.deleteExtRelation;
import static steps.stateService.StateServiceSteps.*;

@Tag("state_service")
@Epic("State Service")
@Feature("Item")
@DisabledIfEnv("prod")
public class ItemTest extends Tests {

    private static Project project;
    private static Action action;
    private static Graph graph;
    private static final String projects = "projects";
    private static final List<Integer> relationsIdsForDelete = new ArrayList<>();

    @BeforeAll
    public static void init() {
        project = Project.builder()
                .isForOrders(true)
                .build()
                .createObject();
        action = createAction();
        graph = createGraph();
    }

    @AfterAll
    public static void deleteTestData() {
        relationsIdsForDelete.forEach(x -> deleteExtRelation(projects, project.getId(), x));
    }

    @DisplayName("Получение списка primary items по items/as_secondary_items/")
    @TmsLink("1744953")
    @Test
    public void getPrimaryItemsListBySecondaryItemIdTest() {
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject json = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject secondaryJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject json2 = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        Item primaryItem = createItem(project.getId(), json);
        Item secondaryItem = createItem(project.getId(), secondaryJson);
        Item primaryItem2 = createItem(project.getId(), json2);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation extRelation2 = createExtRelation(projects, project.getId(), primaryItem2.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation2.getId());
        List<Item> primaryItemsList = getPrimaryItemsList(secondaryItem.getItemId());
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(primaryItem.getItemId())));
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(primaryItem2.getItemId())));
    }

    @DisplayName("Получение списка secondary items по items/as_primary_items/")
    @TmsLink("1745012")
    @Test
    public void getSecondaryItemsListByPrimaryItemIdTest() {
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject json = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject secondaryJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject secondaryJson2 = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        Item primaryItem = createItem(project.getId(), json);
        Item secondaryItem = createItem(project.getId(), secondaryJson);
        Item secondaryItem2 = createItem(project.getId(), secondaryJson2);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation extRelation2 = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem2.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation2.getId());
        List<Item> primaryItemsList = getSecondaryItemsList(primaryItem.getItemId());
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(secondaryItem.getItemId())));
        assertTrue(primaryItemsList.stream().anyMatch(item -> item.getItemId().equals(secondaryItem2.getItemId())));
    }

    @DisplayName("Получение списка items у которых нет primary связи с конкретным item_id")
    @TmsLink("1745012")
    @Test
    public void getItemsListWithOutPrimaryRelationTest() {
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject json = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject secondaryJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("app")
                .subtype("acls")
                .build()
                .toJson();
        Item primaryItem = createItem(project.getId(), json);
        Item secondaryItem = createItem(project.getId(), secondaryJson);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        List<Item> primaryItemsList = getItemsListWithOutPrimaryRelation(secondaryItem.getItemId());
    }

    @DisplayName("Получение списка items у которых нет secondary связи с конкретным item_id")
    @TmsLink("1745012")
    @Test
    public void getItemsListWithOutSecondaryRelationTest() {
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject json = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("bm")
                .subtype("acls")
                .build()
                .toJson();
        JSONObject secondaryJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("app")
                .subtype("acls")
                .build()
                .toJson();
        Item primaryItem = createItem(project.getId(), json);
        Item secondaryItem = createItem(project.getId(), secondaryJson);
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        List<Item> secondaryItemsList = getItemsListWithOutSecondaryRelation(primaryItem.getItemId());
    }
}
