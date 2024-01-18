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
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;


@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerPositiveNameTest extends Tests {
    private static final int MAX_FIELD_SIZE = 255;
    private static final int MAX_FIELD_SIZE_GSLB = 64;
    private static final int MIN_FIELD_SIZE = 1;
    private static final int MIN_FIELD_SIZE_GSLB = 3;
    private static final String MASK = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";
    private static final String MASK_GSLB = "abcdefghijklmnopqrastuvwxyz-1234567890";
    private static final String MASK_ROUTE = "abcdefghijklmnopqrastuvwxyz.1234567890.";
    private static final String MASK_ADVANCED = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = addTcpBackendMax(balancer,true);
            Frontend frontend = addTcpFrontendMax(balancer, backend.getBackendName(),true);
            Gslb gslb = addTcpGslbMax(balancer, frontend,true);
            addTcpRouteMax(backend.getBackendName(), gslb.getGlobalname(), true);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка минимальной длины наименований полей у заказов {0}")
    void validFieldMinName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = addTcpBackendMax(balancer,false);
            Frontend frontend = addTcpFrontendMax(balancer, backend.getBackendName(),false);
            Gslb gslb = addTcpGslbMax(balancer, frontend,false);
            addTcpRouteMax(backend.getBackendName(), gslb.getGlobalname(), false);
        }
    }

    @Step("Создание tcp Backend")
    public static Backend addTcpBackendMax(LoadBalancer balancer, boolean max) {
        String pattern = (max ? MASK_ADVANCED : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK_ADVANCED.length() : MIN_FIELD_SIZE)).random();
        String backendName = (max ? MASK : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK.length() : MIN_FIELD_SIZE)).random();
        Backend backend = Backend.builder()
                .servers(serversTcp)
                .backendName(backendName)
//                .checkPort(new Random().nextInt(35678) + 10000)
                .advCheck("tcp-check")
                .checkFall(3)
//                .checkSsl("disabled")
//                .match("string")
                .checkRise(3)
                .checkInterval(5000)
                .cookieStatus(false)
//                .pattern(pattern)
//                .data(pattern)
                .build();
        balancer.addBackend(backend);
        return backend;
    }

    @Step("Создание tcp Frontend c tcp Backend")
    public static Frontend addTcpFrontendMax(LoadBalancer balancer, String backendName, boolean max) {
        String frontendName = (max ? MASK : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK.length() : MIN_FIELD_SIZE)).random();
        Frontend frontend =  Frontend.builder()
                .frontendName(frontendName)
                .defaultBackendNameTcp(backendName)
                .mode("tcp")
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    @Step("Создание Gslb c Frontend")
    public static Gslb addTcpGslbMax(LoadBalancer balancer, Frontend frontend, boolean max) {
        String globalName = (max ? MASK_GSLB : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE_GSLB - MASK_GSLB.length() : MIN_FIELD_SIZE_GSLB)).random();
        Gslb gslb =  Gslb.builder()
                .globalname(globalName)
                .frontend(frontend.getFrontendName())
                .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    public static RouteSni addTcpRouteMax(String backendName, String globalName, boolean max) {
        String name = (max ? MASK_ROUTE : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK_ROUTE.length() : MIN_FIELD_SIZE)).random();
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, name));
        return RouteSni.builder().routes(routes).globalname(globalName).build();
    }
}
