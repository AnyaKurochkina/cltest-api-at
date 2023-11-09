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
import models.cloud.subModels.loadBalancer.Server;
import org.junit.Mock;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.opentest4j.MultipleFailuresError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendChangeNegativeTest extends Tests {

    static List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    static List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    static Backend backend = Backend.builder().servers(serversHttp).backendName(new Generex("load_balancer_negative_test-[0-9]{4}").random()).build();
    static Frontend frontend = Frontend.builder().frontendName(new Generex("load_balancer_negative_test-[0-9]{4}")
            .random()).mode("tcp").defaultBackendNameTcp(backend.getBackendName()).build();

    @Mock
    static LoadBalancer balancer = LoadBalancer.builder().build()
              //.buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/37c93f8e-c2ee-40cb-a5d2-008524676f3f/main?context=proj-ln4zg69jek&type=project&org=vtb");
              .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/76e2f498-36f5-43c9-88f2-a664fec0a7f0/frontends?context=proj-2xdbtyzqs3&type=project&org=vtb");

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Редактирование Backend. delete. Несуществующий server.name {0}")
    void notValidBackendServerName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().name("not_valid").build());
            balancer.addBackend(backend);
            Throwable throwable = Assertions.assertThrows(MultipleFailuresError.class, () ->
                    balancer.editBackend(backend.getBackendName(), "delete", servers));
            AssertUtils.assertContains(throwable.getMessage(), "The entered servers were not found in the specified backend");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Редактирование Backend. Несуществующий action {0}")
    void notValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            List<Server> servers = Collections.singletonList(Server.builder().address("10.10.10.10").port(80).name("name").build());
            balancer.addBackend(backend);
            AssertResponse.run(() -> balancer.editBackend(backend.getBackendName(), "not_valid", servers)).status(422).responseContains("action");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление Backend. Несуществующий backend_name {0}")
    void deleteNotValidBackendName(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            AssertResponse.run(() -> balancer.deleteBackend(Backend.builder().backendName("not_valid").build())).status(422).responseContains("backend_name");
        }
    }
}
