package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
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

import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerSniTest extends Tests {
    private static final String ROUTE_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}";
    static final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
            //.buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/8e61b88a-1411-4df7-862e-a4e6d4baf05a/main?context=proj-114wetem0c&type=project&org=vtb");
              .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/76e2f498-36f5-43c9-88f2-a664fec0a7f0/frontends?context=proj-2xdbtyzqs3&type=project&org=vtb");

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка sni маршрута {0}")
    void createRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpFrontend(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                    .frontend(frontend)
                    .build();
            balancer.addGslb(gslb);
            RouteSni route = addRoute(balancer, frontend.getDefaultBackendName(), gslb.getGlobalname(),true);
            balancer.addRouteSni(route);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление маршрута sni {0}")
    void deleteRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            balancer.deleteRouteSni(route, getRouteByDelete(balancer, route));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение маршрута sni {0}")
    void editRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            Gslb gslb = addTcpGslb(balancer);
            balancer.editRouteSni(route, gslb.getFrontend().getDefaultBackendNameTcp());
        }
    }

    static RouteSni addRoute(LoadBalancer balancer, String backendName, String globalName, boolean valid) {
        RouteSni.DnsPrefix dnsPrefixes = OrderServiceSteps.getObjectClass(balancer, String.format(GSLIB_PATH, globalName), RouteSni.DnsPrefix.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, new Generex(valid ? "snitcp[0-9]{10}" : "[a-z0-9]{256}").random()));
        RouteSni routeSni = RouteSni.builder().routes(routes).dnsPrefix(dnsPrefixes).build();
        return routeSni;
    }

    @Step("Получение Route в формате для удаления")
    public static RouteSni.RouteCheck getRouteByDelete(LoadBalancer balancer, RouteSni routeSni) {
        return OrderServiceSteps.getObjectClass(balancer, String.format(ROUTE_PATH, routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
    }

    static Frontend addTcpFrontend(LoadBalancer balancer) {
        Backend backend = Backend.builder()
                .servers(serversTcp)
                .backendName(new Generex("backend_tcp_[0-9]{10}").random())
                .advancedCheck(false)
                .build();
        balancer.addBackend(backend);
        Frontend frontend = Frontend.builder()
                .frontendName(new Generex("frontend_tcp_[0-9]{10}").random())
                .defaultBackendNameTcp(backend.getBackendName())
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    static Gslb addTcpGslb(LoadBalancer balancer) {
        Frontend frontend = addTcpFrontend(balancer);
        Gslb gslb = Gslb.builder()
                .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                .frontend(frontend)
                .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    static RouteSni addTcpRoute(LoadBalancer balancer) {
        Gslb gslb = addTcpGslb(balancer);
        RouteSni route = addRoute(balancer, gslb.getFrontend().getDefaultBackendName(), gslb.getGlobalname(),true);
        balancer.addRouteSni(route);
        return route;
    }

}
