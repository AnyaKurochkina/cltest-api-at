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
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;


@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoadBalancerPositiveTest extends Tests {
    private static final int MAX_FIELD_SIZE = 255;
    private static final int MAX_FIELD_SIZE_GSLB = 64;
    private static final int MIN_FIELD_SIZE = 1;
    private static final int MIN_FIELD_SIZE_GSLB = 3;
    private static final String MASK = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";
    private static final String MASK_GSLB = "abcdefghijklmnopqrastuvwxyz-1234567890";
    private static final String MASK_ROUTE = "abcdefghijklmnopqrastuvwxyz.1234567890.";
    private static final String MASK_ADVANCED = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~';";

    @TmsLink("")
    @Order(1)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(MASK + new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE - MASK.length())).random(), true);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), MASK + new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE - MASK.length())).random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, MASK_GSLB + new Generex(String.format("[a-z0-9]{%s}", MAX_FIELD_SIZE_GSLB - MASK_GSLB.length())).random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend, gslb.getGlobalname(), MASK_ROUTE + new Generex(String.format("[a-z0-9]{%s}", MAX_FIELD_SIZE - MASK_ROUTE.length())).random());
            balancer.addRouteSni(route);
        }
    }

    @TmsLink("")
    @Order(2)
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка минимальной длины наименований полей у заказов {0}")
    void validFieldMinName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random(), false);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random());
            Gslb gslb = createGslb(frontend, new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE_GSLB)).random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend, gslb.getGlobalname(), new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE)).random());
            balancer.addRouteSni(route);
        }
    }

    @Step("Создание tcp Backend")
    private Backend createBackendTcp(String backendName, boolean max) {
        return Backend.builder()
                .servers(serversTcp)
                .backendName(backendName)
                .advancedCheck(true)
                .checkPort(32344)
                .advCheck("tcp-check")
                .checkFall(3)
                .checkSsl("disabled")
                .match("string")
                .checkRise(3)
                .checkInterval(5000)
                .pattern(new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE : MIN_FIELD_SIZE)).random())
                .data(new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE : MIN_FIELD_SIZE)).random())
                .build();
    }

    @Step("Создание tcp Frontend c tcp Backend")
    private Frontend createFrontendTcp(String backendName, String frontendName) {
        return Frontend.builder()
                .frontendName(frontendName)
                .defaultBackendNameTcp(backendName)
                .mode("tcp")
                .build();
    }

    @Step("Создание Gslb c Frontend")
    private Gslb createGslb(Frontend frontend, String globalName) {
        return Gslb.builder()
                .globalname(globalName)
                .frontend(frontend.getFrontendName())
                .build();
    }


    @Step("Создание Route c Gslb и Backend")
    private RouteSni createRoute(LoadBalancer balancer, Backend backend, String globalName, String name) {
        List<RouteSni.DnsPrefix> dnsPrefixes = OrderServiceSteps.getProductsField(balancer, "data.find{it.type=='cluster'}.data.config.polaris_config", List.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backend.getBackendName(), name));
        RouteSni route = null;
        for (RouteSni.DnsPrefix dnsPrefix : dnsPrefixes) {
            if (((Map<?, ?>) dnsPrefix).get("globalname").toString().contains(globalName)) {
                route = RouteSni.builder()
                        .dnsPrefix(new RouteSni.DnsPrefix((Map<?, ?>) dnsPrefix))
                        .routes(routes)
                        .build();
                break;
            }
        }
        return route;
    }


}
