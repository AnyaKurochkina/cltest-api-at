package api.cloud.orderService.loadBalancer;

import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeBackendEmptyParametersTest extends AbstractLoadBalancerTest {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой mode {0}")
    void backendEmptyMode(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().mode(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("mode");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой balancing_algorithm {0}")
    void backendEmptyBalancingAlgorithm(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().balancingAlgorithm(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("balancing_algorithm");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers {0}")
    void backendEmptyModeServers(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().servers(new ArrayList<>()).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой backend_name {0}")
    void backendEmptyModeBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("backend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой check_fall {0}")
    void backendEmptyModeCheckFall(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().checkFall(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой check_rise {0}")
    void backendEmptyModeCheckRise(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().checkRise(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой check_interval {0}")
    void backendEmptyModeCheckInterval(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().checkInterval(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой adv_check {0}")
    void backendEmptyAdvCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().advCheck(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой http_reuse {0}")
    void backendEmptyCheckMethod(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().httpReuse(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой check_uri {0}")
    void backendEmptyCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().checkUri(null).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers.port {0}")
    void backendEmptyServersPort(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.simpleTcpServer().port(null).build());
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("port");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой servers.address {0}")
    void backendEmptyServersAddress(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.simpleTcpServer().address(null).build());
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("address");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Пустой server.name {0}")
    void backendEmptyServersName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.simpleTcpServer().name(null).build());
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("name");
        }
    }
}
