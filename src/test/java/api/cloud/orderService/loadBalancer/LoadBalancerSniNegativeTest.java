package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.RouteSni;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import static api.cloud.orderService.loadBalancer.LoadBalancerSniTest.*;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerSniNegativeTest extends Tests {
    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/8e61b88a-1411-4df7-862e-a4e6d4baf05a/main?context=proj-114wetem0c&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с невалидными названием {0}")
    void notValidMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Gslb gslb = addTcpGslb(balancer);
            RouteSni route = addRoute(balancer, gslb.getFrontend().getDefaultBackendName(), gslb.getGlobalname(),false);
            AssertResponse.run(() -> balancer.addRouteSni(route));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменения бекэнда на несуществующий бекэнд в маршруте sni {0}")
    void editRouteNotExistBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            AssertResponse.run(() -> balancer.editRouteSni(route, new Generex("[a-z0-9]{20}").random()));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с ранее используемым бекэндом в другом маршруте {0}")
    void editRouteUsedBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route1 = addTcpRoute(balancer);
            Gslb gslb = addTcpGslb(balancer);
            RouteSni route2 = addRoute(balancer, route1.routes.get(0).getBackendName(), gslb.getGlobalname(),true);
            AssertResponse.run(() -> balancer.addRouteSni(route2));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаления маршрута sni по невалидному названию {0}")
    void removeRouteNotValidRouteName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            AssertResponse.run(() -> balancer.deleteRouteSni(route, getRouteByDeleteNotValid(balancer, route)));
        }
    }

    @Step("Получение Route для удаления в невалидном формате")
    public static RouteSni.RouteCheck getRouteByDeleteNotValid(LoadBalancer balancer, RouteSni routeSni) {
        RouteSni.RouteCheck route = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
        route.setRouteName(new Generex("[a-z0-9]{20}").random());
        return route;
    }
}
