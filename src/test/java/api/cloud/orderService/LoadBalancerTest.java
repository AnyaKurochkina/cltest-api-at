package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.orderService.products.WildFly;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import models.cloud.subModels.loadBalancer.Server;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;
import steps.orderService.OrderServiceSteps;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
@Execution(ExecutionMode.SAME_THREAD)
public class LoadBalancerTest extends Tests {

    List<Server> serversTcp = Arrays.asList(Server.builder().address("10.226.48.194").port(443).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(443).name("d5soul-ngc005lk.corp.dev.vtb").build());
    List<Server> serversHttp = Arrays.asList(Server.builder().address("10.226.48.194").port(80).name("d5soul-ngc004lk.corp.dev.vtb").build(),
            Server.builder().address("10.226.99.132").port(80).name("d5soul-ngc005lk.corp.dev.vtb").build());

    final LoadBalancer balancer = LoadBalancer.builder().build()
            .buildFromLink("https://prod-portal-front.cloud.vtb.ru/network/orders/d7581920-53f9-4b53-8c8a-7ab7488ca043/main?context=proj-ln4zg69jek&type=project&org=vtb");

    //    @TmsLink("391703")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(LoadBalancer product) {
        //noinspection EmptyTryBlock
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//        }
    }

    //    @TmsLink("391705")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.expandMountPoint();
//        }
    }

    //    @TmsLink("391699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.restart();
//        }
    }

    //    @TmsLink("391702")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.stopSoft();
//            balancer.start();
//        }
    }

    //    @TmsLink("391704")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.resize(balancer.getMaxFlavor());
//        }
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.stopHard();
//            balancer.start();
//        }
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "TCP публикация с простой проверкой доступности {0}")
    void tcpSimple(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addTcpSimple(balancer);
//        }
    }

    Frontend addTcpSimple(LoadBalancer balancer) {
        Backend backend = Backend.builder()
                .servers(serversTcp)
                .backendName("backend_tcp_simple")
                .advancedCheck(false)
                .build();
        balancer.addBackend(backend);
        Frontend frontend = Frontend.builder()
                .frontendName("frontend_tcp_simple")
                .defaultBackendNameTcp(backend.getBackendName())
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "TCP публикация с проверкой доступности по http ссылке {0}")
    void tcp(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_tcp_width_check")
                    .advancedCheck(true)
                    .checkUri("/status")
                    .build();
            balancer.addBackend(backend);
            balancer.addFrontend(Frontend.builder()
                    .frontendName("frontend_tcp_width_check")
                    .defaultBackendNameTcp(backend.getBackendName())
                    .build());
//        }
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "HTTP публикация с простой проверкой доступности {0}")
    void httpSimple(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addHttpSimple(balancer);
//        }
    }

    Frontend addHttpSimple(LoadBalancer balancer) {
        Backend backend = Backend.builder()
                .servers(serversHttp)
                .backendName("backend_http_simple")
                .mode("http")
                .balancingAlgorithm("roundrobin")
                .advancedCheck(false)
                .build();
        balancer.addBackend(backend);
        Frontend frontend = Frontend.builder()
                .frontendName("frontend_tcp_simple")
                .mode("http")
                .frontendPort(80)
                .defaultBackendNameTcp(backend.getBackendName())
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "HTTP публикация с проверкой доступности по http ссылке {0}")
    void http(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversHttp)
                    .mode("http")
                    .backendName("backend_http_width_check")
                    .advancedCheck(true)
                    .checkUri("/status")
                    .build();
            balancer.addBackend(backend);
            balancer.addFrontend(Frontend.builder()
                    .frontendName("frontend_http_width_check")
                    .mode("http")
                    .frontendPort(80)
                    .defaultBackendNameTcp(backend.getBackendName())
                    .build());
//        }
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Глобальная TCP публикация {0}")
    void addTcpGslb(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(product);
            balancer.addGslb(Gslb.builder()
                    .globalname("tcp-public")
                    .frontend(frontend)
                    .build());
//        }
    }

    //    @TmsLinks({@TmsLink("391700"), @TmsLink("391701")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Глобальная HTTP публикация {0}")
    void addHttpGslb(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.addGslb(Gslb.builder()
                    .globalname("http-public")
                    .frontend(frontend)
                    .build());
//        }
    }

    //    @TmsLink("391698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление бэкенда {0}")
    void deleteBackend(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_remove")
                    .advancedCheck(false)
                    .build();
            balancer.addBackend(backend);
            balancer.deleteBackend(backend);
//        }
    }

    //    @TmsLink("391698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление фронтенда {0}")
    void deleteFrontend(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.deleteFrontend(frontend);
//        }
    }

    //    @TmsLink("391698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление GSLB публикации {0}")
    void deleteGsbl(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname("tcp-public")
                    .frontend(frontend)
                    .build();
            balancer.addGslb(gslb);
            balancer.deleteGslb(gslb);
//        }
    }

    //    @TmsLink("391698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Синхронизировать информацию о конфигурации глобальных публикаций {0}")
    void gslbSync(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.gslbSync();
//        }
    }

    //    @TmsLink("391698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление всех GSLB публикаций в заказе {0}")
    void deleteAllGslb(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.deleteAllGslb();
//        }
    }

    //    @TmsLink("391698")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(LoadBalancer product) {
//        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
//            balancer.deleteObject();
//        }
    }

}
