package tests.productCatalog;

import core.helper.Deleted;
import models.productCatalog.Action;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("zalupa")
public class ActionsTest {

    @Test
    public void createAction() {
//        GraphSteps createGraphResponse = new GraphSteps();
//        ActionsSteps actionsSteps = new ActionsSteps();
//        String graphId = createGraphResponse.getGraph("AtTestGraph");
//        actionsSteps.createAction("TestAction",graphId);
//        String actionId = actionsSteps.getActionId("TestAction");
//        actionsSteps.deleteAction(actionId);
//        String actionIdAfterDelete = actionsSteps.getActionId("TestAction");
//        Assertions.assertNull(actionIdAfterDelete);
        Action.builder().actionName("TestObjectAT").build().createObject();
    }

    @Test
    @Deleted
    public void deleteAction() {
//        GraphSteps createGraphResponse = new GraphSteps();
//        ActionsSteps actionsSteps = new ActionsSteps();
//        String graphId = createGraphResponse.getGraph("AtTestGraph");
//        actionsSteps.createAction("TestAction",graphId);
//        String actionId = actionsSteps.getActionId("TestAction");
//        actionsSteps.deleteAction(actionId);
//        String actionIdAfterDelete = actionsSteps.getActionId("TestAction");
//        Assertions.assertNull(actionIdAfterDelete);
        Action.builder().actionName("TestObjectAT1").build().createObject().deleteObject();
    }
}

