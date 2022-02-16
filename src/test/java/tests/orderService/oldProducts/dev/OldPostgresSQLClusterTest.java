package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.PostgresSQLCluster;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("PostgresSQL Cluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgressqlcluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgresSQLClusterTest extends Tests {

    final PostgresSQLCluster postgres = PostgresSQLCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("9c97f55c-ab21-4724-be4d-cb90b8a815c6")
            .orderId("b8877469-5751-4ca5-acf2-86a99695f240")
            .productName("PostgreSQL Cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить PostgreSQLCluster OLD")
    @Test
    void expandMountPoint() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Создать бд PostgreSQLCluster OLD")
    @Test
    void createDb() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("dbcreate1");

        postgres.removeDb("dbcreate1");
    }

    @Order(3)
    @DisplayName("Проверить подключение к БД PostgresSQLCluster OLD")
    @Test
    void checkBdConnection() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("bd_for_check_connect");
        postgres.checkConnection(postgres.getDbUrl(), postgres.getDbAdminUser(), postgres.getDbAdminPass());
        postgres.removeDb("cached_bd");
    }

    @Order(4)
    @DisplayName("Создать пользователя PostgreSQLCluster OLD")
    @Test
    void createDbmsUser() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("dbforuser2");
        postgres.createDbmsUser("testchelik1", "user", "dbforuser2");

        postgres.removeDb("dbforuser2");
        postgres.removeDbmsUser("testchelik1", "dbforuser2");
    }

    @Order(5)
    @DisplayName("Сбросить пароль пользователя PostgreSQLCluster OLD")
    @Test
    void resetPassword() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("createdbforreset3");
        postgres.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgres.resetPassword("chelikforreset1");

        postgres.removeDb("createdbforreset3");
        postgres.removeDbmsUser("chelikforreset1", "createdbforreset3");
    }

    @Tag("remove")
    @Order(6)
    @DisplayName("Удалить пользователя PostgreSQLCluster OLD")
    @Test
    void removeDbmsUser() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("createdbforremove4");
        postgres.createDbmsUser("chelikforremove2", "user", "createdbforremove4");
        postgres.removeDbmsUser("chelikforremove2", "createdbforremove4");

        postgres.removeDb("createdbforremove4");
    }

    @Order(7)
    @DisplayName("Сбросить пароль владельца PostgreSQLCluster OLD")
    @Test
    void resetDbOwnerPassword() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("createdbforreset8");
        postgres.resetDbOwnerPassword("createdbforreset8");

        postgres.removeDb("createdbforreset8");
    }

    @Order(8)
    @DisplayName("Удалить бд PostgreSQLCluster OLD")
    @Test
    void removeDb() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.createDb("createdbforremove5");
        postgres.removeDb("createdbforremove5");
    }

    @Order(9)
    @DisplayName("Перезагрузить PostgreSQLCluster OLD")
    @Test
    void restart() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.restart();
    }

    @Order(10)
    @DisplayName("Выключить PostgreSQLCluster OLD")
    @Test
    void stopSoft() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.stopSoft();
    }

    @Order(11)
    @DisplayName("Включить PostgreSQLCluster OLD")
    @Test
    void start() {
        if (postgres.productStatusIs(STARTED)) {
            postgres.stopHard();
        }
        postgres.start();
    }

    @Order(12)
    @DisplayName("Выключить принудительно PostgreSQLCluster OLD")
    @Test
    void stopHard() {
        if (postgres.productStatusIs(STOPPED)) {
            postgres.start();
        }
        postgres.stopHard();
    }
}
