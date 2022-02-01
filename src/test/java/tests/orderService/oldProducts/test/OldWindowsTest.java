package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты TEST")
@Feature("Windows OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_windows"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWindowsTest extends Tests {

    final Windows windows = Windows.builder()
            .projectId("proj-juh8ynkvtn")
            .productId("28bed880-2714-4317-a967-d000d492bd9d")
            .orderId("ced7ea99-79af-4ff4-aaf7-7ef32b722d18")
            .productName("Windows")
            .build();

    @Order(1)
    @DisplayName("Перезагрузить Windows OLD")
    @Test
    void restart() {
        try {
            windows.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            windows.restart();
        }
    }

    @Order(2)
    @DisplayName("Выключить Windows OLD")
    @Test
    void stopSoft() {
        windows.stopSoft();
        windows.start();
    }

    @Order(3)
    @DisplayName("Изменить конфигурацию Windows OLD")
    @Test
    void resize() {
        windows.stopHard();
        try {
            windows.resize(windows.getMaxFlavor());
        } finally {
            windows.start();
        }
    }

    @Order(4)
    @DisplayName("Включить Windows OLD")
    @Test
    void start() {
        windows.stopHard();
        windows.start();
    }

    @Order(5)
    @DisplayName("Выключить принудительно Windows OLD")
    @Test
    void stopHard() {
        windows.stopHard();
        windows.start();
    }
}
