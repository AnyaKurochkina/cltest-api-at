package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.WildFly;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("WildFly")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildfly"), @Tag("prod")})
public class WildFlyTest extends Tests {

    @TmsLink("377474")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(WildFly product) {
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377467")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.expandMountPoint();
        }
    }

    @TmsLink("377470")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.restart();
        }
    }

    @TmsLink("377473")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @TmsLink("377469")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            try {
                wildFly.resize(wildFly.getMaxFlavor());
            } finally {
                wildFly.start();
            }
        }
    }

    @TmsLink("654210")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.refreshVmConfig();
        }
    }

    @TmsLink("377472")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("377471")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("377477")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.checkPreconditionStatusProduct(ProductStatus.CREATED);
            wildFly.updateCerts();
        }
    }

    @TmsLink("377468")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(WildFly product) {
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
