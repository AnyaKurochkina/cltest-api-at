package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.PostgreSQL;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgreSQL Astra")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresql_astra"), @Tag("prod")})
public class PostgreSQLAstraTest extends Tests {
    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";
    static final String dbName = "cached_bd";

    @TmsLink("1057046")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgreSQL product) {
        //noinspection EmptyTryBlock
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("1057048")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.expandMountPoint();
        }
    }

    @TmsLink("1057043")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
        }
    }

    @TmsLink("1057047")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка подключения к БД {0}")
    void checkDbConnection(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            String db = "bd_for_check_connection";
            postgreSQL.createNonProd(db, adminPassword);
            postgreSQL.checkConnection(db, adminPassword);
            postgreSQL.removeDb(db);
        }
    }

    @TmsLink("1057037")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.createDbmsUser("chelik1", "user", dbName);
        }
    }

    @TmsLink("1057042")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.createDbmsUser("chelikforreset1", "user", dbName);
            postgreSQL.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("1057039")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.resetDbOwnerPassword(dbName);
        }
    }

    @TmsLink("1057044")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.createDbmsUser("chelikforreset2", "user", dbName);
            postgreSQL.removeDbmsUser("chelikforreset2", dbName);
        }
    }

    @TmsLink("1057050")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.restart();
        }
    }

    @TmsLink("1057052")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.removeDb(dbName);
        }
    }

    @Disabled
    @TmsLink("1057038")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopSoft();
            postgreSQL.start();
        }
    }

    @TmsLink("1057041")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.resize(postgreSQL.getMaxFlavor());
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("1057045"),@TmsLink("1057053")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

    @TmsLink("1057049")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить default_transaction_isolation {0}")
    void updateDti(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateDti("REPEATABLE READ");
        }
    }

    @TmsLink("1057040")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить max_connections {0}")
    void updateMaxConnections(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateMaxConnections("OLTP", 99);
        }
    }

    @TmsLinks({@TmsLink("1116377"),@TmsLink("1116378")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Назначить/Убрать предел подключений {0}")
    void setConnLimit(PostgreSQL product) {
        Assumptions.assumeTrue("LT".equalsIgnoreCase(product.getEnv()), "Тест включен только для среды LT");
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createNonProd(dbName, adminPassword);
            postgreSQL.setConnLimit(dbName, 20);
            postgreSQL.removeConnLimit(dbName);
        }
    }

    @TmsLink("1057051")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.deleteObject();
        }
    }
}
