package api.cloud.orderService.loadBalancer;

import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.RouteSni;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerSniNegativeTest extends AbstractLoadBalancerTest {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с невалидными названием {0}")
    void notValidMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Gslb gslb = createSimpleTcpGslb(balancer);
            RouteSni.Route route = new RouteSni.Route(SIMPLE_TCP_BACKEND_NAME, new Generex("[a-z0-9]{256}").random());
            AssertResponse.run(() -> balancer.addRoute(gslb.getGlobalname(), route)).status(422).responseContains("routes.0.name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение backend на несуществующий backend в маршруте sni {0}")
    void editRouteNotExistBackend(LoadBalancer product) {
        String backendName = new Generex("[a-z0-9]{20}").random();
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni.Route route = createSimpleRoute(balancer);
            Throwable throwable = Assertions.assertThrows(Error.class,
                    () -> balancer.editRouteSni(route, backendName));
            AssertUtils.assertContains(throwable.getMessage(), "backend_name");
        }
    }
}
