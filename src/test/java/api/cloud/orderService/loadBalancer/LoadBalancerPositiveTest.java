package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.*;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoadBalancerPositiveTest extends Tests {

    List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/a75efab9-0452-4609-a110-0812cce44d8c/main?context=proj-ln4zg69jek&type=project&org=vtb");
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
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Проверка максимальной длины наименований полей у заказов {0}")
    void validFieldMaxName(LoadBalancer product) {
        Backend backend = createBackendTcpMaxName();
        Frontend frontend = createFrontendTcpMaxName(backend.getBackendName());
        Gslb gslb = createGslbMaxName(frontend);
        createRouteMaxName(backend, gslb.getGlobalname());
    }

    @TmsLink("")
    @Order(2)
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Проверка минимальной длины наименований полей у заказов {0}")
    void validFieldMinName(LoadBalancer product) {
        Backend backend = createBackendTcpMinName();
        Frontend frontend = createFrontendTcpMinName(backend.getBackendName());
        Gslb gslb = createGslbMinName(frontend);
        createRouteMinName(backend, gslb.getGlobalname());
    }



    @Step("Создание tcp Backend с максимальным значением длины наименования")
    private Backend createBackendTcpMaxName() {
        Backend backend = Backend.builder()
                .servers(serversTcp)
                .backendName(MASK + new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE - MASK.length())).random())
                .advancedCheck(true)
                .pattern(new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE)).random())
                .data(new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE)).random())
                .build();
        balancer.addBackend(backend);
        return backend;
    }
    @Step("Создание tcp Backend с минимальным значением длины наименования")
    private Backend createBackendTcpMinName() {
        Backend backend = Backend.builder()
                .servers(serversTcp)
                .backendName(new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random())
                .advancedCheck(true)
                .data("a")
                .pattern("b")
                .build();
        balancer.addBackend(backend);
        return backend;
    }

    @Step("Создание tcp Frontend c tcp Backend с максимальными значениями длин наименований")
    private Frontend createFrontendTcpMaxName(String backendName) {
        Frontend frontend = Frontend.builder()
                .frontendName(MASK + new Generex(String.format("[a-zA-Z0-9]{%s}", MAX_FIELD_SIZE - MASK.length())).random())
                .defaultBackendNameTcp(backendName)
                .mode("tcp")
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    @Step("Создание tcp Frontend c tcp Backend с минимальными значениями длин наименований")
    private Frontend createFrontendTcpMinName(String backendName) {
        Frontend frontend = Frontend.builder()
                .frontendName(new Generex(String.format("[a-zA-Z]{%s}", MIN_FIELD_SIZE)).random())
                .defaultBackendNameTcp(backendName)
                .mode("tcp")
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    @Step("Создание Gslb c Backend с максимальными значениями длин наименования")
    Gslb createGslbMaxName(Frontend frontend) {
        Gslb gslb = Gslb.builder()
                .globalname(MASK_GSLB + new Generex(String.format("[a-z0-9]{%s}", MAX_FIELD_SIZE_GSLB - MASK_GSLB.length())).random())
                .frontend(frontend)
                .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    @Step("Создание Gslb c Backend с минимальными значениями длин наименования")
    Gslb createGslbMinName(Frontend frontend) {
        Gslb gslb = Gslb.builder()
                .globalname(new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE_GSLB)).random())
                .frontend(frontend)
                .build();
        balancer.addGslb(gslb);
        return gslb;
    }

    @Step("Создание Route c Gslb и Backend с максимальными значениями длин наименования")
    void createRouteMaxName(Backend backend, String globalName) {
        List<RouteSni.DnsPrefix> dnsPrefixes = (List<RouteSni.DnsPrefix>)OrderServiceSteps.getListObjectClass(balancer, "data.find{it.type=='cluster'}.data.config.polaris_config");
        List<RouteSni.Route> routes = Arrays.asList(new RouteSni.Route(backend.getBackendName(),MASK_ROUTE + new Generex(String.format("[a-z0-9]{%s}", MAX_FIELD_SIZE - MASK_ROUTE.length())).random()));//

        for(int i = 0; i < dnsPrefixes.size(); i++) {
            if(((Map)dnsPrefixes.get(i)).get("globalname").toString().contains(globalName)) {
                RouteSni route = RouteSni.builder()
                        .dnsPrefix(new RouteSni.DnsPrefix((Map)dnsPrefixes.get(i)))
                        .routes(routes)
                        .build();
                balancer.addRouteSni(route);
                break;
            }
        }
    }

    @Step("Создание Route c Gslb и Backend с минимальными значениями длин наименования")
    void createRouteMinName(Backend backend, String globalName) {
        List<RouteSni.DnsPrefix> dnsPrefixes = (List<RouteSni.DnsPrefix>)OrderServiceSteps.getListObjectClass(balancer, "data.find{it.type=='cluster'}.data.config.polaris_config");
        List<RouteSni.Route> routes = Arrays.asList(new RouteSni.Route(backend.getBackendName(),new Generex(String.format("[a-z0-9]{%s}", MIN_FIELD_SIZE)).random()));

        for(int i = 0; i < dnsPrefixes.size(); i++) {
            if(((Map)dnsPrefixes.get(i)).get("globalname").toString().contains(globalName)) {
                RouteSni route = RouteSni.builder()
                        .dnsPrefix(new RouteSni.DnsPrefix((Map)dnsPrefixes.get(i)))
                        .routes(routes)
                        .build();
                balancer.addRouteSni(route);
                break;
            }
        }
    }
}
