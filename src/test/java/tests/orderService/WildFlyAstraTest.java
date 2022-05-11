package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.WildFly;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("WildFly (Astra)")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("wildflyAstra"), @Tag("prod")})
public class WildFlyAstraTest extends Tests {

    final String productName = "Разработка WildFly Astra";

    @TmsLink("833650")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(WildFly product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("833652")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.expandMountPoint();
        }
    }

    @TmsLink("833651")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.restart();
        }
    }

    @TmsLink("833645")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopSoft();
            wildFly.start();
        }
    }

    @TmsLink("833648")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            try {
                wildFly.resize(wildFly.getMaxFlavor());
            } finally {
                wildFly.start();
            }
        }
    }

    @TmsLink("833647")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.refreshVmConfig();
        }
    }

    @TmsLink("833653")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("833646")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.stopHard();
            wildFly.start();
        }
    }

    @TmsLink("833644")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить сертификаты {0}")
    void updateCerts(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.updateCerts();
        }
    }

    @TmsLink("833649")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(WildFly product) {
        product.setProductName(productName);
        try (WildFly wildFly = product.createObjectExclusiveAccess()) {
            wildFly.deleteObject();
        }
    }
}
