package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Windows;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("Windows")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("windows"), @Tag("prod")})
public class WindowsTest extends Tests {

    @TmsLink("377721")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Windows product) {
        //noinspection EmptyTryBlock
        try (Windows windows = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377724")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить диск {0}")
    void addDisk(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("K");
        }
    }

    @TmsLink("377715")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить диск {0}")
    void expandMountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("K");
            windows.expandMountPoint("K");
        }
    }

    @TmsLink("694091")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить диск {0}")
    void deleteMountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("K");
            windows.unmountDisk("K");
            windows.deleteDisk("K");
        }
    }

    @TmsLink("694092")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Подключить диск {0}")
    void mountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("S");
            windows.unmountDisk("S");
            windows.mountDisk("S");
        }
    }

    @TmsLink("694093")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Отключить диск {0}")
    void unmountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("T");
            windows.unmountDisk("T");
        }
    }

//    @TmsLink("377717")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Перезагрузить {0}")
//    void restart(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.restart();
//        }
//    }
//
//    @TmsLink("377720")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Выключить {0}")
//    void stopSoft(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.stopSoft();
//            windows.start();
//        }
//    }
//
//    @TmsLink("377722")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Изменить конфигурацию {0}")
//    void resize(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.stopHard();
//            try {
//                windows.resize(windows.getMaxFlavor());
//            } finally {
//                windows.start();
//            }
//        }
//    }
//
//    @TmsLink("654229")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Проверить конфигурацию {0}")
//    void refreshVmConfig(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.refreshVmConfig();
//        }
//    }
//
//    @TmsLink("377719")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Включить {0}")
//    void start(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.stopHard();
//            windows.start();
//        }
//    }
//
//    @TmsLink("377718")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Выключить принудительно {0}")
//    void stopHard(Windows product) {
//        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.stopHard();
//            windows.start();
//        }
//    }

    @TmsLink("377716")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.deleteObject();
        }
    }
}
