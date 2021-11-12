package tests.productCatalog;

import models.productCatalog.Action;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ActionsSteps;
import steps.productCatalog.GraphSteps;

public class ActionsTest {

    @Test
    public void createACtion(){
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
    public void deleteAction(){
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
