package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.*;
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
public class LoadBalancerRouteTest extends Tests {
    private static final int MAX_FIELD_SIZE = 255;
    private static final int MAX_FIELD_SIZE_GSLB = 64;
    private static final int MIN_FIELD_SIZE = 1;
    private static final int MIN_FIELD_SIZE_GSLB = 3;
    private static final String MASK = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";
    private static final String MASK_GSLB = "abcdefghijklmnopqrastuvwxyz-1234567890";
    private static final String MASK_ROUTE = "abcdefghijklmnopqrastuvwxyz.1234567890.";
    private static final String MASK_ADVANCED = "abcdefghijklmnopqrastuvwxyz.ABCDEFGHIJKLMNOPQRSTUVWXYZ-1234567890_";
    static final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    static final String ROUTE_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}";

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
    @ParameterizedTest(name = "Создание маршрута sni {0}")
    void createRouteSnit(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("createroutesni-backend[0-9]{10}").random(), false);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("createroutesni-frontend[0-9]{10}").random());
            Gslb gslb = createGslb(frontend, new Generex("createroutesni-gslb[0-9]{10}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("createroutesni[0-9]{10}").random());
            balancer.addRouteSni(route);
        }
    }

    @TmsLinks({@TmsLink("SOUL-7924"),@TmsLink("SOUL-6422")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаления маршрута sni {0}")
    void deleteRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("deleteroutesni-backend[0-9]{10}").random(), false);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("deleteroutesni-frontend[0-9]{10}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("deleteroutesni-gslb[0-9]{10}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("deleteroutesni[0-9]{10}").random());
            balancer.addRouteSni(route);
            balancer.deleteRouteSni(route, getRouteByDelete(balancer, route));
        }
    }

    @TmsLink("SOUL-7923")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение маршрута sni {0}")
    void editRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend1 = createBackendTcp(new Generex("editroutesni-backend1-[0-9]{10}").random(),false);
            balancer.addBackend(backend1);
            Backend backend2 = createBackendTcp(new Generex("editroutesni-backend2-[0-9]{10}").random(),false);
            balancer.addBackend(backend2);
            Frontend frontend1 = createFrontendTcp(backend1.getBackendName(), new Generex("editroutesni-frontend1-[0-9]{10}").random());
            balancer.addFrontend(frontend1);
            Frontend frontend2 = createFrontendTcp(backend2.getBackendName(), new Generex("editroutesni-frontend2-[0-9]{10}").random());
            balancer.addFrontend(frontend2);
            Gslb gslb1 = createGslb(frontend1, new Generex("editroutesni-gslb1-[0-9]{10}").random());
            balancer.addGslb(gslb1);
            Gslb gslb2 = createGslb(frontend2, new Generex("editroutesni-gslb2-[0-9]{10}").random());
            balancer.addGslb(gslb2);
            RouteSni route = createRoute(balancer, backend1.getBackendName(), gslb1.getGlobalname(), new Generex("editroutesni[0-9]{10}").random());
            balancer.addRouteSni(route);
            balancer.editRouteSni(route, backend2.getBackendName());
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с невалидными названием {0}")
    void notValidMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("createroutesni-notvalidnameroute-backend[0-9]{10}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("createroutesni-notvalidnameroute-frontend[0-9]{10}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("createroutesni-notvalidnameroute-gslb[0-9]{10}").random());
            balancer.addGslb(gslb);
            AssertResponse.run(() -> balancer.addRouteSni(createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{256}").random())));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменения бекэнда на несуществующий бекэнд в маршруте sni {0}")
    void editRouteNotExistBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("editroutesni-notexistbackend-backend[0-9]{10}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("editroutesni-notexistbackend-frontend[0-9]{10}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("editroutesni-notexistbackend-gslb[0-9]{10}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("editroutesninotexistbackend[0-9]{10}").random());
            balancer.addRouteSni(route);
            AssertResponse.run(() -> balancer.editRouteSni(route, new Generex("[a-z0-9]{20}").random()));
        }
    }

    @TmsLink("SOUL-7513")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni с ранее используемым бекэндом в другом маршруте {0}")
    void editRouteUsedBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend1 = createBackendTcp(new Generex("createroutesni-usedbackend-backend1-[0-9]{10}").random(),false);
            balancer.addBackend(backend1);
            Backend backend2 = createBackendTcp(new Generex("createroutesni-usedbackend-backend2-[0-9]{10}").random(),false);
            balancer.addBackend(backend2);
            Frontend frontend1 = createFrontendTcp(backend1.getBackendName(), new Generex("editroutesni-usedbackend-frontend1-[0-9]{10}").random());
            balancer.addFrontend(frontend1);
            Frontend frontend2 = createFrontendTcp(backend2.getBackendName(), new Generex("editroutesni-usedbackend-frontend2-[0-9]{10}").random());
            balancer.addFrontend(frontend2);
            Gslb gslb1 = createGslb(frontend1, new Generex("createroutesni-usedbackend-gslb1-[0-9]{10}").random());
            balancer.addGslb(gslb1);
            Gslb gslb2 = createGslb(frontend2, new Generex("createroutesni-usedbackend-gslb2-[0-9]{10}").random());
            balancer.addGslb(gslb2);
            RouteSni route1 = createRoute(balancer, backend1.getBackendName(), gslb1.getGlobalname(), new Generex("createroutesniusedbackend[0-9]{10}").random());
            balancer.addRouteSni(route1);
            RouteSni route2 = createRoute(balancer, backend1.getBackendName(), gslb2.getGlobalname(), new Generex("createroutesniusedbackend[0-9]{10}").random());
            AssertResponse.run(() -> balancer.addRouteSni(route2));
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаления маршрута sni по невалидному названию {0}")
    void removeRouteNotValidRouteName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createBackendTcp(new Generex("deleteroutesni-notvalidname-backend[0-9]{10}").random(), true);
            balancer.addBackend(backend);
            Frontend frontend = createFrontendTcp(backend.getBackendName(), new Generex("deleteroutesni-notvalidname-frontend[0-9]{10}").random());
            balancer.addFrontend(frontend);
            Gslb gslb = createGslb(frontend, new Generex("deleteroutesni-notvalidname-gslb[0-9]{10}").random());
            balancer.addGslb(gslb);
            RouteSni route = createRoute(balancer, backend.getBackendName(), gslb.getGlobalname(), new Generex("[a-z0-9]{20}").random());
            balancer.addRouteSni(route);
            AssertResponse.run(() -> balancer.deleteRouteSni(route, getRouteByDeleteNotValid(balancer, route)));
        }
    }

    @Step("Получение Route для удаления в невалидном формате")
    public static RouteSni.RouteCheck getRouteByDeleteNotValid(LoadBalancer balancer, RouteSni routeSni) {
        RouteSni.RouteCheck route = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
        route.setRouteName(new Generex("[a-z0-9]{20}").random());
        return route;
    }

