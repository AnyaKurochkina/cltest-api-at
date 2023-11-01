package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.*;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerRouteNegativeTest extends Tests {
    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/8e61b88a-1411-4df7-862e-a4e6d4baf05a/main?context=proj-114wetem0c&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = LoadBalancerPositiveTest.createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = LoadBalancerPositiveTest.createFrontendTcp(backend.getBackendName(), new Generex("[a-zA-Z0-9]{20}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = LoadBalancerPositiveTest.createGslb(frontend, new Generex("[a-z0-9]{20}").random());
            balancer.addGslb(gslb);
            AssertResponse.run(() -> balancer.addRouteSni(LoadBalancerPositiveTest.createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{256}").random())));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка изменения бекэнда на невалидный бекэнд в маршруте sni {0}")
    void editRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = LoadBalancerPositiveTest.createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = LoadBalancerPositiveTest.createFrontendTcp(backend.getBackendName(), new Generex("[a-zA-Z0-9]{20}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = LoadBalancerPositiveTest.createGslb(frontend, new Generex("[a-z0-9]{20}").random());
            balancer.addGslb(gslb);
            RouteSni route = LoadBalancerPositiveTest.createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{20}").random());
            balancer.addRouteSni(route);

            AssertResponse.run(() -> balancer.editRouteSni(route, new Generex("[a-z0-9]{20}").random()));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка удаления маршрута sni по невалидному названию {0}")
    void removeRouteNotValidRouteName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = LoadBalancerPositiveTest.createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = LoadBalancerPositiveTest.createFrontendTcp(backend.getBackendName(), new Generex("[a-zA-Z0-9]{20}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = LoadBalancerPositiveTest.createGslb(frontend, new Generex("[a-z0-9]{20}").random());
            balancer.addGslb(gslb);
            RouteSni route = LoadBalancerPositiveTest.createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{20}").random());
            balancer.addRouteSni(route);

            AssertResponse.run(() -> balancer.deleteRouteSni(route, getRouteByDeleteNotValid(balancer, route)));
        }
    }

    @Step("Получение Route для удаления в невалидном формате")
    public static RouteSni.RouteCheck getRouteByDeleteNotValid(LoadBalancer balancer, RouteSni routeSni) {
        RouteSni.RouteCheck route = (RouteSni.RouteCheck) OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
        route.setRouteName(new Generex("[a-z0-9]{20}").random());
        return route;
    }
}