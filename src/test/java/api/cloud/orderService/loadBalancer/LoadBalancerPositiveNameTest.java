package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.*;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.*;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.*;

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
    static final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    static final String ROUTE_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}";

//    static List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
//            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
//    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
           .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/8e61b88a-1411-4df7-862e-a4e6d4baf05a/main?context=proj-114wetem0c&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = addTcpBackendMax(balancer,true);
            Frontend frontend = addTcpFrontendMax(balancer, backend.getBackendName(),true);
            Gslb gslb = addTcpGslbMax(balancer, frontend,true);
            addTcpRouteMax(balancer, gslb,true);
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
            addTcpRouteMax(balancer, gslb,false);
        }
    }

    @Step("Создание tcp Backend")
    public static Backend addTcpBackendMax(LoadBalancer balancer, boolean max) {
        String pattern = (max ? MASK_ADVANCED : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK_ADVANCED.length() : MIN_FIELD_SIZE)).random();
        String backendName = (max ? MASK : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK.length() : MIN_FIELD_SIZE)).random();
        Backend backend = Backend.builder()
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
                        .frontend(frontend)
                        .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    @Step("Создание Route c Gslb и Backend")
    public static RouteSni addTcpRouteMax(LoadBalancer balancer, Gslb gslb, boolean max) {
        String name = (max ? MASK_ROUTE : "") + new Generex(String.format("[a-zA-Z0-9]{%s}", max ? MAX_FIELD_SIZE - MASK_ROUTE.length() : MIN_FIELD_SIZE)).random();
        RouteSni.DnsPrefix dnsPrefixes =  OrderServiceSteps.getObjectClass(balancer, String.format(GSLIB_PATH, gslb.getGlobalname()), RouteSni.DnsPrefix.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(gslb.getFrontend().getDefaultBackendName(), name));
        return RouteSni.builder().routes(routes).dnsPrefix(dnsPrefixes).build();
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
}
