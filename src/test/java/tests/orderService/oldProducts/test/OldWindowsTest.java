package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

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
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.restart();
    }

    @Order(2)
    @DisplayName("Выключить Windows OLD")
    @Test
    void stopSoft() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.stopSoft();
    }

    @Order(3)
    @DisplayName("Изменить конфигурацию Windows OLD")
    @Test
    void resize() {
        if (windows.productStatusIs(STARTED)) {
            windows.stopHard();
        }
        windows.resize(windows.getMaxFlavor());
        windows.resize(windows.getMinFlavor());
    }

    @Order(4)
    @DisplayName("Включить Windows OLD")
    @Test
    void start() {
        if (windows.productStatusIs(STARTED)) {
            windows.stopHard();
        }
        windows.start();
    }

    @Order(5)
    @DisplayName("Выключить принудительно Windows OLD")
    @Test
    void stopHard() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.stopHard();
    }
}
