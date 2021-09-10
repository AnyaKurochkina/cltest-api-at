package tests.tarifficator;

import models.tarifficator.TariffPlan;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import steps.tarifficator.TariffPlanSteps;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("tariffPlans")
public class TariffPlanTests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();

    @Test
    public void createBaseTariffPlanFromActive() {
        TariffPlan tariffPlan = tariffPlanSteps.createTariffPlan(TariffPlan.builder()
                .title("AT #7")
                .base(true)
                .oldTariffPlanId("e33296f8-8112-4800-a214-7b3c36ece765")
                .build());

    }
}
