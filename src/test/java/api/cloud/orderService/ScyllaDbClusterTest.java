package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;

import models.cloud.orderService.products.ScyllaDbCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("ScyllaDb Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("scylladb_cluster"), @Tag("prod")})
public class ScyllaDbClusterTest extends Tests {
    private final String password = "pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ";

    @TmsLink("1349516")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ScyllaDbCluster product) {
        //noinspection EmptyTryBlock
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("1349521")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка подключения {0}")
    void checkConnect(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik5", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik5");
            scyllaDb.checkConnectDb("cachedbd", "chelik5", password);
        }
    }

    @TmsLink("1349517")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
        }
    }


    @TmsLink("1349515")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Присвоить права доступа пользователю {0}")
    void addPermissionsUser(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik3", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik3");
        }
    }

    @TmsLink("1349514")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить права доступа пользователю {0}")
    void removePermissionsUser(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik4", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik4");
            scyllaDb.removePermissionsUser("cachedbd", "chelik4");
        }
    }

    @TmsLink("1349524")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik1", password, "admin");
        }
    }

    @TmsLink("1349527")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikforreset1", password, "admin");
            String newPassword = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
            scyllaDb.resetPassword("chelikforreset1", newPassword);
        }
    }

    @TmsLink("1349520")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikdelete2", password, "admin");
            scyllaDb.removeDbmsUser("chelikdelete2");
        }
    }

    @TmsLink("1349518")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("bdfordelete");
            scyllaDb.removeDb("bdfordelete");
        }
    }

    @TmsLink("1349523")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ScyllaDbCluster product) {
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.deleteObject();
        }
    }
}