//    @TmsLink("")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Изменения бекэнда на новый бекэнд в маршруте sni {0}")
//    void sdf(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            //Gslb gslb = getExistGslb(balancer,"editroutesni-gslb1-5832408251.oslb-synt01.test.vtb.ru");
//            RouteSni routeSni = getExistRouteSni(balancer,"editroutesni9879328206.editroutesni-gslb1-5832408251.oslb-synt01.test.vtb.ru");
//            //Backend backend = getExistBackend(balancer,"editroutesni-backend2-9701364270");
//            balancer.editRouteSni(routeSni, "editroutesni-backend2-9701364270");
//            int i = 0;
//        }
//    }

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
                .frontend(frontend)
                .build();
    }

    @Step("Создание Route c Gslb и Backend")
    public static RouteSni createRoute(LoadBalancer balancer, String backendName, String globalName, String name) {
        RouteSni.DnsPrefix dnsPrefixes =  OrderServiceSteps.getObjectClass(balancer, String.format(GSLIB_PATH, globalName), RouteSni.DnsPrefix.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, name));
        return RouteSni.builder().routes(routes).dnsPrefix(dnsPrefixes).build();
    }

    @Step("Получение Route в формате для удаления")
    public static RouteSni.RouteCheck getRouteByDelete(LoadBalancer balancer, RouteSni routeSni) {
        return OrderServiceSteps.getObjectClass(balancer, String.format(ROUTE_PATH, routeSni.getRoutes().get(0).getName()), RouteSni.RouteCheck.class);
    }

    @Step("Получение существующего backend по backend_name")
    public static Backend getExistBackend(LoadBalancer balancer, String backendName) {
        return OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.backends.find{it.backend_name.contains('%s')}", backendName), Backend.class);
    }

    @Step("Получение существующего frontend по frontend_name")
    public static Frontend getExistFrontend(LoadBalancer balancer, String frontendName) {
        return OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.frontends.find{it.frontend_name.contains('%s')}", frontendName), Frontend.class);
    }

    @Step("Получение существующего gslb по globalname")
    public static Gslb getExistGslb(LoadBalancer balancer, String globalName) {
        String backendName = OrderServiceSteps.getProductsField(balancer, String.format("data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}.monitor_params.backend_name", globalName), String.class);
        Frontend frontend = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.frontends.find{it.default_backend_name.contains('%s')}", backendName), Frontend.class);
        return  Gslb.builder().globalname(globalName).frontend(frontend).build();
    }

    @Step("Получение существующего route sni по route_name")
    public static RouteSni getExistRouteSni(LoadBalancer balancer, String routeName) {
        RouteSni.DnsPrefix dnsPrefix = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}", routeName), RouteSni.DnsPrefix.class);
        RouteSni.RouteCheck route = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeName), RouteSni.RouteCheck.class);
        return  RouteSni.builder().dnsPrefix(dnsPrefix).routes(Collections.singletonList(new RouteSni.Route(route.getBackendName(),route.getRouteName()))).build();
    }
}
