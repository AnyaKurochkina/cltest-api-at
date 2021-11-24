package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Rhel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Rhel old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_rhel"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRhelTest extends Tests {

    Rhel rhel = Rhel.builder()
            .projectId("proj-67nljbzjtt")
            .productId("0da09981-c1ac-45b6-ba3b-7bfe52fd45bc")
            .orderId("fa58784b-69c8-4b2e-b42b-4f704e5bbf2c")
            .build();

    @Order(1)
    @DisplayName("Расширить Rhel OLD")
    @Test
    void expandMountPoint() {
        try {
            rhel.start();
        } finally {
            rhel.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить Rhel OLD")
    @Test
    void restart() {
        rhel.restart();
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
