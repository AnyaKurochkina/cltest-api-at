package api.cloud.orderService.loadBalancer;

import api.Tests;
import com.mifmif.common.regex.Generex;
import core.helper.http.AssertResponse;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.List;

@Execution(ExecutionMode.SAME_THREAD)
@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerBackendChangeNegativeTest extends Tests {

    @Mock
    static LoadBalancer loadBalancer = LoadBalancer.builder().platform("OpenStack").env("DEV").segment("dev-srv-app").build()
            .buildFromLink("https://console.blue.cloud.vtb.ru/network/orders/b41c8f54-354a-475e-90be-2a44188c9e8b/main?context=proj-iv550odo9a&type=project&org=vtb");

    public static List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    static List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    static Backend backend = Backend.builder().backendName(new Generex("load_balancer_negative_test-[0-9]{4}").random()).build();
    static Frontend frontend = Frontend.builder().frontendName(new Generex("load_balancer_negative_test-[0-9]{4}")
            .random()).mode("tcp").defaultBackendNameTcp(backend.getBackendName()).build();

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
