package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Redis;
import org.junit.jupiter.api.*;
import tests.Tests;

@Epic("Старые продукты TEST")
@Feature("Redis OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_redis"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRedisTest extends Tests {

    final Redis redis = Redis.builder()
            .projectId("proj-juh8ynkvtn")
            .productId("6662a03d-20ce-4a83-a684-ddec48393516")
            .orderId("af3dd005-a86f-41c2-be43-12b2a21cdac4")
            .productName("Redis")
            .build();

    @Order(1)
    @DisplayName("Расширить Redis OLD")
    @Test
    void expandMountPoint() {
        try {
            redis.start();
        } catch (Throwable t) {
            t.getStackTrace();
        } finally {
            redis.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Сбросить пароль Redis OLD")
    @Test
    void resetPassword() {
        redis.resetPassword();
    }

    @Order(3)
    @DisplayName("Перезагрузить Redis OLD")
    @Test
    void restart() {
        redis.restart();
    }

    @Order(4)
    @DisplayName("Выключить Redis OLD")
    @Test
    void stopSoft() {
        redis.stopSoft();
        redis.start();
    }

    @Order(5)
    @DisplayName("Изменить конфигурацию Redis OLD")
    @Test
    void resize() {
        redis.resize(redis.getMaxFlavor());
    }

    @Order(6)
    @DisplayName("Включить Redis OLD")
    @Test
    void start() {
        redis.stopHard();
        redis.start();
    }

    @Order(7)
    @DisplayName("Выключить принудительно Redis OLD")
    @Test
    void stopHard() {
        redis.stopHard();
    }
}
