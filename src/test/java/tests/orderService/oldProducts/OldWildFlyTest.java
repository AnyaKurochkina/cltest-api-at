package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Nginx;
import models.orderService.products.WildFly;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Старые продукты")
@Feature("WildFly OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_wildfly"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWildFlyTest extends Tests {

    WildFly wildFly = WildFly.builder()
            .projectId("proj-67nljbzjtt")
            .productId("972a66f1-cd45-437f-b920-676bad68e594")
            .orderId("da58d2c6-bc47-4a52-8aaa-85de22916a89")
            .productName("WildFly")
            .build();

    @Order(1)
    @DisplayName("Расширить WildFly OLD")
    @Test
    void expandMountPoint() {
        wildFly.start();
        wildFly.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить WildFly OLD")
    @Test
    void restart() {
        wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
        wildFly.restart();
    }

    @Order(3)
    @DisplayName("Выключить WildFly OLD")
    @Test
    void stopSoft() {
        wildFly.stopSoft();
        wildFly.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию WildFly OLD")
    @Test
    void resize() {
        wildFly.stopHard();
        try {
            wildFly.resize();
        } finally {
            wildFly.start();
        }
    }

    @Order(5)
    @DisplayName("Включить WildFly OLD")
    @Test
    void start() {
        wildFly.stopHard();
        wildFly.start();
    }

    @Order(6)
    @DisplayName("Обновить сертификаты WildFly OLD")
    @Test
    void updateCerts() {
        wildFly.updateCerts();
    }

    @Order(7)
    @DisplayName("Выключить принудительно WildFly OLD")
    @Test
    void stopHard() {
        wildFly.stopHard();
    }
}
