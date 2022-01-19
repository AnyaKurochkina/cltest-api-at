package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Nginx;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты DEV")
@Feature("Nginx OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_nginx"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldNginxTest extends Tests {

    final Nginx nginx = Nginx.builder()
            .projectId("proj-67nljbzjtt")
            .productId("ebe832bd-ed1c-4998-8a58-6c4d16db1d60")
            .orderId("ef57bd15-2f61-4c34-8460-bf77d7312808")
            .productName("Nginx")
            .build();

    @Order(1)
    @DisplayName("Расширить Nginx OLD")
    @Test
    void expandMountPoint() {
        try {
            nginx.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            nginx.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить Nginx OLD")
    @Test
    void restart() {
        nginx.restart();
    }

    @Order(3)
    @DisplayName("Выключить Nginx OLD")
    @Test
    void stopSoft() {
        nginx.stopSoft();
        nginx.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Nginx OLD")
    @Test
    void resize() {
        nginx.stopHard();
        try {
            nginx.resize();
        } finally {
            nginx.start();
        }
    }

    @Order(5)
    @DisplayName("Включть Nginx OLD")
    @Test
    void start() {
        nginx.stopHard();
        nginx.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Nginx OLD")
    @Test
    void stopHard() {
        nginx.stopHard();
    }
}
