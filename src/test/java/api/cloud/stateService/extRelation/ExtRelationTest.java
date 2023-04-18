package api.cloud.stateService.extRelation;

import api.Tests;
import core.helper.JsonHelper;
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

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.stateService.ExtRelationsStep.*;
import static steps.stateService.StateServiceSteps.createEventStateService;
import static steps.stateService.StateServiceSteps.createItem;

@Tag("state_service")
@Epic("State Service")
@Feature("Ext_Relations")
@DisabledIfEnv("prod")
public class ExtRelationTest extends Tests {

    private static Project project;
    private static Action action;
    private static Graph graph;
    private static final List<Integer> relationsIdsForDelete = new ArrayList<>();
    private static final String projects = "projects";
    private static Item primaryItem;
    private static Item secondaryItem;
    private static Item primaryItem2;

    @BeforeAll
    public static void init() {
        project = Project.builder()
                .isForOrders(true)
                .build()
                .createObject();
        action = createAction();
        graph = createGraph();
    }

    @BeforeEach
    public void createTestData() {
        String orderId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        JSONObject json = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(orderId)
                .itemId(itemId)
                .type("paas")
                .subtype("build")
                .build()
                .toJson();
        JSONObject secondaryJson = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("paas")
                .subtype("build")
                .build()
                .toJson();
        JSONObject json2 = Item.builder()
                .actionId(action.getActionId())
                .graphId(graph.getGraphId())
                .orderId(UUID.randomUUID().toString())
                .itemId(UUID.randomUUID().toString())
                .type("paas")
                .subtype("build")
                .build()
                .toJson();
        primaryItem = createItem(project.getId(), json);
        secondaryItem = createItem(project.getId(), secondaryJson);
        primaryItem2 = createItem(project.getId(), json2);
    }

    @AfterAll
    public static void deleteTestData() {
        relationsIdsForDelete.forEach(x -> deleteExtRelation(projects, project.getId(), x));
    }

    @DisplayName("Создание ExtRelation с параметром is_exclusive=false")
    @TmsLink("")
    @Test
    public void createExtRelationWithExclusiveFalseTest() {
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation actualRelation = getExtRelationById(projects, project.getId(), extRelation.getId());
        assertEquals(extRelation, actualRelation);

        ExtRelation extRelation2 = createExtRelation(projects, project.getId(), primaryItem2.getItemId(), secondaryItem.getItemId(),
                false);
        relationsIdsForDelete.add(extRelation2.getId());
        ExtRelation actualRelation2 = getExtRelationById(projects, project.getId(), extRelation2.getId());
        assertEquals(extRelation2, actualRelation2);
        assertEquals(actualRelation.getSecondaryItemId(), actualRelation2.getSecondaryItemId());
    }

    @DisplayName("Создание ExtRelation с параметром is_exclusive=true")
    @TmsLink("")
    @Test
    public void createExtRelationWithExclusiveTrueTest() {
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                true);
        relationsIdsForDelete.add(extRelation.getId());
        ExtRelation actualRelation = getExtRelationById(projects, project.getId(), extRelation.getId());
        assertEquals(extRelation, actualRelation);
        String error = createExtRelationResponse(projects, project.getId(), primaryItem2.getItemId(), secondaryItem.getItemId(),
                true)
                .assertStatus(400)
                .jsonPath()
                .getString("error");
        assertEquals("['Данный Item не может использоваться эксклюзивно, т.к. он уже используется как secondary']", error);
    }

    @DisplayName("Создание ExtRelation с параметром is_exclusive=true")
    @TmsLink("")
    @Test
    public void deleteExtRelationWhenItemStateChangedTest() {
        ExtRelation extRelation = createExtRelation(projects, project.getId(), primaryItem.getItemId(), secondaryItem.getItemId(),
                false);
        assertTrue(isRelationExistById(projects, project.getId(), extRelation.getId()), String.format("Relation c id - %d не существует", extRelation.getId()));
        JSONObject eventJson = JsonHelper.getJsonTemplate("stateService/createEvent.json")
                .set("$.order_id", primaryItem.getOrderId())
                .set("$.graph_id", graph.getGraphId())
                .set("$.action_id", action.getActionId())
                .set("$.item_id", primaryItem.getItemId())
                .set("$.type", primaryItem.getType())
                .set("$.subtype", "state")
                .set("$.status", "deleted")
                .build();
        createEventStateService(eventJson);
        assertFalse(isRelationExistById(projects, project.getId(), extRelation.getId()), String.format("Relation c id - %d существует", extRelation.getId()));
    }
}
