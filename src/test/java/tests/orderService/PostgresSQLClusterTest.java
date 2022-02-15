package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.PostgresSQLCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgresSQL Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgressqlcluster"), @Tag("prod")})
public class PostgresSQLClusterTest extends Tests {

    @TmsLink("461798")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresSQLCluster product) {
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("461791")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("461800")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
        }
    }

    @TmsLink("461801")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
            postgres.createDbmsUser("testchelik1", "user", "cached_bd");
        }
    }

    @TmsLink("461793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
            postgres.createDbmsUser("chelikforreset1", "user","cached_bd");
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("461802")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
            postgres.createDbmsUser("chelikforremove2", "user", "cached_bd");
            postgres.removeDbmsUser("chelikforremove2", "cached_bd");
//            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("461804")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
            postgres.resetDbOwnerPassword("cached_bd");
        }
    }

    @TmsLink("461803")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd");
            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("461794")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("461797")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopSoft();
            postgres.start();
        }
    }

    @TmsLink("461796")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopHard();
            postgres.start();
        }
    }

    @TmsLink("461795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopHard();
            postgres.start();
        }
    }

    @TmsLink("461792")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
