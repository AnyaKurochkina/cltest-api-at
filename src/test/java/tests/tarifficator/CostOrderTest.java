package tests.tarifficator;

import models.orderService.interfaces.IProduct;
import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.tarifficator.CostOrderTest")
@DisplayName("Набор тестов для проверки стоимости заказа")
@Tags({@Tag("regress"), @Tag("cost"), @Tag("orders"), @Tag("smoke")})
public class CostOrderTest implements Tests {
    CostSteps costSteps = new CostSteps();

    @ParameterizedTest
    @DisplayName("Проверка стоимости заказа")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @Timeout(60)
    public void getCost(IProduct product, String tmsId) {
        double preBillingCost = costSteps.getCurrentCost(product);
        double cost = costSteps.getPreBillingCost(product);
        //TODO: cost может быть null нужно ждать
        Assertions.assertEquals(preBillingCost, cost, 0.00001, "Стоимость предбиллинга отличается от стоимости продукта " + product);
    }
}
