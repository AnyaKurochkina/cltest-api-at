package tests.tarifficator;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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

@Epic("Финансы")
@Feature("Калькулятор")
@Tags({@Tag("tariff3")})
public class CalculatorTest extends Tests {
    CostSteps costSteps = new CostSteps();

    @ParameterizedTest(name = "Сравнение стоимости продукта {0} с ценой предбиллинга")
    @DisplayName("Сравнение стоимости продуктов с ценой предбиллинга")
    @Source(ProductArgumentsProvider.PRODUCTS)
    public void getCost(IProduct resource) {
        try (IProduct product = resource.createObjectExclusiveAccess()) {
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
}
