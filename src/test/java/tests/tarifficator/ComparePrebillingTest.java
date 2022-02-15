package tests.tarifficator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import models.orderService.products.Rhel;
import org.json.JSONArray;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.HashMap;

@Epic("Финансы")
@Feature("Тарификатор")
@Tags({@Tag("regress"), @Tag("tariff")})
public class ComparePrebillingTest extends Tests {
    final CostSteps costSteps = new CostSteps();

    @Source(ProductArgumentsProvider.PRODUCTS)
    @TmsLink("654182")
    @ParameterizedTest(name = "Сравнение стоимости продукта {0} в предбиллинге с ценами активного ТПО")
    public void compareTariffs(Rhel resource){
        try (IProduct product = resource.createObjectExclusiveAccess()) {
            String tariffPlanId = costSteps.getActiveTariffId();
            HashMap<String, Double> activeTariffPlanPrice = costSteps.getPrices(tariffPlanId);
            JSONArray preBillingData = costSteps.getCost(product);
            costSteps.compareTariffs(activeTariffPlanPrice, preBillingData);
        }
    }

}
