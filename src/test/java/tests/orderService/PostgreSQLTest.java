package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.PostgreSQL;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgreSQL")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresql"), @Tag("prod")})
public class PostgreSQLTest extends Tests {
    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";

    @TmsLink("377668")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgreSQL product) {
        //noinspection EmptyTryBlock
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377661")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.expandMountPoint();
        }
    }

    @TmsLink("377677")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
        }
    }

    @TmsLink("15778")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка подключения к БД {0}")
    void checkDbConnection(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            String db = "bd_for_check_connection";
            postgreSQL.createDb(db, adminPassword);
            postgreSQL.checkConnection(db, adminPassword);
            postgreSQL.removeDb(db);
        }
    }

    @TmsLink("377678")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
            postgreSQL.createDbmsUser("chelik1", "user", "cached_bd");
        }
    }

    @TmsLink("377663")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
            postgreSQL.createDbmsUser("chelikforreset1", "user", "cached_bd");
            postgreSQL.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("461766")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
            postgreSQL.resetDbOwnerPassword("cached_bd");
        }
    }

    @TmsLink("377683")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
            postgreSQL.createDbmsUser("chelikforreset2", "user", "cached_bd");
            postgreSQL.removeDbmsUser("chelikforreset2", "cached_bd");
        }
    }

    @TmsLink("377664")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.restart();
        }
    }

    @TmsLink("392130")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd", adminPassword);
            postgreSQL.removeDb("cached_bd");
        }
    }

    @TmsLink("377667")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopSoft();
            postgreSQL.start();
        }
    }

    @TmsLink("377669")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.resize(postgreSQL.getMaxFlavor());
        }
    }

    @TmsLinks({@TmsLink("377665"),@TmsLink("377666")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

    @TmsLink("705397")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить default_transaction_isolation {0}")
    void updateDti(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateDti("REPEATABLE READ");
        }
    }

    @TmsLink("705398")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить max_connections {0}")
    void updateMaxConnections(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.updateMaxConnections("OLTP", 99);
        }
    }

    @TmsLink("377662")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.deleteObject();
        }
    }
}
