package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgresSQLCluster;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgresSQL Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgressqlcluster")})
public class PostgresSQLClusterTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresSQLCluster product) {
        PostgresSQLCluster postgres = product.createObjectExclusiveAccess();
        postgres.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.createDb("dbcreate1");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.createDb("dbforuser2");
            postgres.createDbmsUser("testchelik1", "user", "dbforuser2");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("createdbforreset3");
            postgres.createDbmsUser("chelikforreset1", "user","createdbforreset3");
            postgres.resetPassword("chelikforreset1");
        }
    }

    @Tag("remove")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("createdbforremove4");
            postgres.createDbmsUser("chelikforremove2", "user", "createdbforremove4");
            postgres.removeDbmsUser("chelikforremove2", "createdbforremove4");
            postgres.removeDb("createdbforremove4");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetDbOwnerPassword(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.resetDbOwnerPassword();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("createdbforremove5");
            postgres.removeDb("createdbforremove5");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.stopSoft();
            postgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.stopHard();
            postgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgres.stopHard();
            postgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(PostgresSQLCluster product) {
        try (PostgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.deleteObject();
        }
    }
}
