package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.Windows;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import static models.orderService.interfaces.IProduct.*;
import static models.orderService.products.Windows.ADD_DISK;

@Epic("Продукты")
@Feature("Windows")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("windows")})
public class WindowsTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Windows product) {
        Windows windows = product.createObjectExclusiveAccess();
        windows.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить диск {0}")
    void addDisk(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.addDisk();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить диск {0}")
    void expandMountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.stopSoft();
            windows.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.stopHard();
            windows.resize();
            windows.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.stopHard();
            windows.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.checkPreconditionStatusProduct(ProductStatus.CREATED);
            windows.stopHard();
            windows.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.deleteObject();
        }
    }
}
