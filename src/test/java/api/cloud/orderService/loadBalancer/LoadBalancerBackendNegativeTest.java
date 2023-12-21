package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.Server;
import models.cloud.subModels.loadBalancer.StandbyMode;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversHttp;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendNegativeTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный mode {0}")
    void notValidBackendMode(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).mode("not_valid").backendName("not_valid_backend_mode").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("mode");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный backendName {0}")
    void notValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_name=").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("backend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_uri {0}")
    void notValidBackendCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_httpchk_check_uri")
                    .advCheck("httpchk")
                    .checkUri("0")
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("check_uri");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный balancing_algorithm {0}")
    void notValidBackendBalancingAlgorithm(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_balancing_algorithm")
                    .balancingAlgorithm("not_valid").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("balancing_algorithm");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_port {0}")
    void notValidBackendCheckPort(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_port")
                    .advCheck("tcp-check")
                    .checkPort(65535)
                    .checkSsl("disabled")
                    .match("string")
                    .checkFall(3)
                    .checkRise(3)
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный adv_check {0}")
    void notValidBackendAdvCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_adv_check")
                    .advCheck("not_valid")
                    .checkPort(65533)
                    .checkSsl("disabled")
                    .match("string")
                    .checkFall(3)
                    .checkRise(3)
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("adv_check");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_fall {0}")
    void notValidBackendCheckFall(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_fall")
                    .advCheck("tcp-check")
                    .checkPort(65532)
                    .checkSsl("disabled")
                    .match("string")
                    .checkFall(101)
                    .checkRise(3)
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_ssl {0}")
    void notValidBackendCheckSsl(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_ssl")
                    .advCheck("tcp-check")
                    .checkPort(65532)
                    .checkSsl("not_valid")
                    .match("string")
                    .checkFall(5)
                    .checkRise(3)
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный match {0}")
    void notValidBackendMatch(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_match")
                    .advCheck("tcp-check")
                    .checkPort(65532)
                    .checkSsl("disabled")
                    .match("not_valid")
                    .checkFall(5)
                    .checkRise(3)
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_method {0}")
    void notValidBackendCheckMethod(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_method")
                    .advCheck("tcp-check")
                    .checkPort(65532)
                    .checkSsl("disabled")
                    .checkMethod("not_valid")
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("check_method");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный version_and_headers {0}")
    void notValidBackendVersionAndHeaders(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_version_and_headers")
                    .mode("http")
                    .advCheck("httpchk")
                    .checkPort(65531)
                    .checkSsl("disabled")
                    .checkMethod("GET")
                    .checkFall(5)
                    .checkRise(3)
                    .checkInterval(5000)
                    .checkUri("/")
                    .versionAndHeaders("not_valid")
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("version_and_headers");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный address {0}")
    void notValidBackendServerAddress(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.260").port(80).build());
            Backend backend = Backend.builder().backendName("not_valid_servers_address").servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.address");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный port {0}")
    void notValidBackendServerPort(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.10").port(0).build());
            Backend backend = Backend.builder().backendName("not_valid_servers_port").cookieStatus(false).servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.port");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный name {0}")
    void notValidBackendServerName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("").address("10.226.48.10").port(0).build());
            Backend backend = Backend.builder().backendName("not_valid_servers_name").servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный pattern {0}")
    void notValidBackendPattern(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_pattern")
                    .advCheck("tcp-check")
                    .checkPort(65532)
                    .checkSsl("disabled")
                    .checkFall(5)
                    .checkRise(3)
                    .pattern(new Generex("[a-z]{256}").random())
                    .checkInterval(5000)
                    .build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перевести сервера бэкенда в Active/StandBy режим. Пустой backend_name {0}")
    void changeActiveStandbyModeNotValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            StandbyMode standbyMode = StandbyMode.builder().backendName(LoadBalancerBackendChangeNegativeTest.backend.getBackendName()).build();
            StandbyMode standbyMode = StandbyMode.builder().serverNames(Collections.singletonList("10.0.0.9")).build();
            AssertResponse.run(() -> balancer.changeActiveStandbyMode(standbyMode)).status(422).responseContains("backend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перевести сервера бэкенда в Active/StandBy режим. Невалидный state {0}")
    void changeActiveStandbyModeNotValidState(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
            StandbyMode standbyMode = StandbyMode.builder().backendName(LoadBalancerBackendChangeNegativeTest.backend.getBackendName()).
                    serverNames(Collections.singletonList("10.0.0.9")).state("not_valid").build();
            AssertResponse.run(() -> balancer.changeActiveStandbyMode(standbyMode)).status(422).responseContains("state");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать GSLB. Невалидный globalname {0}")
    void changeActiveStandbyModeNotValidGlobalname(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
            balancer.addFrontendUseCache(LoadBalancerBackendChangeNegativeTest.frontend);
            Gslb gslb = Gslb.builder().frontend(LoadBalancerBackendChangeNegativeTest.frontend.getFrontendName()).globalname(new Generex("[a-z]{65}").random()).build();
            AssertResponse.run(() -> balancer.addGslb(gslb)).status(422).responseContains("globalname");
        }
    }
}
