package tests;

import core.CacheService;
import models.subModels.KafkaTopic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.calculator.CalcCostSteps;
import steps.tarifficator.CostSteps;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("cost1")
public class CostTest implements Tests {



    @Test
    public void getCost(){
        CostSteps costSteps = new CostSteps();
        String tariffPlanId = costSteps.getActiveTariffId();
        costSteps.getPrices(tariffPlanId);
    }

//    @Test
//    @Tag("test")
//    public void test(){
//        CacheService cacheService = new CacheService();
//        String[] names = new String[]{"1", "2"};
//        List<KafkaTopic> kafkaTopics = new ArrayList<>();
//        for(String name : names)
//            kafkaTopics.add(new KafkaTopic());
//        String s = new JSONObject("{\"topics\": " + cacheService.toJson(kafkaTopics) + "}").toString();
//        System.out.println(s);
//    }
}
