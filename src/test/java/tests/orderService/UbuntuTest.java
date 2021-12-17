package tests.orderService;

import core.helper.MarkDelete;
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
@Tags({@Tag("regress"), @Tag("orders"), @Tag("ubuntu"), @Tag("prod")})
public class UbuntuTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Ubuntu product) {
        //noinspection EmptyTryBlock
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopSoft();
            ubuntu.start();
        }
    }

    @Tag("actions")
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

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.deleteObject();
        }
    }
}
