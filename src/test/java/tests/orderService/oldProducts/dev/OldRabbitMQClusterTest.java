package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.RabbitMQCluster;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("RabbitMQCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_rabbitmqcluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRabbitMQClusterTest extends Tests {

    final RabbitMQCluster rabbit = RabbitMQCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("cee004ec-136d-4605-98e1-da4dac466151")
            .orderId("108f0794-83fd-476f-a729-46a3befe027c")//108f0794-83fd-476f-a729-46a3befe027c
            .productName("RabbitMQCluster")
            .build();

    @Order(1)
    @DisplayName("Расширить RabbitMQCluster OLD")
    @Test
    void expandMountPoint() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить RabbitMQCluster OLD")
    @Test
    void restart() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.restart();
    }

    @Order(3)
    @DisplayName("Выключить RabbitMQCluster OLD")
    @Test
    void stopSoft() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.stopSoft();
    }

    @Order(4)
    @DisplayName("Создать пользователя RabbitMQCluster OLD")
    @Test
    void createUser() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.rabbitmqCreateUser();
    }

    @Order(5)
    @DisplayName("Включить RabbitMQCluster OLD")
    @Test
    void start() {
        if (rabbit.productStatusIs(STARTED)) {
            rabbit.stopHard();
        }
        rabbit.start();
    }

    @Order(6)
    @DisplayName("Обновить сертификаты RabbitMQCluster OLD")
    @Test
    void updateCerts() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.updateCerts();
    }

    @Order(7)
    @DisplayName("Выключить принудительно RabbitMQCluster OLD")
    @Test
    void stopHard() {
        if (rabbit.productStatusIs(STOPPED)) {
            rabbit.start();
        }
        rabbit.stopHard();
    }
}
