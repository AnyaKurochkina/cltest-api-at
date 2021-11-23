package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgreSQL;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("PostgreSQL old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgresql"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgreSQLTest extends Tests {

    PostgreSQL postgreSQL = PostgreSQL.builder()
            .projectId("proj-67nljbzjtt")
            .productId("3b3807a6-9ad0-4ca6-930a-a37efffcc605")
            .orderId("f6dd249c-b124-40b0-a99a-a40e55d5b5ce")
            .build();

    @Order(1)
    @DisplayName("Расширить")
    @Test
    void expandMountPoint() {
        postgreSQL.start();
        postgreSQL.expandMountPoint();

    }

    @Order(2)
    @DisplayName("Создать БД")
    @Test
    void createDb() {
        postgreSQL.createDb("createdb1");

        postgreSQL.removeDb("createdb1");

    }

    @Order(3)
    @DisplayName("Создать пользователя БД")
    @Test
    void createDbmsUser() {
        postgreSQL.createDb("createdbforuser2");
        postgreSQL.createDbmsUser("chelik1", "user", "createdbforuser2");

        postgreSQL.removeDbmsUser("chelik1", "createdbforuser2");
        postgreSQL.removeDb("createdbforuser2");
    }

    @Order(4)
    @DisplayName("Сбросить пароль пользователя БД")
    @Test
    void resetPassword() {
        postgreSQL.createDb("createdbforreset3");
        postgreSQL.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgreSQL.resetPassword("chelikforreset1");

        postgreSQL.removeDbmsUser("chelikforreset1", "createdbforreset3");
        postgreSQL.removeDb("createdbforreset3");

    }

    @Order(5)
    @DisplayName("Сбросить пароль владельца БД")
    @Test
    void resetDbOwnerPassword() {
        postgreSQL.createDb("createdbforreset8");
        postgreSQL.resetDbOwnerPassword("createdbforreset8");

        postgreSQL.removeDb("createdbforreset8");
    }

    @Order(6)
    @DisplayName("Удалить пользователя БД")
    @Test
    void removeDbmsUser() {
        postgreSQL.checkPreconditionStatusProduct(ProductStatus.CREATED);
        postgreSQL.createDb("createdbforremove4");
        postgreSQL.createDbmsUser("chelikforreset2", "user", "createdbforremove4");
        postgreSQL.removeDbmsUser("chelikforreset2", "createdbforremove4");

        postgreSQL.removeDb("createdbforremove4");

    }

    @Order(7)
    @DisplayName("Перезагрузить")
    @Test
    void restart() {
        postgreSQL.restart();
    }

    @Order(8)
    @DisplayName("Удалить БД")
    @Test
    void removeDb() {
        postgreSQL.createDb("createdbforremove5");
        postgreSQL.removeDb("createdbforremove5");

    }

    @Order(9)
    @DisplayName("Выключить")
    @Test
    void stopSoft() {
        postgreSQL.stopSoft();
        postgreSQL.start();
    }

    @Order(10)
    @DisplayName("Изменить конфигурацию")
    @Test
    void resize() {
        postgreSQL.resize();
    }

    @Order(11)
    @DisplayName("Включить")
    @Test
    void start() {
        postgreSQL.stopHard();
        postgreSQL.start();
    }

    @Order(12)
    @DisplayName("Выключить принудительно")
    @Test
    void stopHard() {
        postgreSQL.stopHard();
    }
}
