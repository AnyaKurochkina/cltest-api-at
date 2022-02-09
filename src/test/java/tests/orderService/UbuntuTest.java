package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
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

    @TmsLink("391696")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(Ubuntu product) {
        //noinspection EmptyTryBlock
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("391706")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.expandMountPoint();
        }
    }

    @TmsLink("391692")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.restart();
        }
    }

    @TmsLink("391695")
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

    @TmsLink("391697")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.stopHard();
            try {
                ubuntu.resize(ubuntu.getMaxFlavor());
            } finally {
                ubuntu.start();
            }
        }
    }

    @TmsLink("654208")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(Ubuntu product) {
        try (Ubuntu ubuntu = product.createObjectExclusiveAccess()) {
            ubuntu.checkPreconditionStatusProduct(ProductStatus.CREATED);
            ubuntu.refreshVmConfig();
        }
    }

    @TmsLink("391694")
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

    @TmsLink("391693")
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
