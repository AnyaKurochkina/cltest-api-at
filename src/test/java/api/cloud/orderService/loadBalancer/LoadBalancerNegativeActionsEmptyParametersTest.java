package api.cloud.orderService.loadBalancer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.CheckString;
import models.cloud.subModels.loadBalancer.HealthCheck;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;

@Execution(ExecutionMode.SAME_THREAD)
@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerNegativeActionsEmptyParametersTest extends Tests {

    @Mock
    static LoadBalancer loadBalancer = LoadBalancer.builder().platform("OpenStack").env("DEV").segment("dev-srv-app").build()
            .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/b41c8f54-354a-475e-90be-2a44188c9e8b/main?context=proj-iv550odo9a&type=project&org=vtb");


    @TmsLink("")
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проверки доступности. httpchk без check_uri {0}")
    void createHeathCheckWithoutCheckUri(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
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
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
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
            balancer.addBackendUseCache(LoadBalancerBackendChangeNegativeTest.backend);
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
