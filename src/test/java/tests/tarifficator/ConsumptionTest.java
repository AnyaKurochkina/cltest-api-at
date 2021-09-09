package tests.tarifficator;

import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.authorizer.AuthorizerSteps;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.List;

@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("consumption")
public class ConsumptionTest implements Tests {

    @ParameterizedTest
    @TmsLink("31")
    @Source(ProductArgumentsProvider.ENV)
    public void getCost(String env){
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        CostSteps costSteps = new CostSteps();
        List<String> allSuccessProducts = orderServiceSteps.getProductsWithStatus(env,
                "damaged", "deprovisioned", "pending", "changing", "success");
        Float sumOfConsumptionOfAllProducts = costSteps.getConsumptionSumOfProducts(allSuccessProducts);
        AuthorizerSteps authorizerSteps = new AuthorizerSteps();
        String path = authorizerSteps.getPathToFolder("proj-xazpppulba");
        double consumptionOfProject = costSteps.getConsumptionByPath(path);
        Assertions.assertEquals(Double.parseDouble(String.valueOf(sumOfConsumptionOfAllProducts)), consumptionOfProject,
                "Расход проекта не соответсвтует сумме расхода всех продуктов");
    }
}
