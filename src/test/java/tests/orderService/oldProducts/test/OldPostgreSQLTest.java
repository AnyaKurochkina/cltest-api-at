package tests.orderService.oldProducts.test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.PostgreSQL;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты TEST")
@Feature("PostgreSQL OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgresql"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgreSQLTest extends Tests {
    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";

    final PostgreSQL postgreSQL = PostgreSQL.builder()
            .projectId("proj-juh8ynkvtn")
            .productId("3b3807a6-9ad0-4ca6-930a-a37efffcc605")
            .orderId("ff75d654-c851-4cf0-8acc-95473afe9c36")
            .productName("PostgreSQL")
            .build();

    @Order(1)
    @DisplayName("Расширить PorstgreSQL OLD")
    @Test
    void expandMountPoint() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.expandMountPoint();

    }

    @Order(2)
    @DisplayName("Создать БД PorstgreSQL OLD")
    @Test
    void createDb() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdb1", adminPassword);

        postgreSQL.removeDb("createdb1");

    }

    @Order(3)
    @DisplayName("Создать пользователя БД PorstgreSQL OLD")
    @Test
    void createDbmsUser() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdbforuser2", adminPassword);
        postgreSQL.createDbmsUser("chelik1", "user", "createdbforuser2");

        postgreSQL.removeDbmsUser("chelik1", "createdbforuser2");
        postgreSQL.removeDb("createdbforuser2");
    }

    @Order(4)
    @DisplayName("Сбросить пароль пользователя БД PorstgreSQL OLD")
    @Test
    void resetPassword() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdbforreset3", adminPassword);
        postgreSQL.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgreSQL.resetPassword("chelikforreset1");

        postgreSQL.removeDbmsUser("chelikforreset1", "createdbforreset3");
        postgreSQL.removeDb("createdbforreset3");

    }

    @Order(5)
    @DisplayName("Сбросить пароль владельца БД PorstgreSQL OLD")
    @Test
    void resetDbOwnerPassword() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdbforreset8", adminPassword);
        postgreSQL.resetDbOwnerPassword("createdbforreset8");

        postgreSQL.removeDb("createdbforreset8");
    }

    @Order(6)
    @DisplayName("Удалить пользователя БД PorstgreSQL OLD")
    @Test
    void removeDbmsUser() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdbforremove4", adminPassword);
        postgreSQL.createDbmsUser("chelikforreset2", "user", "createdbforremove4");
        postgreSQL.removeDbmsUser("chelikforreset2", "createdbforremove4");

        postgreSQL.removeDb("createdbforremove4");

    }

    @Order(7)
    @DisplayName("Перезагрузить PorstgreSQL OLD")
    @Test
    void restart() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.restart();
    }

    @Order(8)
    @DisplayName("Удалить БД")
    @Test
    void removeDb() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.createDb("createdbforremove5", adminPassword);
        postgreSQL.removeDb("createdbforremove5");

    }

    @Order(9)
    @DisplayName("Выключить PorstgreSQL OLD")
    @Test
    void stopSoft() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.stopSoft();
    }

    @Order(10)
    @DisplayName("Изменить конфигурацию PorstgreSQL OLD")
    @Test
    void resize() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.resize(postgreSQL.getMaxFlavor());
        postgreSQL.resize(postgreSQL.getMinFlavor());
    }

    @Order(11)
    @DisplayName("Включить PorstgreSQL OLD")
    @Test
    void start() {
        if (postgreSQL.productStatusIs(STARTED)) {
            postgreSQL.stopHard();
        }
        postgreSQL.start();
    }

    @Order(12)
    @DisplayName("Выключить принудительно PorstgreSQL OLD")
    @Test
    void stopHard() {
        if (postgreSQL.productStatusIs(STOPPED)) {
            postgreSQL.start();
        }
        postgreSQL.stopHard();
    }
}
