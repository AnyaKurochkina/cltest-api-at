package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.RedisSentinel;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Redis Sentinel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("redis_sentinel"), @Tag("prod")})
public class RedisSentinelTest extends Tests {

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(RedisSentinel product) {
        //noinspection EmptyTryBlock
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(RedisSentinel product) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnect(RedisSentinel product) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.checkConnect();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить параметр notify-keyspace-events {0}")
    void changeNotifyKeyspaceEvents(RedisSentinel product) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.changeNotifyKeyspaceEvents("KEA");
        }
    }

    @TmsLink("")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(RedisSentinel product) {
        try (RedisSentinel redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
