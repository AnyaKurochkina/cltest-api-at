package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
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
    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
    static final String productName = "PostgreSQL Cluster";

    @TmsLink("461798")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresSQLCluster product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("461791")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("461800")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
        }
    }

    @TmsLink("413968")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить подключение к БД PostgresSQLCluster {0}")
    void checkBdConnection(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            String db = "bd_for_check_connect";
            postgres.createDb(db, adminPassword);
            postgres.checkConnection(db, adminPassword);
            postgres.removeDb(db);
        }
    }

    @TmsLink("461801")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
            postgres.createDbmsUser("testchelik1", "user", "cached_bd");
        }
    }

    @TmsLink("461793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
            postgres.createDbmsUser("chelikforreset1", "user", "cached_bd");
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("461802")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
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
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
            postgres.resetDbOwnerPassword("cached_bd");
        }
    }

    @TmsLink("461803")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("cached_bd", adminPassword);
            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("461794")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("461797")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.stopSoft();
            postgres.start();
        }
    }

    @TmsLinks({@TmsLink("461795"),@TmsLink("461796")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(PostgresSQLCluster product) {
        product.setProductName(productName);
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
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
