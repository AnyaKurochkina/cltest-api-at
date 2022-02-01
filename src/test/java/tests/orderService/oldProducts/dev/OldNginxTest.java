package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Nginx;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

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
        if (nginx.productStatusIs(STOPPED)) {
            nginx.start();
        }
        nginx.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить Nginx OLD")
    @Test
    void restart() {
        if (nginx.productStatusIs(STOPPED)) {
            nginx.start();
        }
        nginx.restart();
    }

    @Order(3)
    @DisplayName("Выключить Nginx OLD")
    @Test
    void stopSoft() {
        if (nginx.productStatusIs(STOPPED)) {
            nginx.start();
        }
        nginx.stopSoft();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Nginx OLD")
    @Test
    void resize() {
        if (nginx.productStatusIs(STARTED)) {
            nginx.stopHard();
        }
        nginx.resize(nginx.getMaxFlavor());
        nginx.resize(nginx.getMinFlavor());
    }

    @Order(5)
    @DisplayName("Включить Nginx OLD")
    @Test
    void start() {
        if (nginx.productStatusIs(STARTED)) {
            nginx.stopHard();
        }
        nginx.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Nginx OLD")
    @Test
    void stopHard() {
        if (nginx.productStatusIs(STOPPED)) {
            nginx.start();
        }
        nginx.stopHard();
    }
}
