package tests;

import core.CacheService;
import core.helper.Http;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
import models.orderService.products.WildFly;
import models.subModels.KafkaTopic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import steps.Steps;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("cost2")
public class CostTest extends Tests {

//    @Test
//    public void ActionCreateDeleteTest(){
        //        GraphSteps createGraphResponse = new GraphSteps();
//        ActionsSteps actionsSteps = new ActionsSteps();
//        String graphId = createGraphResponse.getGraph("AtTestGraph");
//        actionsSteps.createAction("TestAction",graphId);
//        String actionId = actionsSteps.getActionId("TestAction");
//        actionsSteps.deleteAction(actionId);
//        String actionIdAfterDelete = actionsSteps.getActionId("TestAction");
//        Assertions.assertNull(actionIdAfterDelete);
//    }

//    @Test
//    void removeDbmsUser1() {
//            PostgreSQL postgreSQL= new PostgreSQL("68f64f59-5072-48c1-9ba6-80129fa18d2e",
//                    "proj-frybyv41jh",
//                    "PostgreSQL");
////            postgreSQL.createDb("createdbforremove");
//            postgreSQL.createDbmsUser("chelikforreset15", "user", "createdbforremove");
//            postgreSQL.removeDbmsUser("chelikforreset15", "createdbforremove");
//    }

    @ParameterizedTest
    @Order(1)
    @ValueSource(strings = {"proj-ti717c4xdp", "proj-hnsmiuaqg2"})
    public void createProject(String proj) {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        List<String> list = orderServiceSteps.getProductsWithStatus(proj, "damaged", "changing", "pending");
        for (String id : list){
            System.out.println(id);
        }
    }
}
