package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.RabbitMQCluster;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты")
@Feature("RabbitMQCluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_rabbitmqcluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRabbitMQClusterTest extends Tests {

    final RabbitMQCluster rabbit = RabbitMQCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("cee004ec-136d-4605-98e1-da4dac466151")
            .orderId("43c2f7f3-74e2-4f78-beef-aae28107b6a1")//43c2f7f3-74e2-4f78-beef-aae28107b6a1 создал новый(старый бажный)
            .productName("RabbitMQCluster")
            .build();

    @Order(1)
    @DisplayName("Расширить RabbitMQCluster OLD")
    @Test
    void expandMountPoint() {
        try {
            rabbit.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            rabbit.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Перезагрузить RabbitMQCluster OLD")
    @Test
    void restart() {
        rabbit.restart();
    }

    @Order(3)
    @DisplayName("Выключить RabbitMQCluster OLD")
    @Test
    void stopSoft() {
        rabbit.stopSoft();
        rabbit.start();
    }

    @Order(4)
    @DisplayName("Создать пользователя RabbitMQCluster OLD")
    @Test
    void createUser() {
        rabbit.rabbitmqCreateUser();
    }

    @Order(5)
    @DisplayName("Включить RabbitMQCluster OLD")
    @Test
    void start() {
        rabbit.stopHard();
        rabbit.start();
    }

    @Order(6)
    @DisplayName("Обновить сертификаты RabbitMQCluster OLD")
    @Test
    void updateCerts() {
        rabbit.updateCerts();
    }

    @Order(7)
    @DisplayName("Выключить принудительно RabbitMQCluster OLD")
    @Test
    void stopHard() {
        rabbit.stopHard();
    }
}
