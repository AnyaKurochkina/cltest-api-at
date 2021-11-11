package tests.orderService;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgreSQL;
import models.orderService.products.PostgresPro;
import models.orderService.products.ProstgresSQLCluster;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ProstgresSQL Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("prostgressqlcluster")})
public class ProstgresSQLClusterTest extends Tests {

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ProstgresSQLCluster product) {
        ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess();
        prostgres.close();
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.expandMountPoint();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.createDb("dbcreate");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.createDb("dbforuser");
            prostgres.createDbmsUser("testchelik", "user", "dbforuser");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPassword(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.createDb("createdbforreset");
            prostgres.createDbmsUser("chelikforreset", "user","createdbforreset");
            prostgres.resetPassword();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster postgres = product.createObjectExclusiveAccess()) {
            postgres.createDb("createdbforremove");
            postgres.createDbmsUser("chelikforremove", "user", "createdbforremove");
            postgres.removeDbmsUser("chelikforremove", "createdbforremove");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetDbOwnerPassword(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.resetDbOwnerPassword();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.createDb("createdbforremove1");
            prostgres.removeDb("createdbforremove1");
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.restart();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.stopSoft();
            prostgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.stopHard();
            prostgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.checkPreconditionStatusProduct(ProductStatus.CREATED);
            prostgres.stopHard();
            prostgres.start();
        }
    }

    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @Deleted
    void delete(ProstgresSQLCluster product) {
        try (ProstgresSQLCluster prostgres = product.createObjectExclusiveAccess()) {
            prostgres.deleteObject();
        }
    }
}
