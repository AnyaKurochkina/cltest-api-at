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

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendNegativeTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный mode {0}")
    void notValidBackendMode(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().mode("not_valid").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("mode");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный backendName {0}")
    void notValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().backendName("not_valid_backend_name=").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("backend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_uri {0}")
    void notValidBackendCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().checkUri("0").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("check_uri");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный balancing_algorithm {0}")
    void notValidBackendBalancingAlgorithm(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().balancingAlgorithm("not_valid").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("balancing_algorithm");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный httpReuse {0}")
    void notValidBackendHttpReuse(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().httpReuse("not_valid").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный adv_check {0}")
    void notValidBackendAdvCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().advCheck("not_valid").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("adv_check");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный check_fall {0}")
    void notValidBackendCheckFall(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().checkFall(101).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).responseContains("check_fall").status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный stringVersion {0}")
    void notValidBackendStringVersion(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().stringVersion("0").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).responseContains("string_version").status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный match {0}")
    void notValidBackendMatch(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().stringMatch("0").build();
            AssertResponse.run(() -> balancer.addBackend(backend)).responseContains("string_match").status(422);
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный string_value {0}")
    void notValidBackendStringValue(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().stringValue(new Generex("[a-z]{256}").random()).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("string_value");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный address {0}")
    void notValidBackendServerAddress(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.260").port(80).build());
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.address");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный port {0}")
    void notValidBackendServerPort(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("name").address("10.226.48.10").port(0).build());
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.port");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный name {0}")
    void notValidBackendServerName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("").address("10.226.48.10").port(0).build());
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().servers(servers).build();
            AssertResponse.run(() -> balancer.addBackend(backend)).status(422).responseContains("servers.0.name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Backend. Невалидный stringHostHdr {0}")
    void notValidBackendStringHostHdr(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleHttpBackendWidthHttpCheck().stringHostHdr(new Generex("[a-z]{256}").random()).build();
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
