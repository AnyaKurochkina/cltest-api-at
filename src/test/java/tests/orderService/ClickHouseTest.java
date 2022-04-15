package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.ClickHouse;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ClickHouse")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("clickhouse"), @Tag("prod")})
public class ClickHouseTest extends Tests {

    @TmsLink("377799")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ClickHouse product) {
        //noinspection EmptyTryBlock
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.expandMountPoint();
        }
    }

    @TmsLink("377689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца{0}")
    void resetPasswordOwner(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPasswordOwner();
        }
    }

//    @TmsLink("377689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль customer{0}")
    void resetPasswordCustomer(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPasswordCustomer();
        }
    }

    @TmsLink("377795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.restart();
        }
    }

    @TmsLink("711827")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnectDb(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkConnectDb();
        }
    }

    @TmsLink("377798")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopSoft();
            clickHouse.start();
        }
    }

    @TmsLink("377797")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @TmsLink("377796")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @TmsLink("377794")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.deleteObject();
        }
    }
}
