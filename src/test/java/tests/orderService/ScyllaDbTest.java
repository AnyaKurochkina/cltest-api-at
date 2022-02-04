package tests.orderService;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ElasticsearchOpensearchCluster;
import models.orderService.products.PostgreSQL;
import models.orderService.products.ScyllaDb;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ScyllaDb")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("scylladb"), @Tag("prod")})
public class ScyllaDbTest extends Tests {
    private final String password = "pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ";

    @TmsLink("622616")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ScyllaDb product) {
        //noinspection EmptyTryBlock
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("622619")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.expandMountPoint();
        }
    }

    @TmsLink("622622")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
        }
    }

    @TmsLink("654176")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверить конфигурацию {0}")
    void refreshVmConfig(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.refreshVmConfig();
        }
    }


    @TmsLink("654178")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Присвоить права доступа пользователю {0}")
    void addPermissionsUser(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik3", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik3", "admin");
        }
    }

    @TmsLink("654179")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить права доступа пользователю {0}")
    void removePermissionsUser(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik4", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik4", "admin");
            scyllaDb.removePermissionsUser("cachedbd", "chelik4");
        }
    }

    @TmsLink("622629")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik1", password, "admin");
        }
    }

    @TmsLink("622614")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikforreset1", password, "admin");
            String newPassword = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
            scyllaDb.resetPassword("chelikforreset1", newPassword);
        }
    }

    @TmsLink("622631")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikdelete2", password, "admin");
            scyllaDb.removeDbmsUser("chelikdelete2");
        }
    }

    @TmsLink("622618")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.restart();
        }
    }

    @TmsLink("622634")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("bdfordelete");
            scyllaDb.removeDb("bdfordelete");
        }
    }

    @TmsLink("622621")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.stopSoft();
            scyllaDb.start();
        }
    }

    @TmsLink("622624")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.stopHard();
            scyllaDb.start();
        }
    }

    @TmsLink("622625")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.checkPreconditionStatusProduct(ProductStatus.CREATED);
            scyllaDb.stopHard();
            scyllaDb.start();
        }
    }

    @TmsLink("622626")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ScyllaDb product) {
        try (ScyllaDb scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.deleteObject();
        }
    }
}
