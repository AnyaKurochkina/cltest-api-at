package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Rhel;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты DEV")
@Feature("Rhel OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_rhel"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRhelTest extends Tests {

    final Rhel rhel = Rhel.builder()
            .projectId("proj-67nljbzjtt")
            .productId("0da09981-c1ac-45b6-ba3b-7bfe52fd45bc")
            .orderId("fa58784b-69c8-4b2e-b42b-4f704e5bbf2c")
            .productName("Rhel")
            .build();

    @Order(1)
    @DisplayName("Перезагрузить Rhel OLD")
    @Test
    void restart() {
        try {
            rhel.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            rhel.restart();
        }
    }

    @Order(3)
    @DisplayName("Выключить Rhel OLD")
    @Test
    void stopSoft() {
        rhel.stopSoft();
        rhel.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Rhel OLD")
    @Test
    void resize() {
        rhel.stopHard();
        try {
            rhel.resize();
        } finally {
            rhel.start();
        }
    }

    @Order(5)
    @DisplayName("Включить Rhel OLD")
    @Test
    void start() {
        rhel.stopHard();
        rhel.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Rhel OLD")
    @Test
    void stopHard() {
        rhel.stopHard();
    }
}
