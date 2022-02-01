package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgreSQL;
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

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgreSQL product) {
        //noinspection EmptyTryBlock
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {}
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.expandMountPoint();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd");
            postgreSQL.createDbmsUser("chelik1", "user", "cached_bd");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd");
            postgreSQL.createDbmsUser("chelikforreset1", "user", "cached_bd");
            postgreSQL.resetPassword("chelikforreset1");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd");
            postgreSQL.resetDbOwnerPassword("cached_bd");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.createDb("cached_bd");
            postgreSQL.createDbmsUser("chelikforreset2", "user", "cached_bd");
            postgreSQL.removeDbmsUser("chelikforreset2", "cached_bd");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.restart();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("cached_bd");
            postgreSQL.removeDb("cached_bd");
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.stopSoft();
            postgreSQL.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.resize(postgreSQL.getMaxFlavor());
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.deleteObject();
        }
    }
}
