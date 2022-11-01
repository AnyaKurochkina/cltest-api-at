package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.Redis;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("redisAstra"), @Tag("prod")})
public class RedisAstraTest extends Tests {

    @TmsLink("795817")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Redis product) {
        //noinspection EmptyTryBlock
        try (Redis redis = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("795816")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.expandMountPoint();
        }
    }

    @TmsLink("795825")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @TmsLink("795822")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.restart();
        }
    }

    @Disabled
    @TmsLink("795823")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopSoft();
            redis.start();
        }
    }

    @TmsLink("795820")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resize(redis.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("795818"),@TmsLink("795821")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopHard();
            redis.start();
        }
    }

    @TmsLink("795824")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnect(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkConnect();
        }
    }

    @TmsLink("795819")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
