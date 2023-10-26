package api.cloud.orderService.loadBalancer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.LoadBalancer;
import models.cloud.subModels.loadBalancer.*;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Collections;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversHttp;
import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerTest extends Tests {

    @TmsLink("1286242")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(LoadBalancer product) {
        //noinspection EmptyTryBlock
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1286246")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("1286247")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.restart();
        }
    }

    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.stopSoft();
            balancer.start();
        }
    }

    @TmsLink("1286249")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.resize(balancer.getMaxFlavorLinuxVm());
        }
    }

    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.stopHard();
            balancer.start();
        }
    }

    @TmsLink("1286250")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "TCP публикация с простой проверкой доступности {0}")
    void tcpSimple(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addTcpSimple(balancer);
        }
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

    @TmsLink("1286253")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "TCP публикация с проверкой доступности по http ссылке {0}")
    void tcp(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_tcp_width_check")
                    .advancedCheck(true)
                    .checkUri("/status")
                    .build();
            balancer.addBackend(backend);
            if (balancer.isDev())
                Assertions.assertTrue(balancer.isStateContains(backend.getBackendName()));
            final Frontend frontendTcpWidthCheck = Frontend.builder()
                    .frontendName("frontend_tcp_width_check")
                    .defaultBackendNameTcp(backend.getBackendName())
                    .build();
            balancer.addFrontend(frontendTcpWidthCheck);
            if (balancer.isDev())
                Assertions.assertTrue(balancer.isStateContains(frontendTcpWidthCheck.getFrontendName()));
        }
    }

    @TmsLink("1286255")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "HTTP публикация с простой проверкой доступности {0}")
    void httpSimple(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addHttpSimple(balancer);
        }
    }

    Frontend addHttpSimple(LoadBalancer balancer) {
        Backend backend = Backend.builder()
                .servers(serversHttp)
                .backendName("backend_http_simple_4")
                .mode("http")
                .balancingAlgorithm("roundrobin")
                .advancedCheck(false)
                .build();
        balancer.addBackend(backend);
        Frontend frontend = Frontend.builder()
                .frontendName("frontend_http_simple_4")
                .mode("http")
                .frontendPort(80)
                .defaultBackendNameHttp(backend.getBackendName())
                .build();
        balancer.addFrontend(frontend);
        return frontend;
    }

    @TmsLink("1286256")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "HTTP публикация с проверкой доступности по http ссылке {0}")
    void http(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
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
                    .defaultBackendNameHttp(backend.getBackendName())
                    .build());
        }
    }

    @TmsLink("1286258")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Глобальная TCP публикация {0}")
    void addTcpGslb(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            balancer.addGslb(Gslb.builder()
                    .globalname("glb-tcp-public-" + balancer.getEnv().toLowerCase())
                    .frontend(frontend)
                    .build());
        }
    }

    @TmsLink("1286259")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Глобальная HTTP публикация {0}")
    void addHttpGslb(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.addGslb(Gslb.builder()
                    .globalname("glb-http-public-" + balancer.getEnv().toLowerCase())
                    .frontend(frontend)
                    .build());
        }
    }

    @TmsLink("1286260")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление бэкенда {0}")
    void deleteBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_remove")
                    .advancedCheck(false)
                    .build();
            balancer.addBackend(backend);
            balancer.deleteBackend(backend);
        }
    }

    @TmsLink("1286261")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление фронтенда {0}")
    void deleteFrontend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.deleteFrontend(frontend);
        }
    }

    @TmsLink("1286262")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление GSLB публикации {0}")
    void deleteGsbl(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname("glb-tcp-public" + balancer.getEnv().toLowerCase())
                    .frontend(frontend)
                    .build();
            balancer.addGslb(gslb);
            balancer.deleteGslb(gslb);
        }
    }

    @TmsLink("1286265")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Синхронизировать информацию о конфигурации глобальных публикаций {0}")
    void gslbSync(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.gslbSync();
        }
    }

    @TmsLink("1286266")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удаление всех GSLB публикаций в заказе {0}")
    void deleteAllGslb(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname("glb-tcp-public-delete-all" + balancer.getEnv().toLowerCase())
                    .frontend(frontend)
                    .build();
            balancer.addGslb(gslb);
            balancer.deleteAllGslb();
        }
    }

    @TmsLink("1676788")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Откат конфигурации {0}")
    void revertConfig(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("revert_config")
                    .advancedCheck(true)
                    .checkUri("/status")
                    .build();
            balancer.revertConfig(backend);

        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение фронтенда {0}")
    void editFrontend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            frontend.setDefaultBackendName(frontend.getDefaultBackendNameHttp());
            frontend.setDefaultBackendNameHttp(null);
            balancer.editFrontEnd(frontend, false, frontend.getDefaultBackendNameHttp(), 999);
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменение бэкенда {0}")
    void editBackend(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_edit")
                    .advancedCheck(false)
                    .build();
            balancer.addBackend(backend);
            balancer.editBackend(backend.getBackendName(), "delete", serversTcp);
        }
    }

    @TmsLink("")
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создание проверки доступности. httpchk {0}")
    void createHeathCheck(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_heath2")
                    .advancedCheck(false)
                    .build();
            balancer.addBackend(backend);
            HealthCheck healthCheck = HealthCheck.builder().backendName(backend.getBackendName())
                    .protocol("httpchk")
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringType("connect")
                            .stringAddress("10.0.0.1")
                            .stringPort(10)
                            .stringUseSsl("disabled")
                            .stringSendProxy("disabled")
                            .build()))
                    .checkMethod("GET")
                    .checkUri("/")
                    .build();
            balancer.createHealthCheck(healthCheck);
        }
    }

    @TmsLink("1286267")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(LoadBalancer product) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.deleteObject();
        }
    }

}
