package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.WildFly;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты TEST")
@Feature("WildFly OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_wildfly"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWildFlyTest extends Tests {

    final WildFly wildFly = WildFly.builder()
            .projectId("proj-juh8ynkvtn")
            .productId("972a66f1-cd45-437f-b920-676bad68e594")
            .orderId("05e157b1-b9ae-4b23-9e16-74d5e6071db4")
            .productName("WildFly")
            .build();

    @Order(1)
    @DisplayName("Расширить WildFly OLD")
    @Test
    void expandMountPoint() {
        try {
            wildFly.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            wildFly.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить WildFly OLD")
    @Test
    void restart() {
        wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
        wildFly.restart();
    }

    @Order(3)
    @DisplayName("Выключить WildFly OLD")
    @Test
    void stopSoft() {
        wildFly.stopSoft();
        wildFly.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию WildFly OLD")
    @Test
    void resize() {
        if (wildFly.productStatusIs(ProductStatus.ON)) {
            wildFly.stopHard();
            wildFly.resize(wildFly.getMaxFlavor());
            wildFly.resize(wildFly.getMinFlavor());
        } else {
            wildFly.resize(wildFly.getMaxFlavor());
            wildFly.resize(wildFly.getMinFlavor());
        }
    }

    @Order(5)
    @DisplayName("Включить WildFly OLD")
    @Test
    void start() {
        wildFly.stopHard();
        wildFly.start();
    }

    @Order(6)
    @DisplayName("Обновить сертификаты WildFly OLD")
    @Test
    void updateCerts() {
        wildFly.updateCerts();
    }

    @Order(7)
    @DisplayName("Выключить принудительно WildFly OLD")
    @Test
    void stopHard() {
        wildFly.stopHard();
    }
}
