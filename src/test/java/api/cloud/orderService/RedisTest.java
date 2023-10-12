package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Redis;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Redis (Astra)")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("redis"), @Tag("prod")})
public class RedisTest extends Tests {

    @TmsLink("795817")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(Redis product) {
        //noinspection EmptyTryBlock
        try (Redis redis = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("795816")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Расширить {0}")
    void expandMountPoint(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.expandMountPoint();
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить ОС {0}")
    void checkActions(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            Assertions.assertTrue(redis.isActionExist("update_os_standalone"));
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Обновить ОС {0}")
    void updateOsStandalone(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.updateOsStandalone();
        }
    }

    @TmsLink("795825")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Сбросить пароль {0}")
    void resetPassword(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resetPassword();
        }
    }

    @Disabled
    @TmsLink("795822")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезагрузить {0}")
    void restart(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.restart();
        }
    }

    @Disabled
    @TmsLink("795823")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить {0}")
    void stopSoft(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopSoft();
            redis.start();
        }
    }

    @TmsLink("795820")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить конфигурацию {0}")
    void resize(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.resize(redis.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("795818"),@TmsLink("795821")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить принудительно/Включить {0}")
    void stopHard(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.stopHard();
            redis.start();
        }
    }

    @TmsLink("795824")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверка создания {0}")
    void checkConnect(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.checkConnect();
        }
    }

    @TmsLink("1654563")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить парметр notify-keyspace-events {0}")
    void changeNotifyKeyspaceEvents(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.changeNotifyKeyspaceEvents("KEA");
        }
    }

    @TmsLink("795819")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(Redis product) {
        try (Redis redis = product.createObjectExclusiveAccess()) {
            redis.deleteObject();
        }
    }
}
