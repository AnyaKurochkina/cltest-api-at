package tests.tarifficator;

import core.utils.Waiting;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.calculator.CalcCostSteps;
import steps.tarifficator.CostSteps;
import tests.Tests;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.tarifficator.CostOrderTest")
@DisplayName("Набор тестов для проверки стоимости заказа")
@Tags({@Tag("regress"),@Tag("prod"), @Tag("orders")})
public class CostOrderTest extends Tests {
    CostSteps costSteps = new CostSteps();

    @ParameterizedTest(name = "{0}")
    @TmsLink("32")
    @DisplayName("Проверка стоимости заказа")
    @Source(ProductArgumentsProvider.PRODUCTS)
    public void getCost(IProduct product, String tmsId) {
        Float preBillingCost = costSteps.getPreBillingCost(product);
        Float currentCost = costSteps.getCurrentCost(product);
        for (int i = 0; i < 15; i++) {
            Waiting.sleep(20000);
            if (Math.abs(currentCost - preBillingCost) > 0.00001)
                continue;
            break;
        }

        Assertions.assertEquals(preBillingCost, currentCost, 0.00001, "Стоимость предбиллинга отличается от стоимости продукта " + product);
    }
}
