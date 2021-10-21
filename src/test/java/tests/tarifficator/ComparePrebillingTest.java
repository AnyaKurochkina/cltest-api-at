package tests.tarifficator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.junit.OrderLabel;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.HashMap;

@Epic("Финансы")
@Feature("Тарификатор")
@Tags({@Tag("regress"), @Tag("tariff2")})
public class ComparePrebillingTest extends Tests {
    CostSteps costSteps = new CostSteps();

    @DisplayName("Сравнение стоимости продуктов в предбиллинге с ценами активного тарифного плана")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сравнение стоимости продукта {0} в предбиллинге с ценами активного тарифного плана")
    public void compareTariffs(IProduct product){
        String tariffPlanId = costSteps.getActiveTariffId();
        HashMap<String, Double> activeTariffPlanPrice = costSteps.getPrices(tariffPlanId);
        JSONArray preBillingData = costSteps.getCost(product);
        costSteps.compareTariffs(activeTariffPlanPrice, preBillingData);
    }

}
