package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Redis;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Redis")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("redis"), @Tag("prod")})
public class RedisTest extends Tests {

    @TmsLink("377701")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Redis product) {
        //noinspection EmptyTryBlock
        try (Redis redis = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377694")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.expandMountPoint();
        }
    }

    @TmsLink("377702")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @TmsLink("377697")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.restart();
        }
    }

    @TmsLink("377700")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopSoft();
            redis.start();
        }
    }

    @TmsLink("653499")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resize(redis.getMaxFlavor());
        }
    }

    @TmsLink("377699")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopHard();
            redis.start();
        }
    }

    @TmsLink("377698")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopHard();
            redis.start();
        }
    }

    @TmsLink("377695")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
