package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.*;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.*;


@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerPositiveTest extends Tests {
    private static final int MAX_FIELD_SIZE = 255;
    private static final int MAX_FIELD_SIZE_GSLB = 64;
    private static final int MIN_FIELD_SIZE = 1;
    private static final int MIN_FIELD_SIZE_GSLB = 3;
    private static final String MASK = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";
    private static final String MASK_GSLB = "abcdefghijklmnopqrastuvwxyz-1234567890";
    private static final String MASK_ROUTE = "abcdefghijklmnopqrastuvwxyz.1234567890.";
    private static final String MASK_ADVANCED = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";

    static List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
           .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/8e61b88a-1411-4df7-862e-a4e6d4baf05a/main?context=proj-114wetem0c&type=project&org=vtb");

    @TmsLink("")
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
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), MASK_ROUTE + new Generex(String.format("[a-z0-9]{%s}", MAX_FIELD_SIZE - MASK_ROUTE.length())).random());
            balancer.addRouteSni(route);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка минимальной длины наименований полей у заказов {0}")
    void validFieldMinName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random(), false);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random());
            Gslb gslb = createGslb(frontend, new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE_GSLB)).random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE)).random());
            balancer.addRouteSni(route);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка удаления маршрута sni {0}")
    void deleteRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(), false);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("[a-zA-Z0-9]{20}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("[a-z0-9]{20}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{20}").random());
            balancer.addRouteSni(route);
            balancer.deleteRouteSni(route, getRouteByDelete(balancer, route));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка изменения бекэнда на новый бекэнд в маршруте sni {0}")
    void editRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend1 = createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(),false);
            balancer.addBackend(backend1);
            Backend backend2 = createBackendTcp(new Generex("[a-zA-Z0-9]{20}").random(),false);
            balancer.addBackend(backend2);
            Frontend frontend = createFrontendTcp(backend1.getBackendName(), new Generex("[a-zA-Z0-9]{20}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("[a-z0-9]{20}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend1.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{20}").random());
            balancer.addRouteSni(route);
            balancer.editRouteSni(route, backend2.getBackendName());
        }
    }

    @Step("Создание tcp Backend")
    public static Backend createBackendTcp(String backendName, boolean max) {
        String pattern = (max ? MASK_ADVANCED : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK_ADVANCED.length() : MIN_FIELD_SIZE)).random();
        return Backend.builder()
                .servers(serversTcp)
                .backendName(backendName)
                .advancedCheck(true)
                .checkPort(new Random().nextInt(35678) + 10000)
                .advCheck("tcp-check")
                .checkFall(3)
                .checkSsl("disabled")
                .match("string")
                .checkRise(3)
                .checkInterval(5000)
                .pattern(pattern)
                .data(pattern)
                .build();
    }

    @Step("Создание tcp Frontend c tcp Backend")
    public static Frontend createFrontendTcp(String backendName, String frontendName) {
        return Frontend.builder()
                .frontendName(frontendName)
                .defaultBackendNameTcp(backendName)
                .mode("tcp")
                .build();
    }

    @Step("Создание Gslb c Frontend")
    public static Gslb createGslb(Frontend frontend, String globalName) {
        return Gslb.builder()
                .globalname(globalName)
                .frontend(frontend.getFrontendName())
                .build();
    }

    @Step("Создание Route c Gslb и Backend")
    public static RouteSni createRoute(LoadBalancer balancer, String backendName, String globalName, String name) {
        List<RouteSni.DnsPrefix> dnsPrefixes = OrderServiceSteps.getProductsField(balancer, "data.find{it.type=='cluster'}.data.config.polaris_config", List.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, name));
        RouteSni route = null;
        for(int i = 0; i < dnsPrefixes.size(); i++) {
            if(((Map<?, ?>)dnsPrefixes.get(i)).get("globalname").toString().contains(globalName)) {
                route = RouteSni.builder()
                        .dnsPrefix(new RouteSni.DnsPrefix((Map<?, ?>)dnsPrefixes.get(i)))
                        .routes(routes)
                        .build();
                break;
            }
        }
        return route;
    }

    @Step("Получение Route в формате для удаления")
    public static RouteSni.RouteCheck getRouteByDelete(LoadBalancer balancer, RouteSni routeSni) {
        return  (RouteSni.RouteCheck) OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
    }


    // так не работает: в цикле ошибка java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to models.cloud.subModels.loadBalancer.RouteSni$DnsPrefix
    @Step("Создание Route c Gslb и Backend")
    private RouteSni createRouteNotValid(LoadBalancer balancer, String backendName, String globalName, String name) {
        List<RouteSni.DnsPrefix> dnsPrefixes = OrderServiceSteps.getProductsField(balancer, "data.find{it.type=='cluster'}.data.config.polaris_config", List.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, name));
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
