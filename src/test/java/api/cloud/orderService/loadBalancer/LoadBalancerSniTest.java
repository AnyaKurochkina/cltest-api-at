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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;
import static api.cloud.orderService.loadBalancer.LoadBalancerPositiveNameTest.balancer;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerSniTest extends Tests {
//    @Mock
//    static LoadBalancer balancer = LoadBalancer.builder().build()
//            .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/df4bcb7c-6139-45dd-b6de-81c5633bfa95/main?context=proj-2xdbtyzqs3&type=project&org=vtb");
//             // .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/d0f1264e-fd90-4495-bd3d-d5dd2871f558/frontends?context=proj-2xdbtyzqs3&type=project&org=vtb");

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание маршрута sni {0}")
    void createRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addTcpRoute(balancer);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление маршрута sni {0}")
    void deleteRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            balancer.deleteRouteSni(route);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение маршрута sni {0}")
    void editRouteSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            RouteSni route = addTcpRoute(balancer);
            String backendName = addTcpGslb(balancer);
            balancer.editRouteSni(route, backendName);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить псевдоним маршрута sni {0}")
    void createAliasesSni(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            RouteSni route = getExistRouteSni(balancer,"snitcp3470502953.gslb-tcp-2257439140.oslb-synt01.test.vtb.ru");
//            addAliases(balancer, route);
//            OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.aliases.contains('%s')}", "aliassnitcp1008398558"), RouteSni.RouteCheck.class);
            addAliases(balancer, addTcpRoute(balancer));
        }
    }

//    @TmsLink("")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Проверка sni маршрута {0}")
//    void check(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            //RouteSni route = getExistRouteSni(balancer,"snitcp0307054963.gslb-tcp-2483251909.oslb-synt01.test.vtb.ru");
//            //balancer.deleteRouteSni(route);
//        }
//    }

    private void addAliases(LoadBalancer balancer, RouteSni route) {
        balancer.addAliases(route, Arrays.asList(RouteSni.Alias.builder().name(new Generex("aliassnitcp[0-9]{10}").random()).build(), RouteSni.Alias.builder().name(new Generex("aliassnitcp[0-9]{10}").random()).build()));
    }

    static RouteSni addRoute(String backendName, String globalName, boolean valid) {
        String globalNameFull = OrderServiceSteps.getProductsField(balancer, String.format("data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}.globalname", "gslb-tcp-2020322555"), String.class);
        List<RouteSni.Route> routes = Collections.singletonList(new RouteSni.Route(backendName, new Generex(valid ? "snitcp[0-9]{10}" : "[a-z0-9]{256}").random()));
        return RouteSni.builder().routes(routes).globalname(globalNameFull).build();
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

    static String addTcpGslb(LoadBalancer balancer) {
        Frontend frontend = addTcpFrontend(balancer);
        Gslb gslb = Gslb.builder()
                .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                .frontend(frontend.getFrontendName())
                .build();
        balancer.addGslb(gslb);
        return frontend.getDefaultBackendNameTcp();
    }
    static Gslb addTcpGslb(LoadBalancer balancer, Frontend frontend) {
        Gslb gslb = Gslb.builder()
                .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                .frontend(frontend.getFrontendName())
                .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    static RouteSni addTcpRoute(LoadBalancer balancer) {
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
        Gslb gslb = Gslb.builder()
                .globalname(new Generex("gslb-tcp-[0-9]{10}").random())
                .frontend(frontend.getFrontendName())
                .build();
        balancer.addGslb(gslb);
        RouteSni route = addRoute(backend.getBackendName(), gslb.getGlobalname(),true);
        balancer.addRouteSni(route);
        return route;
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
        return  Gslb.builder().globalname(globalName).frontend(frontend.getFrontendName()).build();
    }

    @Step("Получение существующего route sni по route_name")
    public static RouteSni getExistRouteSni(LoadBalancer balancer, String routeName) {
        RouteSni.RouteCheck route = OrderServiceSteps.getObjectClass(balancer, String.format("data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}", routeName), RouteSni.RouteCheck.class);
        String[] names = route.getRouteName().split("[.]", 2);
        return RouteSni.builder().globalname(names[1]).routes(Collections.singletonList(new RouteSni.Route(route.getBackendName(),names[0]))).build();
    }

}
