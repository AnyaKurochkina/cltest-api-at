package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Redis;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STOPPED;
import static models.orderService.interfaces.ProductStatus.STARTED;

@Epic("Старые продукты DEV")
@Feature("Redis OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_redis"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldRedisTest extends Tests {

    final Redis redis = Redis.builder()
            .projectId("proj-67nljbzjtt")
            .productId("6662a03d-20ce-4a83-a684-ddec48393516")
            .orderId("ad4f9bf5-ca89-4d20-bc98-b29644a9fd40")//b29c6a04-c92f-4b52-90c1-4172ae93fe19 создал новый(старый бажный)
            .productName("Redis")
            .build();

    @Order(1)
    @DisplayName("Расширить Redis OLD")
    @Test
    void expandMountPoint() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Сбросить пароль Redis OLD")
    @Test
    void resetPassword() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.resetPassword();
    }

    @Order(3)
    @DisplayName("Перезагрузить Redis OLD")
    @Test
    void restart() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.restart();
    }

    @Order(4)
    @DisplayName("Выключить Redis OLD")
    @Test
    void stopSoft() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.stopSoft();
    }

    @Order(5)
    @DisplayName("Изменить конфигурацию Redis OLD")
    @Test
    void resize() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.resize(redis.getMaxFlavor());
        redis.resize(redis.getMinFlavor());
    }

    @Order(6)
    @DisplayName("Включить Redis OLD")
    @Test
    void start() {
        if (redis.productStatusIs(STARTED)) {
            redis.stopHard();
        }
        redis.start();
    }

    @Order(7)
    @DisplayName("Выключить принудительно Redis OLD")
    @Test
    void stopHard() {
        if (redis.productStatusIs(STOPPED)) {
            redis.start();
        }
        redis.stopHard();
    }
}
