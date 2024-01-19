package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.opentest4j.MultipleFailuresError;

import java.util.Collections;

@Execution(ExecutionMode.SAME_THREAD)
@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerFrontendNegativeTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание http Frontend c tcp Backend {0}")
    void notValidBackendServerName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().build();
            balancer.addBackendUseCache(backend);
            Throwable throwable = Assertions.assertThrows(MultipleFailuresError.class,
                    () -> balancer.addFrontend(Frontend.simpleHttpFrontend(backend.getBackendName()).build()));
            AssertUtils.assertContains(throwable.getMessage(), "tcp");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание Frontend. Невалидный backend_name {0}")
    void notValidFrontendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().build();
            balancer.addBackendUseCache(backend);
            Frontend frontend = Frontend.simpleTcpFrontend(backend.getBackendName())
                    .frontendName(new Generex("[a-zA-Z0-9]{256}").random())
                    .build();
            Throwable throwable = Assertions.assertThrows(MultipleFailuresError.class, () -> balancer.addFrontend(frontend));
            AssertUtils.assertContains(throwable.getMessage(), "frontend_name");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Массовое Удаление. Несуществующий name {0}")
    void deleteFrontendsNotValidFrontendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = Frontend.builder()
                    .frontendName("not_valid_name")
                    .build();
            AssertResponse.run(() -> balancer.deleteFrontends(Collections.singletonList(frontend))).status(422).responseContains("frontends.0");
        }
    }

}
