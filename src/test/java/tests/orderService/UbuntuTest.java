package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Ubuntu;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Ubuntu")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("ubuntu")})
public class UbuntuTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Ubuntu product) {
        Ubuntu ubuntu = product.createObjectExclusiveAccess();
        ubuntu.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopSoft();
            ubuntu.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            try {
                ubuntu.resize();
            } finally {
                ubuntu.start();
            }
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.deleteObject();
        }
    }
}
