package api.cloud.orderService.loadBalancer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.common.mapper.TypeRef;
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
import steps.orderService.OrderServiceSteps;

import java.util.Collections;
import java.util.List;

import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversHttp;
import static api.cloud.orderService.loadBalancer.LoadBalancerBackendChangeNegativeTest.serversTcp;

//@Execution(ExecutionMode.SAME_THREAD)
@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerTest extends Tests {

//    @Mock
//    static LoadBalancer loadBalancer = LoadBalancer.builder().platform("OpenStack").env("IFT").segment("test-srv-synt").build()
//            .buildFromLink("https://console.blue.cloud.vtb.ru/all/orders/ef286138-92e8-4fc8-9ece-a37a8ddd0b73/main?context=proj-csyn7gq5se&type=project&org=vtb");

    @TmsLink("1286242")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(LoadBalancer product, Integer num) {
        //noinspection EmptyTryBlock
        try (LoadBalancer ignored = product.createObjectExclusiveAccess()) {
        }
    }

    @Disabled
    @TmsLink("1286247")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.restart();
        }
    }

    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.stopSoft();
            balancer.start();
        }
    }

    @TmsLink("1286249")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.resize(balancer.getMaxFlavorLinuxVm());
        }
    }

    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.stopHard();
            balancer.start();
        }
    }

    @TmsLink("1286250")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] TCP публикация с простой проверкой доступности {0}")
    void tcpSimple(LoadBalancer product, Integer num) {
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
    @ParameterizedTest(name = "[{1}] TCP публикация с проверкой доступности по http ссылке {0}")
    void tcp(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_tcp_width_check")
                    .advancedCheck(false)
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
    @ParameterizedTest(name = "[{1}] HTTP публикация с простой проверкой доступности {0}")
    void httpSimple(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            addHttpSimple(balancer);
        }
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
                .frontendName("frontend_http_simple")
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
    @ParameterizedTest(name = "[{1}] HTTP публикация с проверкой доступности по http ссылке {0}")
    void http(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversHttp)
                    .mode("http")
                    .backendName("backend_http_width_check")
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
    @ParameterizedTest(name = "[{1}] Глобальная TCP публикация {0}")
    void addTcpGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            balancer.addGslb(Gslb.builder()
                    .globalname("glb-tcp-public-" + balancer.getEnv().toLowerCase())
                    .frontend(frontend.getFrontendName())
                    .build());
        }
    }

    @TmsLink("1286259")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Глобальная HTTP публикация {0}")
    void addHttpGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.addGslb(Gslb.builder()
                    .globalname("glb-http-public-" + balancer.getEnv().toLowerCase())
                    .frontend(frontend.getFrontendName())
                    .build());
        }
    }

    @TmsLink("1286260")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление бэкенда {0}")
    void deleteBackend(LoadBalancer product, Integer num) {
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
    @ParameterizedTest(name = "[{1}] Удаление фронтенда {0}")
    void deleteFrontend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            balancer.deleteFrontend(frontend);
        }
    }

    @TmsLink("1286262")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление GSLB публикации {0}")
    void deleteGsbl(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname("glb-tcp-public" + balancer.getEnv().toLowerCase())
                    .frontend(frontend.getFrontendName())
                    .build();
            balancer.addGslb(gslb);
            balancer.deleteGslb(gslb);
        }
    }

    @TmsLink("1286265")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Синхронизировать информацию о конфигурации глобальных публикаций {0}")
    void gslbSync(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.gslbSync();
        }
    }

    @TmsLink("1286266")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление всех GSLB публикаций в заказе {0}")
    void deleteAllGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addTcpSimple(balancer);
            Gslb gslb = Gslb.builder()
                    .globalname("glb-tcp-public-delete-all" + balancer.getEnv().toLowerCase())
                    .frontend(frontend.getFrontendName())
                    .build();
            balancer.addGslb(gslb);
            balancer.deleteAllGslb();
        }
    }

    @TmsLink("1676788")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Откат конфигурации {0}")
    void revertConfig(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("revert_config")
                    .advancedCheck(false)
                    .checkUri("/status")
                    .build();
            balancer.revertConfig(backend);

        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение фронтенда {0}")
    void editFrontend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = addHttpSimple(balancer);
            frontend.setDefaultBackendName(frontend.getDefaultBackendNameHttp());
            frontend.setDefaultBackendNameHttp(null);
            balancer.editFrontEnd(frontend, false, frontend.getDefaultBackendNameHttp(), 999);
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение бэкенда {0}")
    void editBackend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_edit9")
                    .advancedCheck(false)
                    .build();
            balancer.addBackend(backend);
            balancer.editBackend(backend.getBackendName(), "delete", serversTcp);
        }
    }

    @TmsLink("SOUL-8013")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создание проверки доступности. httpchk {0}")
    void createHeathCheck(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder()
                    .servers(serversTcp)
                    .backendName("backend_for_heath")
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

    @TmsLink("SOUL-8012")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Синхронизация конфигурации {0}")
    void fullSync(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.fullSync();
        }
    }

    @TmsLink("SOUL-8011")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Включить/Выключить maintenance {0}")
    void changePublicationsMaintenanceMode(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            String hostname = OrderServiceSteps.getObjectClass(balancer,"data.find{it.type=='cluster'}.data.config.cluster_nodes[0].name", String.class);
            ChangePublicationsMaintenanceMode modeOff = ChangePublicationsMaintenanceMode.builder().state("inactive")
                    .hostnameOn(Collections.singletonList(hostname)).build();
            balancer.changePublicationsMaintenanceMode(modeOff);
            ChangePublicationsMaintenanceMode modeOn = ChangePublicationsMaintenanceMode.builder().state("active")
                    .hostnameOff(Collections.singletonList(hostname)).build();
            balancer.changePublicationsMaintenanceMode(modeOn);
        }
    }

    @TmsLink("SOUL-8010")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение таймаутов {0}")
    void editTimeouts(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.editTimeouts(300000, 300000, 300000);
        }
    }

    @TmsLink("SOUL-8009")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление ОС {0}")
    void updateOs(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.updateOs();
        }
    }

    @TmsLink("SOUL-8008")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление сертификатов {0}")
    void updateCertificates(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.updateCertificates("all");
        }
    }

    @TmsLink("SOUL-8007")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Вертикальное масштабирование {0}")
    void resizeClusterVms(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.resizeClusterVms(balancer.getMaxFlavor());
        }
    }

    @TmsLink("SOUL-8006")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Горизонтальное масштабирование {0}")
    void addHaproxy(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.addHaproxy(6);
            startAllHosts(balancer);
            balancer.addHaproxy(4);
            startAllHosts(balancer);
        }
    }

    private void startAllHosts(LoadBalancer balancer) {
        List<String> hostnames = OrderServiceSteps.getObjectClass(balancer,
                "data.find{it.type=='cluster'}.data.config.cluster_nodes.findAll{it.main_status=='off'}.name", new TypeRef<List<String>>() {});
        ChangePublicationsMaintenanceMode modeOn = ChangePublicationsMaintenanceMode.builder().state("active").hostnameOff(hostnames).build();
        balancer.changePublicationsMaintenanceMode(modeOn);
    }

    @TmsLink("SOUL-8005")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Увеличить дисковое пространство {0}")
    void expandMountPoint(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.expandMountPoint();
        }
    }

    @TmsLink("SOUL-8004")
    @Disabled
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Комплексное создание {0}")
    void complexCreate(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.builder().servers(serversTcp).backendName("complex_backend3").advancedCheck(false).build();
            Frontend frontend = Frontend.builder().frontendName("complex_frontend3").frontendPort(80)
                    .defaultBackendNameTcp(backend.getBackendName()).build();
            HealthCheck healthCheck = HealthCheck.builder().backendName(backend.getBackendName())
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringType("connect").stringAddress("10.0.0.1").stringPort(10).stringUseSsl("disabled")
                            .stringSendProxy("disabled").build())).build();
            RouteSni routeSni = RouteSni.builder()
                    .routes(Collections.singletonList(new RouteSni.Route(backend.getBackendName(), "complex_sni"))).build();

            ComplexCreate complex = ComplexCreate.builder()/*.healthCheck(healthCheck).sniRoute(routeSni)*/
                    .backend(backend)/*.frontend(frontend)*/.build();
            balancer.complexCreate(complex);
        }
    }

    @TmsLink("1286267")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.deleteObject();
        }
    }

}
