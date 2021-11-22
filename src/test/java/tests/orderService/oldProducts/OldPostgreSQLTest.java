package tests.orderService.oldProducts;

import core.CacheService;
import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.Folder;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgreSQL;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgreSQL old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgresql"), @Tag("prod")})
public class OldPostgreSQLTest extends Tests {

    //TODO: добавить сброс пароля владельца
    protected CacheService cacheService = new CacheService();

    @Test
    void create() {
        PostgreSQL postgreSQL = PostgreSQL.builder()
                .projectId("proj-67nljbzjtt")
                .productId("3b3807a6-9ad0-4ca6-930a-a37efffcc605")
                .orderId("f6dd249c-b124-40b0-a99a-a40e55d5b5ce")
                .build().createObject();
        postgreSQL.createDb("createdb1");
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("createdb1");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("createdbforuser2");
            postgreSQL.createDbmsUser("chelik1", "user", "createdbforuser2");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("createdbforreset3");
            postgreSQL.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
            postgreSQL.resetPassword("chelikforreset1");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("createdbforreset8");
            postgreSQL.resetDbOwnerPassword("createdbforreset8");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.createDb("createdbforremove4");
            postgreSQL.createDbmsUser("chelikforreset2", "user", "createdbforremove4");
            postgreSQL.removeDbmsUser("chelikforreset2", "createdbforremove4");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.createDb("createdbforremove5");
            postgreSQL.removeDb("createdbforremove5");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.stopSoft();
            postgreSQL.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.resize();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgreSQL.stopHard();
            postgreSQL.start();
        }
    }

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
    @Deleted
    void delete(PostgreSQL product) {
        try (PostgreSQL postgreSQL = product.createObjectExclusiveAccess()) {
            postgreSQL.deleteObject();
        }
    }
}
