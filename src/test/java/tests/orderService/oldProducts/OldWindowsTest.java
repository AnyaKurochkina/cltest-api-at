package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Windows OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_windows"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWindowsTest extends Tests {

    Windows windows = Windows.builder()
            .projectId("proj-67nljbzjtt")
            .productId("28bed880-2714-4317-a967-d000d492bd9d")
            .orderId("9125747a-7197-42f0-9b32-aa5ccb4cefd4")
            .productName("Windows")
            .build();

    @Order(1)
    @DisplayName("Добавить диск Windows OLD")
    @Test
    void addDisk() {
        try {
            windows.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            windows.addDisk("A");
        }
    }

    @Order(2)
    @DisplayName("Расширить диск Windows OLD")
    @Test
    void expandMountPoint() {
        windows.addDisk("I");
        windows.expandMountPoint("I");
    }

    @Order(3)
    @DisplayName("Перезагрузить Windows OLD")
    @Test
    void restart() {
        windows.restart();
    }

    @Order(4)
    @DisplayName("Выключить Windows OLD")
    @Test
    void stopSoft() {
        windows.stopSoft();
        windows.start();
    }

    @Order(5)
    @DisplayName("Изменить конфигурацию Windows OLD")
    @Test
    void resize() {
        windows.stopHard();
        try {
            windows.resize();
        } finally {
            windows.start();
        }
    }

    @Order(6)
    @DisplayName("Включить Windows OLD")
    @Test
    void start() {
        windows.stopHard();
        windows.start();
    }

    @Order(7)
    @DisplayName("Выключить принудительно Windows OLD")
    @Test
    void stopHard() {
        windows.stopHard();
        windows.start();
    }
}
