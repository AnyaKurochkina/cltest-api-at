package tests.tarifficator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import models.orderService.products.Rhel;
import org.json.JSONArray;
import org.junit.DisabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.CostSteps;
import tests.Tests;

import java.util.HashMap;

@Epic("Финансы")
@Feature("Тарификатор")
@Tags({@Tag("regress"), @Tag("tariff")})
public class ComparePrebillingTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @DisabledIfEnv("prod")
    @TmsLink("654182")
    @ParameterizedTest(name = "Сравнение стоимости продукта {0} в предбиллинге с ценами активного ТПО")
    public void compareTariffs(Rhel resource){
        try (IProduct product = resource.createObjectExclusiveAccess()) {
            String tariffPlanId = CostSteps.getActiveTariffId();
            HashMap<String, Double> activeTariffPlanPrice = CostSteps.getPrices(tariffPlanId);
            JSONArray preBillingData = CostSteps.getCost(product);
            CostSteps.compareTariffs(activeTariffPlanPrice, preBillingData);
        }
    }

}
