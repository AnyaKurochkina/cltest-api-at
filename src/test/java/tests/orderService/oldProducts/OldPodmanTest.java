package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Podman;
import models.orderService.products.PostgreSQL;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Podman old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_podman"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPodmanTest extends Tests {

    Podman podman = Podman.builder()
            .projectId("proj-67nljbzjtt")
            .productId("91025447-e6d3-4b91-be18-a84d62402825")
            .orderId("faaa9d8d-d3e8-4778-b14d-24be995ec878")
            .build();

    @Order(1)
    @Test
    @DisplayName("Расширить Podman Old")
    void expandMountPoint() {
        podman.start();
        podman.expandMountPoint();
    }

    @Order(2)
    @Test
    @DisplayName("Выключить Podman Old")
    void stopSoft() {
        podman.stopSoft();
        podman.start();
    }

    @Order(3)
    @Test
    @DisplayName("Включить Podman Old")
    void start() {
        try {
            podman.stopHard();
        } finally {
            podman.start();
        }
    }

    @Order(4)
    @Test
    @DisplayName("Выключить принудительно Podman Old")
    void stopHard() {
        podman.stopHard();
    }
}
