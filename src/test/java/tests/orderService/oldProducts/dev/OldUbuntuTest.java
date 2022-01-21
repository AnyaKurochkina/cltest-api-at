package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Ubuntu;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты DEV")
@Feature("Ubuntu OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_ubuntu"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldUbuntuTest extends Tests {

    final Ubuntu ubuntu = Ubuntu.builder()
            .projectId("proj-67nljbzjtt")
            .productId("9d87b2b4-4145-401a-b1b1-edac6bb0c5e4")
            .orderId("f474dac0-1e37-4826-b4ba-1c9effbfa045")
            .productName("Ubuntu")
            .build();

    @Order(1)
    @DisplayName("Расширить Ubuntu OLD")
    @Test
    void expandMountPoint() {
        if (ubuntu.productIsOn()) {
            ubuntu.expandMountPoint();
        } else {
            ubuntu.start();
            ubuntu.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить Ubuntu OLD")
    @Test
    void restart() {
        if (ubuntu.productIsOn()) {
            ubuntu.restart();
        } else {
            ubuntu.start();
            ubuntu.restart();
        }
    }

    @Order(3)
    @DisplayName("Выключить ")
    @Test
    void stopSoft() {
        if (ubuntu.productIsOn()) {
            ubuntu.stopSoft();
            ubuntu.start();
        } else {
            ubuntu.start();
        }
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Ubuntu OLD")
    @Test
    void resize() {
        if (ubuntu.productIsOn()) {
            ubuntu.stopHard();
            ubuntu.resize(ubuntu.getMaxFlavor());
            ubuntu.resize(ubuntu.getMinFlavor());
        } else {
            ubuntu.resize(ubuntu.getMaxFlavor());
            ubuntu.resize(ubuntu.getMinFlavor());
        }
    }

    @Order(5)
    @DisplayName("Включить Ubuntu OLD")
    @Test
    void start() {
        if (ubuntu.productIsOn()) {
            ubuntu.stopHard();
            ubuntu.start();
        } else {
            ubuntu.start();
        }
    }

    @Order(6)
    @DisplayName("Выключить принудительно Ubuntu OLD")
    @Test
    void stopHard() {
        if (ubuntu.productIsOn()) {
            ubuntu.stopHard();
        } else {
            ubuntu.start();
            ubuntu.stopHard();
        }
    }
}
