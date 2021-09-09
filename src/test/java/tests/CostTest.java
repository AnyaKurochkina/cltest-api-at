package tests;

import core.CacheService;
import core.helper.Http;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import models.authorizer.Project;
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
@Execution(ExecutionMode.CONCURRENT)
@Tag("cost2")
public class CostTest implements Tests {


    @Test
    public void getCost(){
//        CostSteps costSteps = new CostSteps();
//        String tariffPlanId = costSteps.getActiveTariffId();
//        costSteps.getPrices(tariffPlanId);
        System.out.println(1);
    }


//    @ParameterizedTest
//    @Order(1)
//    @DisplayName("Создание проекта")
//    @Source(ProductArgumentsProvider.ENV)
//    @Description("Создание проекта с сохранением в Shared Memory")
//    public void createProject(String env, String tmsId) {
//
//
//        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
//        List<String> list = orderServiceSteps.getProductsWithStatus(env, "damaged", "changing", "pending", "success");
//
//
//        for (String id : list){
//            System.out.println(id);
//        }
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






//    @Tag("test")
//    @ParameterizedTest
//    @MethodSource("provideStringsForIsBlank")
//    void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input, boolean expected) {
//        System.out.println(input);
//    }
//
//
//    private static Stream<Arguments> provideStringsForIsBlank() {
//        return null;
//    }

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
