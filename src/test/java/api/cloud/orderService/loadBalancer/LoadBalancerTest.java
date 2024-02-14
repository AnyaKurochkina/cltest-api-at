package api.cloud.orderService.loadBalancer;

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

@Epic("Продукты")
@Feature("Load Balancer")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("load_balancer"), @Tag("prod")})
public class LoadBalancerTest extends AbstractLoadBalancerTest {


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
            createSimpleTcpFrontend(balancer);
        }
    }

    @TmsLink("1286253")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] TCP публикация с проверкой доступности по http ссылке {0}")
    void tcp(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createSimpleTcpBackend(balancer);
            if (balancer.isDev())
                Assertions.assertTrue(balancer.isStateContains(backend.getBackendName()));
            Frontend frontendTcpWidthCheck = createSimpleTcpFrontend(balancer);
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
            createSimpleTcpFrontend(balancer);
        }
    }

    @TmsLink("1286256")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] HTTP публикация с проверкой доступности по http ссылке {0}")
    void http(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            createSimpleHttpBackend(balancer);
        }
    }

    @TmsLink("1286258")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Глобальная TCP публикация {0}")
    void addTcpGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            createSimpleTcpGslb(balancer);
        }
    }

    @TmsLink("1286259")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Глобальная HTTP публикация {0}")
    void addHttpGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            createSimpleHttpGslb(balancer);
        }
    }

    @TmsLink("1286260")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление бэкенда {0}")
    void deleteBackend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName("delete_backend").build();
            balancer.addBackendUseCache(backend);
            balancer.deleteBackends(backend);
        }
    }

    @TmsLink("1286261")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление фронтенда {0}")
    void deleteFrontend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = createSimpleHttpFrontend(balancer);
            balancer.deleteFrontends(frontend);
        }
    }

    @TmsLink("1286262")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление GSLB публикации {0}")
    void deleteGsbl(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Gslb gslb = createSimpleHttpGslb(balancer);
            balancer.deleteGslb(gslb);
        }
    }

    @TmsLink("1286266")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удаление всех GSLB публикаций в заказе {0}")
    void deleteAllGslb(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Gslb gslb = createSimpleTcpGslb(balancer);
            balancer.addGslbUseCache(gslb);
            balancer.deleteAllGslb();
        }
    }

    @TmsLink("1676788")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Откат конфигурации {0}")
    void revertConfig(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = Backend.simpleTcpBackendWidthTcpCheck().backendName("backend_revert_" + UNIQUE_POSTFIX).build();
            balancer.addBackend(backend);
            balancer.revertConfig(backend);
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение фронтенда {0}")
    void editFrontend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Frontend frontend = createSimpleTcpFrontend(balancer);
            frontend.setKeepAliveTcp(true);
            frontend.setKeepCntTcp(3);
            frontend.setKeepTimerTcp(3);
            balancer.editFrontEnd(frontend);
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение бэкенда {0}")
    void editBackend(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createSimpleTcpBackend(balancer);
            balancer.addBackendUseCache(backend);
            backend.setBalancingAlgorithm("source");
            balancer.editBackend(backend);
        }
    }

    @TmsLink("SOUL-8013")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменение проверки доступности. {0}")
    void createHeathCheck(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            Backend backend = createSimpleTcpBackend(balancer);
            String healthCheckName = balancer.healthCheckByBackendName(backend.getBackendName());
            HealthCheck healthCheck = HealthCheck.simpleHttpHealthCheck(healthCheckName, backend.getBackendName()).build();
            balancer.editHealthCheck(healthCheck);
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

    @TmsLink("SOUL-8012")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление кластера {0}")
    void balancerUpdateCluster(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.updateCluster(true);
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
    @Disabled
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновление ОС {0}")
    void updateOs(LoadBalancer product, Integer num) {
        try (LoadBalancer balancer = product.createObjectExclusiveAccess()) {
            balancer.updateOs();
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
        if(!hostnames.isEmpty()) {
            ChangePublicationsMaintenanceMode modeOn = ChangePublicationsMaintenanceMode.builder().state("active").hostnameOff(hostnames).build();
            balancer.changePublicationsMaintenanceMode(modeOn);
        }
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
            Backend backend = Backend.builder().backendName("complex_backend3").build();
            Frontend frontend = Frontend.builder().frontendName("complex_frontend3").frontendPort(80)
                    .defaultBackendNameTcp(backend.getBackendName()).build();
            HealthCheck healthCheck = HealthCheck.builder().backendName(backend.getBackendName())
                    .checkStrings(Collections.singletonList(CheckString.builder()
                            .stringAddress("10.0.0.1").stringPort(10).stringUseSsl("disabled")
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
