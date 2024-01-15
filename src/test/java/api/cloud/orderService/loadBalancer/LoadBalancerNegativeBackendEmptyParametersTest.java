package api.cloud.orderService.loadBalancer;

import api.Tests;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversHttp;
import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeBackendEmptyParametersTest extends Tests {

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
    @ParameterizedTest(name = "Создание Backend. Пустой balancing_algorithm {0}")
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
    @ParameterizedTest(name = "Создание Backend. Пустой check_fall {0}")
    void backendEmptyModeCheckFall(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_fall").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой check_rise {0}")
    void backendEmptyModeCheckRise(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_rise").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой check_interval {0}")
    void backendEmptyModeCheckInterval(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_interval").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой adv_check {0}")
    void backendEmptyAdvCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_adv_check").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой check_method {0}")
    void backendEmptyCheckMethod(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_method").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой check_uri {0}")
    void backendEmptyCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().backendName("backend_empty_check_uri").servers(serversTcp)
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
    @ParameterizedTest(name = "Создание Backend. Пустой server.name {0}")
    void backendEmptyServersName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().address("10.226.48.194").port(80).build());
        Backend backend = Backend.builder().backendName("empty_servers_name").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("name");
        }
    }
}
