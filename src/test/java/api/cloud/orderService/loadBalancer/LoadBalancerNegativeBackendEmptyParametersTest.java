package api.cloud.orderService.loadBalancer;

import api.Tests;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeBackendEmptyParametersTest extends Tests {

    List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/37c93f8e-c2ee-40cb-a5d2-008524676f3f/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой mode {0}")
    void backendEmptyMode(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("backend_empty_mode").mode(null).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("mode");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой balancingAlgorithm {0}")
    void backendEmptyBalancingAlgorithm(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("backend_empty_balancing_algorithm").balancingAlgorithm(null).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("balancing_algorithm");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @Disabled
    @ParameterizedTest(name = "Создание Backend. Пустой servers {0}")
    void backendEmptyModeServers(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_servers").servers(serversTcp).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой backend_name {0}")
    void backendEmptyModeBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName(null).servers(serversTcp).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("backend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой checkFall {0}")
    void backendEmptyModeCheckFall(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_fall").servers(serversTcp)
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(1000)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой checkRise {0}")
    void backendEmptyModeCheckRise(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_rise").servers(serversTcp)
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(1000)
                .checkFall(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой checkInterval {0}")
    void backendEmptyModeCheckInterval(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_interval").servers(serversTcp)
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(1000)
                .checkFall(5)
                .checkRise(5)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой advCheck {0}")
    void backendEmptyAdvCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_adv_check").servers(serversTcp)
                .advancedCheck(true)
                .checkPort(1000)
                .checkInterval(300)
                .checkFall(7)
                .checkRise(7)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой checkMethod {0}")
    void backendEmptyCheckMethod(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_method").servers(serversTcp)
                .advancedCheck(true)
                .checkInterval(300)
                .checkFall(7)
                .checkRise(7)
                .advCheck("httpchk")
                .checkUri("/")
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой checkUri {0}")
    void backendEmptyCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_uri").servers(serversTcp)
                .advancedCheck(true)
                .checkInterval(300)
                .checkFall(7)
                .checkRise(7)
                .advCheck("httpchk")
                .checkMethod("PUT")
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers.port {0}")
    void backendEmptyServersPort(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().address("10.226.48.194").name("name").build());
        Backend backend = Backend.builder().backendName("empty_servers_port").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("port");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers.address {0}")
    void backendEmptyServersAddress(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().name("name").port(80).build());
        Backend backend = Backend.builder().backendName("empty_servers_address").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("address");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers.address {0}")
    void backendEmptyServersName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().address("10.226.48.194").port(80).build());
        Backend backend = Backend.builder().backendName("empty_servers_name").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("name");
        }
    }
}
