package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.Astra;
import models.cloud.orderService.products.Ubuntu;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

@Epic("Продукты")
@Feature("Ubuntu")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("ubuntu"), @Tag("prod")})
public class UbuntuTest extends Tests {

    @TmsLink("391696")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Ubuntu product) {
        //noinspection EmptyTryBlock
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("391706")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.expandMountPoint();
        }
    }

    @Disabled
    @TmsLink("391692")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.restart();
        }
    }

    @Disabled
    @TmsLink("391695")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.stopSoft();
            ubuntu.start();
        }
    }

    @TmsLink("391697")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.resize(ubuntu.getMaxFlavor());
        }
    }

    @TmsLink("654208")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.refreshVmConfig();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("391693"), @TmsLink("391694")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.stopHard();
            ubuntu.start();
        }
    }

    @TmsLink("1090961")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка прав у ролей пользователя {0}")
    void checkCreate(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.executeCheckUseSsh();
        }
    }

    @TmsLink("391691")
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
