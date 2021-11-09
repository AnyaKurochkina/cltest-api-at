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

    @Test
    void updateCerts() {
        WildFly wildFly = new WildFly("f529a1ce-d2e5-4ae3-ab24-30b5a7957be2",
                "proj-frybyv41jh", "WildFly");
        wildFly.updateCerts();
    }

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
