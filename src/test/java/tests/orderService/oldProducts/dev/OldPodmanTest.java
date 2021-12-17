package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Podman;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Podman OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_podman"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPodmanTest extends Tests {

    Podman podman = Podman.builder()
            .projectId("proj-67nljbzjtt")
            .productId("91025447-e6d3-4b91-be18-a84d62402825")
            .orderId("faaa9d8d-d3e8-4778-b14d-24be995ec878")
            .productName("Podman")
            .build();

    @Order(1)
    @Test
    @DisplayName("Расширить Podman OLD")
    void expandMountPoint() {
        try {
            podman.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            podman.expandMountPoint();
        }
    }

    @Order(2)
    @Test
    @DisplayName("Выключить Podman OLD")
    void stopSoft() {
        podman.stopSoft();
        podman.start();
    }

    @Order(3)
    @Test
    @DisplayName("Включить Podman OLD")
    void start() {
        try {
            podman.stopHard();
        } finally {
            podman.start();
        }
    }

    @Order(4)
    @Test
    @DisplayName("Выключить принудительно Podman OLD")
    void stopHard() {
        podman.stopHard();
    }
}
