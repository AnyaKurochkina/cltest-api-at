package tests;

import models.orderService.interfaces.IProduct;
import models.orderService.products.Rhel;
import org.junit.OrderLabel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
        costSteps.getCost(1,1,"c1m1", 10,
                "dev-srv-app", "5", "Nutanix", "8.latest",
                "DEV", "Rhel", "corp.dev.vtb", "c422069e-8f01-4328-b9dc-4a9e5dafd44e");
    }
}
