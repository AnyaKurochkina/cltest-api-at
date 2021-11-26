package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Redis;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Redis OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_redis"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRedisTest extends Tests {

    Redis redis = Redis.builder()
            .projectId("proj-67nljbzjtt")
            .productId("6662a03d-20ce-4a83-a684-ddec48393516")
            .orderId("b29c6a04-c92f-4b52-90c1-4172ae93fe19")//b29c6a04-c92f-4b52-90c1-4172ae93fe19 создал новый(старый бажный)
            .productName("Redis")
            .build();

    @Order(1)
    @DisplayName("Расширить Redis OLD")
    @Test
    void expandMountPoint() {
        redis.start();
        redis.expandMountPoint();
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
        redis.resize();
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
