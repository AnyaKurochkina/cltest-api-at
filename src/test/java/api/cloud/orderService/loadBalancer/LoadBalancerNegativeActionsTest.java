package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeActionsTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить GSLB. Невалидный globalname {0}")
    void removeGslbNotValidGlobalName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
            balancer.addFrontendUseCache(LoadBalancerBackendChangeNegativeTest.frontend);
            String gslb = new Generex("[a-z]{64}").random();
            AssertResponse.run(() -> balancer.deleteGslbSource(gslb)).status(422).responseContains("globalname");
        }
    }

}
