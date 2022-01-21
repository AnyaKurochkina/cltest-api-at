package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.OFF;
import static models.orderService.interfaces.ProductStatus.ON;

@Epic("Старые продукты DEV")
@Feature("Windows OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_windows"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWindowsTest extends Tests {

    final Windows windows = Windows.builder()
            .projectId("proj-67nljbzjtt")
            .productId("28bed880-2714-4317-a967-d000d492bd9d")
            .orderId("9125747a-7197-42f0-9b32-aa5ccb4cefd4")
            .productName("Windows")
            .build();

    @Order(1)
    @DisplayName("Перезагрузить Windows OLD")
    @Test
    void restart() {
        if (windows.productStatusIs(OFF)) {
            windows.start();
        }
        windows.restart();
    }

    @Order(2)
    @DisplayName("Выключить Windows OLD")
    @Test
    void stopSoft() {
        if (windows.productStatusIs(OFF)) {
            windows.start();
        }
        windows.stopSoft();
    }

    @Order(3)
    @DisplayName("Изменить конфигурацию Windows OLD")
    @Test
    void resize() {
        if (windows.productStatusIs(ON)) {
            windows.stopHard();
        }
        windows.resize(windows.getMaxFlavor());
        windows.resize(windows.getMinFlavor());
    }

    @Order(4)
    @DisplayName("Включить Windows OLD")
    @Test
    void start() {
        if (windows.productStatusIs(ON)) {
            windows.stopHard();
        }
        windows.start();
    }

    @Order(5)
    @DisplayName("Выключить принудительно Windows OLD")
    @Test
    void stopHard() {
        if (windows.productStatusIs(OFF)) {
            windows.start();
        }
        windows.stopHard();
    }
}
