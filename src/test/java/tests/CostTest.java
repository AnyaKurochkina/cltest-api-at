package tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.tarifficator.CostSteps;
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@Tag("cost1")
public class CostTest implements Tests {



    @Test
    public void getCost(){
        CostSteps costSteps = new CostSteps();
        String tariffPlanId = costSteps.tariffTest();
        costSteps.getPrices(tariffPlanId);

    }
}
