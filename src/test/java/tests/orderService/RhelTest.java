package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Rhel;
import org.junit.Ignore;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import static models.orderService.interfaces.IProduct.*;

@Epic("Продукты")
@Feature("Rhel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("rhel")})
public class RhelTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Rhel product) {
        Rhel rhel = product.createObjectExclusiveAccess();
        rhel.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.expandMountPoint(EXPAND_MOUNT_POINT);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.restart(RESTART);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopSoft(STOP_SOFT);
            rhel.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.checkPreconditionStatusProduct(ProductStatus.CREATED);
            rhel.stopHard(STOP_HARD);
            rhel.resize(RESIZE);
            rhel.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.stopHard(STOP_HARD);
            rhel.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.stopHard(STOP_HARD);
            rhel.start(START);
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted(Rhel.class)
    void delete(Rhel product) {
        try (Rhel rhel = product.createObjectExclusiveAccess()) {
            rhel.deleteObject();
        }
    }
}
