package tests;

import models.orderService.interfaces.IProduct;
import models.orderService.products.Rhel;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.tarifficator.CostSteps;

import java.util.stream.Stream;
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("cost")
public class CostTest implements Tests {

    @Test
    public void test(){
        CostSteps costSteps = new CostSteps();
        costSteps.getCost(
                "dev-srv-app", "5", "Nutanix", "8.latest",
                "DEV", "Rhel", "corp.dev.vtb");
    }
}
