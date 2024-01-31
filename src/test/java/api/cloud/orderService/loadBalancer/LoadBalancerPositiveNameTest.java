package api.cloud.orderService.loadBalancer;

import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.RouteSni;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;


@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerPositiveNameTest extends AbstractLoadBalancerTest {
    private final static String MAX_BACKEND_NAME = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_" + new Generex("[a-zA-Z0-9]{189}").random();
    private final static String MAX_FRONTEND_NAME = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_" + new Generex("[a-zA-Z0-9]{189}").random();
    private final static String MAX_GSLB_NAME = "abcdefghijklmnopqrastuvwxyz-1234567890" + new Generex("[a-z]{26}").random();
    private final static String MAX_ROUTE_NAME = "abcdefghijklmnopqrastuvwxyz.1234567890." + new Generex("[a-z]{216}").random();

    private final static String MIN_BACKEND_NAME = new Generex("[a-zA-Z0-9]{1}").random();
    private final static String MIN_FRONTEND_NAME = new Generex("[a-zA-Z0-9]{1}").random();
    private final static String MIN_GSLB_NAME = new Generex("[a-z]{3}").random();
    private final static String MIN_ROUTE_NAME = new Generex("[a-z]{1}").random();

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName(MAX_BACKEND_NAME).build();
            balancer.addBackend(backend);
            Frontend frontend = Frontend.simpleTcpFrontend(backend.getBackendName()).frontendName((MAX_FRONTEND_NAME)).build();
            balancer.addFrontend(frontend);
            Gslb gslb = Gslb.builder().frontend(frontend.getFrontendName()).globalname(MAX_GSLB_NAME).build();
            balancer.addGslb(gslb);
            RouteSni.Route route = new RouteSni.Route(backend.getBackendName(), MAX_ROUTE_NAME);
            balancer.addRoute(gslb.getGlobalname(), route);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка минимальной длины наименований полей у заказов {0}")
    void validFieldMinName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName(MIN_BACKEND_NAME).build();
            balancer.addBackend(backend);
            Frontend frontend = Frontend.simpleTcpFrontend(backend.getBackendName()).frontendName((MIN_FRONTEND_NAME)).build();
            balancer.addFrontend(frontend);
            Gslb gslb = Gslb.builder().frontend(frontend.getFrontendName()).globalname(MIN_GSLB_NAME).build();
            balancer.addGslb(gslb);
            RouteSni.Route route = new RouteSni.Route(backend.getBackendName(), MIN_ROUTE_NAME);
            balancer.addRoute(gslb.getGlobalname(), route);
        }
    }
}
