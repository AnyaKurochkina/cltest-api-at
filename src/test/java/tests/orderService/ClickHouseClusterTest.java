package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.orderService.products.ClickHouse;
import models.orderService.products.ClickHouseCluster;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ClickHouseCluster Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("clickhouse_cluster"), @Tag("prod")})
public class ClickHouseClusterTest extends Tests {

//    @TmsLink("377799")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ClickHouseCluster product) {
        //noinspection EmptyTryBlock
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {}
    }

//    @TmsLink("377793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.expandMountPoint();
        }
    }

//    @TmsLink("377689")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Сбросить пароль владельца{0}")
//    void resetPasswordOwner(ClickHouseCluster product) {
//        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
//            cluster.resetPasswordOwner();
//        }
//    }

//    @TmsLink("377689")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Сбросить пароль customer{0}")
//    void resetPasswordCustomer(ClickHouseCluster product) {
//        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
//            cluster.resetPasswordCustomer();
//        }
//    }

//    @TmsLink("377795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.restart();
        }
    }

//    @TmsLink("711827")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnectDb(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.checkConnectDb();
        }
    }

//    @TmsLink("377798")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.stopSoft();
            cluster.start();
        }
    }

//    @TmsLinks({@TmsLink("377796"),@TmsLink("377797")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.stopHard();
            cluster.start();
        }
    }

//    @TmsLink("377794")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.deleteObject();
        }
    }
}
