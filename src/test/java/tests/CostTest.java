package tests;

import core.CacheService;
import core.helper.Http;
import models.subModels.KafkaTopic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.Steps;
import steps.authorizer.AuthorizerSteps;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("cost1")
public class CostTest implements Tests {
    
    @Test
    public void getCost(){
        AuthorizerSteps authorizerSteps = new AuthorizerSteps();
        authorizerSteps.getAllProjectFromFolder("fold-456ks80urq");
    }

//    @Test
//    @Tag("test")
//    public void test(){
//        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
//        List<String> l = orderServiceSteps.getProductsWithStatus("DEV");
//
//        List all = new ArrayList();
//        List list;
//        int i = 0;
//        do {
//            list = new Http(OrderServiceSteps.URL)
//                    .setProjectId("proj-frybyv41jh")
//                    .get("calculator/orders/?folder=%2Forganization%2Fvtb%2Ffolder%2Ffold-s5wu0ff33x%2Ffolder%2Ffold-fr4o3pcghy%2Ffolder%2Ffold-humog4orc1%2Ffolder%2Ffold-cq6hc1jmgd%2Ffolder%2Ffold-dkj0exqf9q%2Ffolder%2Ffold-21yhccp939%2Fproject%2Fproj-frybyv41jh%2F&offset="+i)
//                    .assertStatus(200)
//                    .jsonPath()
//                    .getList("results");
//            all.addAll(list);
//            i += 100;
//        } while (list.size() != 0);
//
//        System.out.printf("1");
//    }
}
