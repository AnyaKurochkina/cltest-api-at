package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.WildFly;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.OFF;
import static models.orderService.interfaces.ProductStatus.ON;

@Epic("Старые продукты DEV")
@Feature("WildFly OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_wildfly"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWildFlyTest extends Tests {

    final WildFly wildFly = WildFly.builder()
            .projectId("proj-67nljbzjtt")
            .productId("972a66f1-cd45-437f-b920-676bad68e594")
            .orderId("da58d2c6-bc47-4a52-8aaa-85de22916a89")
            .productName("WildFly")
            .build();

    @Order(1)
    @DisplayName("Расширить WildFly OLD")
    @Test
    void expandMountPoint() {
        if (wildFly.productStatusIs(OFF)) {
            wildFly.start();
        }
        wildFly.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить WildFly OLD")
    @Test
    void restart() {
        if (wildFly.productStatusIs(OFF)) {
            wildFly.start();
        }
        wildFly.restart();
    }

    @Order(3)
    @DisplayName("Выключить WildFly OLD")
    @Test
    void stopSoft() {
        if (wildFly.productStatusIs(OFF)) {
            wildFly.start();
        }
        wildFly.stopSoft();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию WildFly OLD")
    @Test
    void resize() {
        if (wildFly.productStatusIs(ON)) {
            wildFly.stopHard();
        }
        wildFly.resize(wildFly.getMaxFlavor());
        wildFly.resize(wildFly.getMinFlavor());
    }

    @Order(5)
    @DisplayName("Включить WildFly OLD")
    @Test
    void start() {
        if (wildFly.productStatusIs(ON)) {
            wildFly.stopHard();
        }
        wildFly.start();
    }

    @Order(6)
    @DisplayName("Обновить сертификаты WildFly OLD")
    @Test
    void updateCerts() {
        if (wildFly.productStatusIs(OFF)) {
            wildFly.start();
        }
        wildFly.updateCerts();
    }

    @Order(7)
    @DisplayName("Выключить принудительно WildFly OLD")
    @Test
    void stopHard() {
        if (wildFly.productStatusIs(OFF)) {
            wildFly.start();
        }
        wildFly.stopHard();
    }
}
