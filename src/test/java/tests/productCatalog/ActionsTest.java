package tests.productCatalog;

import core.helper.Deleted;
import core.helper.JsonHelper;
import httpModels.productCatalog.getActions.response.ActionResponse;
import httpModels.productCatalog.patchActions.response.PatchResponse;
import models.productCatalog.Action;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.productCatalog.ActionsSteps;
import steps.productCatalog.GraphSteps;
import tests.Tests;

import static core.helper.JsonHelper.convertResponseOnClass;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class ActionsTest extends Tests {
    ActionsSteps actionsSteps = new ActionsSteps();
    Action action;

    @Order(1)
    @Test
    public void createAction() {
        action = Action.builder().actionName("TestObjectAT").build().createObject();
    }

    @Order(2)
    @Test
    public void searchActionByName() {
        String actionIdWithMultiSearch = actionsSteps.getActionByNameWithMultiSearch("TestObjectAT");
        Assertions.assertNotNull(actionIdWithMultiSearch, String.format("Экшен с именем: %s не найден", "TestObjectAT"));
        Assertions.assertEquals(action.getActionId(), actionIdWithMultiSearch);
    }

    @Order(3)
    @Test
    public void doubleVersionTest() {
        JsonHelper jsonHelper = new JsonHelper();
        GraphSteps graphSteps = new GraphSteps();
        actionsSteps.createAction(jsonHelper.getJsonTemplate("productCatalog/actions/createAction.json")
                .set("$.name", "NegativeAction")
                .set("$.title", "NegativeAction")
                .set("$.description", "NegativeAction")
                .set("$.graph_id", action.getGraphId())
//                .set("$.graph_version", "1.0.0")
                .set("$.graph_version_pattern", "1.")
//                .set("$.required_order_statuses[0]", "success")
//                .set("$.event_type[0]", "bm")
//                .set("$.event_provider[0]", "s3")
//                .set("$.type", "deleted")
                .build()).assertStatus(404);
    }

    @Order(4)
    @Test
    public void patchTest() {
        JsonHelper jsonHelper = new JsonHelper();
        GraphSteps graphSteps = new GraphSteps();
        actionsSteps.patchAction("TestObjectAT", action.getGraphId(), action.getActionId());
        String response = actionsSteps.patchActionRow(jsonHelper.getJsonTemplate("productCatalog/actions/patchVersion.json")
                .set("$.name", "TestObjectAT")
                .set("$.title", "TestObjectAT")
                .set("$.description", "TestObjectAT1")
                .set("$.graph_id", action.getGraphId())
                .build(), action.getActionId()).assertStatus(200).toString();

        PatchResponse mappedResponse = convertResponseOnClass(response, PatchResponse.class);
        Assertions.assertEquals("1.0.1", mappedResponse.getGraphVersion());
    }


    @Order(100)
    @Test
    @Deleted
    public void deleteAction() {
        try (Action action = Action.builder().actionName("TestObjectAT").build().createObjectExclusiveAccess()){
            action.deleteObject();
        }
    }
}

