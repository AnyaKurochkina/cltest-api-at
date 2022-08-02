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
@Feature("PostgresSQL Cluster Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresSqlClusterAstra"), @Tag("prod")})
public class PostgresSQLClusterAstraTest extends Tests {
//    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
    static final String productName = "PostgreSQL Cluster Astra Linux";
    static final String dbName = "db_name";

    @TmsLink("810039")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresSQLCluster product) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("810032")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.expandMountPoint();
        }
    }

    @TmsLink("810040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
        }
    }

    @TmsLink("810045")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить подключение к БД PostgresSQLCluster {0}")
    void checkBdConnection(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.checkConnection(dbName);
        }
    }

    @TmsLink("810041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("testchelik1", "user", dbName);
        }
    }

    @TmsLink("810034")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("chelikforreset1", "user", dbName);
            postgres.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("810042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.createDbmsUser("chelikforremove2", "user", dbName);
            postgres.removeDbmsUser("chelikforremove2", dbName);
//            postgres.removeDb("cached_bd");
        }
    }

    @TmsLink("810044")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("810043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb(dbName);
            postgres.removeDb(dbName);
        }
    }

    @TmsLink("810035")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        product.setProductName(productName);
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.restart();
        }
    }

    @TmsLink("810038")
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

    @TmsLinks({@TmsLink("810036"),@TmsLink("810037")})
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

    @TmsLink("810033")
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
