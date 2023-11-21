package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Ubuntu;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("Ubuntu")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("ubuntu"), @Tag("prod")})
public class UbuntuTest extends Tests {

    @TmsLink("391696")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(Ubuntu product, Integer num) {
        //noinspection EmptyTryBlock
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void checkActions(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            Assertions.assertTrue(ubuntu.isActionExist("update_os_vm"));
        }
    }

    @TmsLink("391706")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("391692")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.restart();
        }
    }

    @Disabled
    @TmsLink("391695")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.stopSoft();
            ubuntu.start();
        }
    }

    @TmsLink("391697")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Изменить конфигурацию {0}")
    void resize(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.resize(ubuntu.getMaxFlavorLinuxVm());
        }
    }

    @TmsLink("654208")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверить конфигурацию {0}")
    void refreshVmConfig(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("391693"), @TmsLink("391694")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @TmsLink("1090961")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка прав у ролей пользователя {0}")
    void checkCreate(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkUserGroupBySsh();
        }
    }

    @TmsLink("391691")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(Ubuntu product, Integer num) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.deleteObject();
        }
    }
}
