package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
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
    final String productName = "Redis";

    @TmsLink("377701")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Redis product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (Redis redis = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("377694")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.expandMountPoint();
        }
    }

    @TmsLink("377702")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @TmsLink("377697")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.restart();
        }
    }

    @TmsLink("377700")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Redis product) {
        product.setProductName(productName);
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
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resize(redis.getMaxFlavor());
        }
    }

    @TmsLinks({@TmsLink("377698"),@TmsLink("377699")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopHard();
            redis.start();
        }
    }

    @TmsLink("173242")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnect(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkConnect();
        }
    }

    @TmsLink("377695")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Redis product) {
        product.setProductName(productName);
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
