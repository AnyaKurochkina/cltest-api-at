package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Windows;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

@Epic("Продукты")
@Feature("Windows")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("windows"), @Tag("prod")})
@DisabledIfEnv("ift")
public class WindowsTest extends Tests {

    @TmsLinks({@TmsLink("377721"), @TmsLink("470095")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Создать {0}")
    void create(Windows product) {
        //noinspection EmptyTryBlock
        try (Windows windows = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377724")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Добавить диск {0}")
    void addDisk(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("I");
        }
    }

    @TmsLink("377715")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Расширить диск {0}")
    void expandMountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("K");
            windows.expandMountPoint("K");
        }
    }

    @TmsLink("694091")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить диск {0}")
    void deleteMountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("L");
            windows.unmountDisk("L");
            windows.deleteDisk("L");
        }
    }

    @TmsLink("694092")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Подключить диск {0}")
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
    @ParameterizedTest(name = "[{index}] Отключить диск {0}")
    void unmountPoint(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.addDisk("T");
            windows.unmountDisk("T");
        }
    }

    @Disabled
    @TmsLink("377717")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Перезагрузить {0}")
    void restart(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.restart();
        }
    }

    @Disabled
    @TmsLink("377720")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить {0}")
    void stopSoft(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.stopSoft();
            windows.start();
        }
    }

    @TmsLink("377722")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Изменить конфигурацию {0}")
    void resize(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
//            windows.stopHard();
//            try {
                windows.resize(windows.getMaxFlavor());
//            } finally {
//                windows.start();
//            }
        }
    }

    @TmsLink("654229")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Проверить конфигурацию {0}")
    void refreshVmConfig(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("377718"),@TmsLink("377719")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Выключить принудительно/Включить {0}")
    void stopHard(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.stopHard();
            windows.start();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("1427063"),@TmsLink("1427065")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Ключ astrom добавить/удалить {0}")
    void astrom(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.astromAdd();
            windows.astromDelete();
        }
    }

    @TmsLink("377716")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{index}] Удалить {0}")
    @MarkDelete
    void delete(Windows product) {
        try (Windows windows = product.createObjectExclusiveAccess()) {
            windows.deleteObject();
        }
    }
}
