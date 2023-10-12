package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.opentest4j.MultipleFailuresError;

import java.util.Arrays;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerFrontendNegativeTest extends Tests {

    List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/37c93f8e-c2ee-40cb-a5d2-008524676f3f/main?context=proj-ln4zg69jek&type=project&org=vtb");

    Backend backend = Backend.builder().servers(serversHttp).backendName("not_valid_backend_match").build();
    Frontend frontend = Frontend.builder().defaultBackendNameHttp("11").frontendName("http").build();

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание http Frontend c tcp Backend {0}")
    void notValidBackendServerName(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Frontend frontend = Frontend.builder()
                .frontendName("frontend_name")
                .defaultBackendNameHttp(backend.getBackendName())
                .build();

        Throwable throwable = Assertions.assertThrows(MultipleFailuresError.class, () -> balancer.addFrontend(frontend));
        AssertUtils.assertContains(throwable.getMessage(), "tcp");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создание http Frontend c tcp Backend {0}")
    void notValidFrontendName(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Frontend frontend = Frontend.builder()
                .frontendName(new Generex("[a-zA-Z0-9]{256}").random())
                .defaultBackendNameTcp(backend.getBackendName())
                .build();

        Throwable throwable = Assertions.assertThrows(MultipleFailuresError.class, () -> balancer.addFrontend(frontend));
        AssertUtils.assertContains(throwable.getMessage(), "frontend_name");
//        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Изменение Frontend с другим Backend {0}")
    void notValidBackendName(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        Frontend frontend = Frontend.builder()
                .frontendName("http")
                .defaultBackendName("11")
                .frontendPort(900)
                .mode("http")
                .build();

        balancer.editFrontEnd(frontend, false, "not_valid_backend", 901);
//        }
    }

}
