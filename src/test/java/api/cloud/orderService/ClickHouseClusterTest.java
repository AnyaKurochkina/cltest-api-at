package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouseCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("ClickHouseCluster Cluster")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("clickhouse_cluster"), @Tag("prod")})
public class ClickHouseClusterTest extends Tests {

    @TmsLink("1161960")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ClickHouseCluster product) {
        //noinspection EmptyTryBlock
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
        }
    }

    @TmsLink("1161955")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.expandMountPoint();
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1161958"), @TmsLink("1161966")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "ТУЗ локальные, создание/удаление локальной УЗ {0}")
    void createUserAccount(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.createUserAccount("test", "qBZ7hUOija_gSSyOEt7rA-.nk-x.R4UzdJvrv8y1JJk.lpp");
            cluster.deleteUserAccount("test");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1161962"), @TmsLink("1161959")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "ТУЗ AD, добавление/удаление {0}")
    void addUserAd(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.addUserAd("user1");
            cluster.deleteUserAd("user1");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1161961"), @TmsLink("1161963")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Группы пользователей AD, добавление/удаление {0}")
    void addGroupAd(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(cluster.getProjectId()).build().createObject();
            cluster.deleteGroupAd(accessGroup.getPrefixName());
            cluster.addGroupAd(accessGroup.getPrefixName());
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1161967"), @TmsLink("1161957")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Группы прикладных администраторов AD, добавление/удаление {0}")
    void addGroupAdmin(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(cluster.getProjectId()).build().createObject();
            cluster.deleteGroupAdmin(accessGroup.getPrefixName());
            cluster.addGroupAdmin(accessGroup.getPrefixName());
        }
    }

    @TmsLink("1161956")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль пользователя {0}")
    void resetPasswordCustomer(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.resetPasswordCustomer();
        }
    }

    @Disabled
    @TmsLink("1161968")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.restart();
        }
    }

    @TmsLink("1161965")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnectDb(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.checkConnectDb(0);
            cluster.checkConnectDb(1);
        }
    }

    @Disabled
    @TmsLink("1161954")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.stopSoft();
            cluster.start();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("1161964"), @TmsLink("1161952")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.stopHard();
            cluster.start();
        }
    }

    @TmsLink("1212279")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "AD Проверка создания ВМ {0}")
    void checkCreate(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            assertContains(cluster.executeSsh("sudo id"), "root");
        }
    }

    @TmsLink("1654650")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Обновить информацию о сертификатах {0}")
    void updateCertsInfo(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.certsInfo();
        }
    }

    @TmsLink("1161953")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ClickHouseCluster product) {
        try (ClickHouseCluster cluster = product.createObjectExclusiveAccess()) {
            cluster.deleteObject();
        }
    }
}
