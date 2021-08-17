package tests.tarifficator;

import models.orderService.interfaces.IProduct;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.tarifficator.CostTest")
@DisplayName("Набор тестов для проверки предбиллинга продуктов")
@Tags({@Tag("regress"), @Tag("cost")})
public class CostTest implements Tests {
    CostSteps costSteps = new CostSteps();

    @ParameterizedTest
    @DisplayName("Проверка предбиллинга для продуктов")
    @Source(ProductArgumentsProvider.PRODUCTS)
    public void getCost(IProduct product){
        costSteps.getCurrentCost(product);
        String tariffPlanId = costSteps.tariffTest();
        costSteps.getPrices(tariffPlanId);
    }
}
