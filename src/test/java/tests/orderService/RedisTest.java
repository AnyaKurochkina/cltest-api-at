package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Redis;
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

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Redis product) {
        //noinspection EmptyTryBlock
        try (Redis redis = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.resetPassword();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.stopSoft();
            redis.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.resize(product.getMaxFlavor());
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.stopHard();
            redis.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkPreconditionStatusProduct(ProductStatus.CREATED);
            redis.stopHard();
            redis.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
