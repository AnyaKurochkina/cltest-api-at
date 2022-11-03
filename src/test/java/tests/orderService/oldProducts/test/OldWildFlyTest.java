package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.WildFly;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты TEST")
@Feature("WildFly Astra OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_wildfly_astra"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWildFlyTest extends Tests {

    final WildFly wildFly = WildFly.builder()
            .productName("WildFly Astra")
            .projectId("proj-bhbyhmik3a")
            .productId("4a8959f3-f926-4497-a61b-46a227ebed9b")
            .orderId("23212a4b-da7c-455e-92cb-3f2df528b457")
            .build();

    @Order(1)
    @TmsLink("841689")
    @DisplayName("Расширить WildFly Astra OLD")
    @Test
    void expandMountPoint() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.expandMountPoint();
    }

    @Order(2)
    @TmsLink("841694")
    @DisplayName("Перезагрузить WildFly Astra OLD")
    @Test
    void restart() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.restart();
    }

    @Order(3)
    @TmsLink("841690")
    @DisplayName("Изменить конфигурацию WildFly Astra OLD")
    @Test
    void resize() {
//        if (wildFly.productStatusIs(STARTED)) {
//            wildFly.stopHard();
//        }
        wildFly.resize(wildFly.getMaxFlavor());
        wildFly.resize(wildFly.getMinFlavor());
    }

    @Order(4)
    @TmsLink("841688")
    @DisplayName("Проверить конфигурацию WildFly Astra OLD")
    @Test
    void refreshVmConfig() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.refreshVmConfig();
    }

    @Disabled
    @Order(5)
    @TmsLink("841693")
    @DisplayName("Включить WildFly Astra OLD")
    @Test
    void start() {
        if (wildFly.productStatusIs(STARTED)) {
            wildFly.stopHard();
        }
        wildFly.start();
    }

    @Order(6)
    @TmsLink("841692")
    @DisplayName("Обновить сертификаты WildFly Astra OLD")
    @Test
    void updateCerts() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.updateCerts();
    }

    @Disabled
    @Order(7)
    @TmsLink("841697")
    @DisplayName("Выключить принудительно WildFly Astra OLD")
    @Test
    void stopHard() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.stopHard();
    }

    @Disabled
    @Order(8)
    @TmsLink("841696")
    @DisplayName("Выключить WildFly Astra OLD")
    @Test
    void stopSoft() {
        if (wildFly.productStatusIs(STOPPED)) {
            wildFly.start();
        }
        wildFly.stopSoft();
    }
}
