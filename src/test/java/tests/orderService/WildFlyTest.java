package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Nginx;
import models.orderService.products.WildFly;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import static models.orderService.interfaces.IProduct.*;

@Epic("Продукты")
@Feature("WildFly")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildfly")})
public class WildFlyTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(WildFly product) {
        WildFly wildFly = product.createObjectExclusiveAccess();
        wildFly.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.expandMountPoint(EXPAND_MOUNT_POINT);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.restart(RESTART);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopSoft(STOP_SOFT);
            wildFly.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.resize(RESIZE);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard(STOP_HARD);
            wildFly.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard(STOP_HARD);
            wildFly.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
