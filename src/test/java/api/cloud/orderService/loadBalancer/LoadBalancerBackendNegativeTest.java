package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendNegativeTest extends Tests {

    List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/37c93f8e-c2ee-40cb-a5d2-008524676f3f/main?context=proj-ln4zg69jek&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный mode {0}")
    void notValidBackendMode(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversHttp).mode("not_valid").backendName("not_valid_backend_mode").build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("mode");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный backendName {0}")
    void notValidBackendName(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_name=").build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("backend_name");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный checkUri {0}")
    void notValidBackendCheckUri(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_httpchk_check_uri")
                .advancedCheck(true)
                .advCheck("httpchk")
                .checkUri("0")
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("check_uri");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный balancingAlgorithm {0}")
    void notValidBackendBalancingAlgorithm(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_balancing_algorithm")
                .balancingAlgorithm("not_valid").build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("balancing_algorithm");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный checkPort {0}")
    void notValidBackendCheckPort(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_port")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65535)
                .checkSsl("disabled")
                .match("string")
                .checkFall(3)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный advCheck {0}")
    void notValidBackendAdvCheck(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_adv_check")
                .advancedCheck(true)
                .advCheck("not_valid")
                .checkPort(65533)
                .checkSsl("disabled")
                .match("string")
                .checkFall(3)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("adv_check");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный checkFall {0}")
    void notValidBackendCheckFall(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_fall")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65532)
                .checkSsl("disabled")
                .match("string")
                .checkFall(101)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный checkSsl {0}")
    void notValidBackendCheckSsl(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_ssl")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65532)
                .checkSsl("not_valid")
                .match("string")
                .checkFall(5)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный match {0}")
    void notValidBackendMatch(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_match")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65532)
                .checkSsl("disabled")
                .match("not_valid")
                .checkFall(5)
                .checkRise(3)
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный checkMethod {0}")
    void notValidBackendCheckMethod(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_check_method")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65532)
                .checkSsl("disabled")
                .checkMethod("not_valid")
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("check_method");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный VersionAndHeaders {0}")
    void notValidBackendVersionAndHeaders(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_version_and_headers")
                .advancedCheck(true)
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
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный address {0}")
    void notValidBackendServerAddress(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.260").port(80).build());
        Backend backend = Backend.builder().backendName("not_valid_servers_address").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.address");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный port {0}")
    void notValidBackendServerPort(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.10").port(0).build());
        Backend backend = Backend.builder().backendName("not_valid_servers_port").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.port");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный name {0}")
    void notValidBackendServerName(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        List<Server> servers = Collections.singletonList(Server.builder().name("").address("10.226.48.10").port(0).build());
        Backend backend = Backend.builder().backendName("not_valid_servers_name").servers(servers).build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.name");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание Backend. Невалидный pattern {0}")
    void notValidBackendPattern(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_pattern")
                .advancedCheck(true)
                .advCheck("tcp-check")
                .checkPort(65532)
                .checkSsl("disabled")
                .checkFall(5)
                .checkRise(3)
                .pattern(new Generex("[a-z]{256}").random())
                .checkInterval(5000)
                .build();
        AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
//        }
    }
}
