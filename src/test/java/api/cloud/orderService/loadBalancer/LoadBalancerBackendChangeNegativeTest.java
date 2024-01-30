package api.cloud.orderService.loadBalancer;

import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendChangeNegativeTest extends AbstractLoadBalancerTest {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Редактирование Backend. Недоступность изменения режима {0}")
    void notValidBackendServerName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().build();
            balancer.addBackendUseCache(backend);
            AssertResponse.run(() -> balancer.editBackend(Backend.simpleHttpBackendWidthHttpCheck().backendName(backend.getBackendName()).build()))
                    .status(422).responseContains("'http' is not one of ['tcp']");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление Backend. Несуществующий backend_name {0}")
    void deleteNotValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            AssertResponse.run(() -> balancer.deleteBackends(Backend.simpleTcpBackendWidthTcpCheck().build()))
                    .status(422).responseContains("backend_name");
        }
    }
}
