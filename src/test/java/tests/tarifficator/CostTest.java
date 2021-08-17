package tests.tarifficator;

import models.orderService.interfaces.IProduct;
import org.json.JSONArray;
import org.json.JSONArray;
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

import java.util.HashMap;

import java.util.HashMap;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@OrderLabel("tests.tarifficator.CostTest")
@Tags({@Tag("regress"), @Tag("cost")})
public class CostTest implements Tests {
    CostSteps costSteps = new CostSteps();

    @ParameterizedTest
    @DisplayName("Сравнение услуг продуктов предбиллинга с услугами продуктов активного тарифного плана")
    @Source(ProductArgumentsProvider.PRODUCTS)
    public void compareTariffs(IProduct product){
        //Получаем ID активного тарифного плана
        String tariffPlanId = costSteps.getActiveTariffId();
        //Получаем прайс активного тарифного плана
        HashMap<String, Double> activeTariffPlanPrice = costSteps.getPrices(tariffPlanId);
        //Получаем цены услуг из предбиллинга
        JSONArray preBillingData = costSteps.getCost(product);
        //Сравниваем цены из предбиллинга с тарифами из активного тарифного плана
        costSteps.compareTariffs(activeTariffPlanPrice, preBillingData);
    }
}
