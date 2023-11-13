package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.RouteSni;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import static api.cloud.orderService.loadBalancer.LoadBalancerSniTest.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerSniNegativeTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с невалидными названием {0}")
    void notValidMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpFrontend(balancer);
            AssertResponse.run(() -> balancer.addRouteSni(addRoute(frontend.getDefaultBackendNameTcp(), addTcpGslb(balancer, frontend).getGlobalname(), false)));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменения бекэнда на несуществующий бекэнд в маршруте sni {0}")
    void editRouteNotExistBackend(LoadBalancer product) {
        String backendName = new Generex("[a-z0-9]{20}").random();
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            try {
                RouteSni route = addTcpRoute(balancer);
                balancer.editRouteSni(route, backendName);
            } catch (Throwable e) {
                assertTrue(e.getMessage().contains("Backend `" + backendName + "` was not found"));
            }
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с ранее используемым бекэндом в другом маршруте {0}")
    void editRouteUsedBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            Frontend frontend = addTcpFrontend(balancer);
            Gslb gslb = addTcpGslb(balancer, frontend);
            AssertResponse.run(() -> balancer.addRouteSni(addRoute(route.getRoutes().get(0).getBackendName(), gslb.getGlobalname(), true)));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаления маршрута sni по невалидному названию {0}")
    void removeRouteNotValidRouteName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            AssertResponse.run(() -> balancer.deleteRouteSni(RouteSni.builder().routes(route.getRoutes()).globalname(route.getGlobalname().replace("-tcp","")).build()));
        }
    }
}
