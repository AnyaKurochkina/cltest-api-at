package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Rhel;
import models.orderService.products.Ubuntu;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Ubuntu old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_ubuntu"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldUbuntuTest extends Tests {

    Ubuntu ubuntu = Ubuntu.builder()
            .projectId("proj-67nljbzjtt")
            .productId("9d87b2b4-4145-401a-b1b1-edac6bb0c5e4")
            .orderId("f474dac0-1e37-4826-b4ba-1c9effbfa045")
            .build();

    @Order(1)
    @DisplayName("Расширить ")
    @Test
    void expandMountPoint() {
        try {
            ubuntu.start();
        } finally {
            ubuntu.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить ")
    @Test
    void restart() {
        ubuntu.restart();
    }

    @Order(3)
    @DisplayName("Выключить ")
    @Test
    void stopSoft() {
        ubuntu.stopSoft();
        ubuntu.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию ")
    @Test
    void resize() {
        ubuntu.stopHard();
        try {
            ubuntu.resize();
        } finally {
            ubuntu.start();
        }
    }

    @Order(5)
    @DisplayName("Включить ")
    @Test
    void start() {
        ubuntu.stopHard();
        ubuntu.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно ")
    @Test
    void stopHard() {
        ubuntu.stopHard();
    }
}
