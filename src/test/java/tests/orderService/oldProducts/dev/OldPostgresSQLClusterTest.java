package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.PostgresSQLCluster;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("PostgresSQL Cluster OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgressqlcluster"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgresSQLClusterTest extends Tests {

    PostgresSQLCluster postgres = PostgresSQLCluster.builder()
            .projectId("proj-67nljbzjtt")
            .productId("9c97f55c-ab21-4724-be4d-cb90b8a815c6")
            .orderId("985858eb-7d78-4ce5-9134-20cdb610be5e")
            .productName("PostgreSQL Cluster")
            .build();

    @Order(1)
    @DisplayName("Расширить PostgreSQLCluster OLD")
    @Test
    void expandMountPoint() {
        try {
            postgres.start();
        }catch (Throwable t) {
            t.getStackTrace();
        } finally {
            postgres.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Создать бд PostgreSQLCluster OLD")
    @Test
    void createDb() {
        postgres.createDb("dbcreate1");

        postgres.removeDb("dbcreate1");
    }

    @Order(3)
    @DisplayName("Создать пользователя PostgreSQLCluster OLD")
    @Test
    void createDbmsUser() {
        postgres.createDb("dbforuser2");
        postgres.createDbmsUser("testchelik1", "user", "dbforuser2");

        postgres.removeDb("dbforuser2");
        postgres.removeDbmsUser("testchelik1", "dbforuser2");
    }

    @Order(4)
    @DisplayName("Сбросить пароль пользователя PostgreSQLCluster OLD")
    @Test
    void resetPassword() {
        postgres.createDb("createdbforreset3");
        postgres.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgres.resetPassword("chelikforreset1");

        postgres.removeDb("createdbforreset3");
        postgres.removeDbmsUser("chelikforreset1", "createdbforreset3");
    }

    @Tag("remove")
    @Order(5)
    @DisplayName("Удалить пользователя PostgreSQLCluster OLD")
    @Test
    void removeDbmsUser() {
        postgres.createDb("createdbforremove4");
        postgres.createDbmsUser("chelikforremove2", "user", "createdbforremove4");
        postgres.removeDbmsUser("chelikforremove2", "createdbforremove4");

        postgres.removeDb("createdbforremove4");
    }

    @Order(6)
    @DisplayName("Сбросить пароль владельца PostgreSQLCluster OLD")
    @Test
    void resetDbOwnerPassword() {
        postgres.createDb("createdbforreset8");
        postgres.resetDbOwnerPassword("createdbforreset8");

        postgres.removeDb("createdbforreset8");
    }

    @Order(7)
    @DisplayName("Удалить бд PostgreSQLCluster OLD")
    @Test
    void removeDb() {
        postgres.createDb("createdbforremove5");
        postgres.removeDb("createdbforremove5");
    }

    @Order(8)
    @DisplayName("Перезагрузить PostgreSQLCluster OLD")
    @Test
    void restart() {
        postgres.restart();
    }

    @Order(9)
    @DisplayName("Выключить PostgreSQLCluster OLD")
    @Test
    void stopSoft() {
        postgres.stopSoft();
        postgres.start();
    }

    @Order(10)
    @DisplayName("Включить PostgreSQLCluster OLD")
    @Test
    void start() {
        postgres.stopHard();
        postgres.start();
    }

    @Order(11)
    @DisplayName("Выключить принудительно PostgreSQLCluster OLD")
    @Test
    void stopHard() {
        postgres.stopHard();
    }
}
