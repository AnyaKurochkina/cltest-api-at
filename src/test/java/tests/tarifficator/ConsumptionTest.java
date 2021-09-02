package tests.tarifficator;

import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("consumption")
public class ConsumptionTest implements Tests {

    @ParameterizedTest
    @Source(ProductArgumentsProvider.ENV)
    public void getCost(String env){
        CostSteps costSteps = new CostSteps();
        List<String> allSuccessProducts = costSteps.getProductsWithStatus(env,
                "damaged", "deprovisioned", "pending", "changing");
        Float sumOfConsumptionOfAllProducts = costSteps.getConsumptionSumOfProducts(allSuccessProducts);
        System.out.println(sumOfConsumptionOfAllProducts);
    }
}
