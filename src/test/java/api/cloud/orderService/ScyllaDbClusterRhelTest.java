package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.orderService.products.ScyllaDbCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

@Epic("Продукты")
@Feature("ScyllaDb Cluster Rhel")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("scylladb_cluster"), @Tag("prod")})
@Disabled
public class ScyllaDbClusterRhelTest extends Tests {
    private final String password = "pXiAR8rrvIfYM1.BSOt.d-ZWyWb7oymoEstQ";
    private static final String productName = "ScyllaDB Cluster RHEL";

    @TmsLink("1349497")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        //noinspection EmptyTryBlock
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("1349503")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка подключения {0}")
    void checkConnect(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik5", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik5");
            scyllaDb.checkConnectDb("cachedbd", "chelik5", password);
        }
    }

    @TmsLink("1349510")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить БД {0}")
    void createDb(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
        }
    }


    @TmsLink("1349509")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Присвоить права доступа пользователю {0}")
    void addPermissionsUser(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik3", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik3");
        }
    }

    @TmsLink("1349511")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить права доступа пользователю {0}")
    void removePermissionsUser(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik4", password, "admin");
            scyllaDb.addPermissionsUser("cachedbd", "chelik4");
            scyllaDb.removePermissionsUser("cachedbd", "chelik4");
        }
    }

    @TmsLink("1349501")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Добавить пользователя {0}")
    void createDbmsUser(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelik1", password, "admin");
        }
    }

    @TmsLink("1349506")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль {0}")
    void resetPassword(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikforreset1", password, "admin");
            String newPassword = "Wx1QA9SI4AzW6AvJZ3sxf7-jyQDazVkouHvcy6UeLI-Gt";
            scyllaDb.resetPassword("chelikforreset1", newPassword);
        }
    }

    @TmsLink("1349499")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить пользователя {0}")
    void removeDbmsUser(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("cachedbd");
            scyllaDb.createDbmsUser("chelikdelete2", password, "admin");
            scyllaDb.removeDbmsUser("chelikdelete2");
        }
    }

    @TmsLink("1349504")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить БД {0}")
    void removeDb(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.createDb("bdfordelete");
            scyllaDb.removeDb("bdfordelete");
        }
    }

    @TmsLink("1349505")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(ScyllaDbCluster product, Integer num) {
        product.setProductName(productName);
        try (ScyllaDbCluster scyllaDb = product.createObjectExclusiveAccess()) {
            scyllaDb.deleteObject();
        }
    }
}
