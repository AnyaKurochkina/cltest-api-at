package api.cloud.orderService.loadBalancer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.CheckString;
import models.cloud.subModels.loadBalancer.HealthCheck;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeActionsEmptyParametersTest extends Tests {

    @TmsLink("")
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проверки доступности. httpchk без check_uri {0}")
    void createHeathCheckWithoutCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackend(LoadBalancerBackendChangeNegativeTest.backend);
            HealthCheck healthCheck = HealthCheck.builder().backendName(LoadBalancerBackendChangeNegativeTest.backend.getBackendName())
                    .protocol("httpchk")
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringType("connect")
                            .stringAddress("10.0.0.1")
                            .stringPort(10)
                            .stringUseSsl("disabled")
                            .stringSendProxy("disabled")
                            .build()))
//                    .checkUri("/")
                    .build();
            balancer.createHealthCheck(healthCheck);
        }
    }

    @TmsLink("")
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проверки доступности. httpchk без check_method {0}")
    void createHeathCheckWithoutCheckMethod(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackend(LoadBalancerBackendChangeNegativeTest.backend);
            HealthCheck healthCheck = HealthCheck.builder().backendName(LoadBalancerBackendChangeNegativeTest.backend.getBackendName())
                    .protocol("httpchk")
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringType("connect")
                            .stringAddress("10.0.0.1")
                            .stringPort(10)
                            .stringUseSsl("disabled")
                            .stringSendProxy("disabled")
                            .build()))
//                    .checkMethod("GET")
                    .build();
            balancer.createHealthCheck(healthCheck);
        }
    }

    @TmsLink("")
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проверки доступности. checkStrings без check_strings.string_use_ssl {0}")
    void createHeathCheckWithoutCheckMethodStringUseSsl(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackend(LoadBalancerBackendChangeNegativeTest.backend);
            HealthCheck healthCheck = HealthCheck.builder().backendName(LoadBalancerBackendChangeNegativeTest.backend.getBackendName())
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringType("connect")
                            .stringAddress("10.0.0.1")
                            .stringPort(10)
//                            .stringUseSsl("disabled")
                            .stringSendProxy("disabled")
                            .build()))
                    .build();
            balancer.createHealthCheck(healthCheck);
        }
    }
}
