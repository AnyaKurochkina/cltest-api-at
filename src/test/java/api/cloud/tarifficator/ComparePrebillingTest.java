package api.cloud.tarifficator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.products.Astra;
import org.json.JSONArray;
import org.junit.DisabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import api.Tests;

import java.util.HashMap;

@Epic("Финансы")
@Feature("Тарификатор")
@Tags({@Tag("regress"), @Tag("tariff")})
public class ComparePrebillingTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @DisabledIfEnv("prod")
    @TmsLink("654182")
    @ParameterizedTest(name = "[{index}] Сравнение стоимости продукта {0} в предбиллинге с ценами активного ТПО")
    public void compareTariffs(Astra resource){
        try (IProduct product = resource.createObjectExclusiveAccess()) {
            String tariffPlanId = CostSteps.getActiveTariffId();
            HashMap<String, Double> activeTariffPlanPrice = CostSteps.getPrices(tariffPlanId);
            JSONArray preBillingData = CostSteps.getCost(product);
            CostSteps.compareTariffs(activeTariffPlanPrice, preBillingData);
        }
    }

}
