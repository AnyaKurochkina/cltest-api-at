package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.ApacheKafka;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("ApacheKafka OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_apachekafka"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldApacheKafkaTest extends Tests {

    ApacheKafka kafka = ApacheKafka.builder()
            .projectId("proj-67nljbzjtt")
            .productId("3b3807a6-9ad0-4ca6-930a-a37efffcc605")
            .orderId("70212faf-5d9f-40bc-b7b1-9c32ae29d721")
            .productName("ApacheKafka")
            .build();

    @Order(1)
    @DisplayName("Расширить ApacheKafka OLD")
    @Test
    void expandMountPoint() {
        kafka.start();
        kafka.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить ApacheKafka OLD")
    @Test
    void restart() {
        kafka.restart();
    }

    @Order(3)
    @DisplayName("Выключить ApacheKafka OLD")
    @Test
    void stopSoft() {
        kafka.stopSoft();
        kafka.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию ApacheKafka OLD")
    @Test
    void resize() {
        kafka.stopHard();
        try {
            kafka.resize();
        } finally {
            kafka.start();
        }
    }

    @Order(5)
    @DisplayName("Включить ApacheKafka OLD")
    @Test
    void start() {
        kafka.stopHard();
        kafka.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно ApacheKafka OLD")
    @Test
    void stopHard() {
        kafka.stopHard();
    }
}
