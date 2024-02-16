package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Step;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.RouteSni;

//@Execution(ExecutionMode.SAME_THREAD)
public class AbstractLoadBalancerTest extends Tests {
//    @Mock
//    public static LoadBalancer loadBalancer = LoadBalancer.builder().platform("OpenStack").env("DEV").segment("dev-srv-app").build()
//            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/b82ea66c-c293-45ce-a6eb-bae07b368c55/main?context=proj-ln4zg69jek&type=project&org=vtb");

    protected final static String UNIQUE_POSTFIX = new Generex("[a-z]{5}").random();
    protected final static String SIMPLE_TCP_BACKEND_NAME = "simple_tcp_backend";
    protected final static String SIMPLE_TCP_FRONTEND_NAME = "simple_tcp_frontend";
    protected final static String SIMPLE_HTTP_BACKEND_NAME = "simple_http_backend";
    protected final static String SIMPLE_HTTP_FRONTEND_NAME = "simple_http_frontend";
    protected final static String SIMPLE_ROUTE_NAME = "simple-route-at";
    protected final static String SIMPLE_GSLB_NAME = "simple-gslb-" + UNIQUE_POSTFIX;

    @Step("Создание простого TCP Frontend")
    protected static Frontend createSimpleTcpFrontend(LoadBalancer balancer) {
        Frontend frontend = Frontend.simpleTcpFrontend(createSimpleTcpBackend(balancer).getBackendName())
                .frontendName(SIMPLE_TCP_FRONTEND_NAME).build();
        balancer.addFrontendUseCache(frontend);
        return frontend;
    }

    @Step("Создание простого TCP Backend")
    protected static Backend createSimpleTcpBackend(LoadBalancer balancer) {
        Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName(SIMPLE_TCP_BACKEND_NAME).build();
        balancer.addBackendUseCache(backend);
        return backend;
    }

    @Step("Создание простого HTTP Frontend")
    protected static Frontend createSimpleHttpFrontend(LoadBalancer balancer) {
        Frontend frontend = Frontend.simpleHttpFrontend(createSimpleHttpBackend(balancer).getBackendName())
                .frontendName(SIMPLE_HTTP_FRONTEND_NAME).build();
        balancer.addFrontendUseCache(frontend);
        return frontend;
    }

    @Step("Создание простого HTTP Backend")
    protected static Backend createSimpleHttpBackend(LoadBalancer balancer) {
        Backend backend = Backend.simpleHttpBackendWidthHttpCheck().backendName(SIMPLE_HTTP_BACKEND_NAME).build();
        balancer.addBackendUseCache(backend);
        return backend;
    }

    @Step("Создание простого TCP GSLB")
    protected static Gslb createSimpleTcpGslb(LoadBalancer balancer) {
        Gslb gslb = Gslb.builder().frontend(createSimpleTcpFrontend(balancer).getFrontendName()).globalname("tcp-" + SIMPLE_GSLB_NAME).build();
        balancer.addGslbUseCache(gslb);
        return gslb;
    }

    @Step("Создание простого HTTP GSLB")
    protected static Gslb createSimpleHttpGslb(LoadBalancer balancer) {
        Gslb gslb = Gslb.builder().frontend(createSimpleHttpFrontend(balancer).getFrontendName()).globalname("http-" + SIMPLE_GSLB_NAME).build();
        balancer.addGslbUseCache(gslb);
        return gslb;
    }

    @Step("Создание простого Route")
    protected static RouteSni.Route createSimpleRoute(LoadBalancer balancer) {
        Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName(SIMPLE_ROUTE_NAME).build();
        balancer.addBackendUseCache(backend);
        RouteSni.Route route = new RouteSni.Route(backend.getBackendName(), SIMPLE_ROUTE_NAME);
        Gslb gslb = createSimpleTcpGslb(balancer);
        balancer.addRouteUseCache(gslb.getGlobalname(), route);
        return route;
    }
}
