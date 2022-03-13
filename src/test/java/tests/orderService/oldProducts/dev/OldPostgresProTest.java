package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.PostgresPro;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("PostgresPRO OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgrespro"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgresProTest extends Tests {
    static final String adminPassword = "KZnFpbEUd6xkJHocD6ORlDZBgDLobgN80I.wNUBjHq";

    final PostgresPro postgresPro = PostgresPro.builder()
            .projectId("proj-67nljbzjtt")
            .productId("0da09981-c1ac-45b6-ba3b-7bfe52fd45bc")
            .orderId("b778270e-a5a6-49df-8de7-4831b19fd4b8")
            .productName("PostgresPro")
            .build();

    @Order(1)
    @DisplayName("Расширить PostgresPRO OLD")
    @Test
    void expandMountPoint() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Добавить БД PostgresPRO OLD")
    @Test
    void createDb() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdb1", adminPassword);

        postgresPro.removeDb("createdb1");
    }

    @Order(3)
    @DisplayName("Проверить подключение к БД PostgresPRO OLD")
    @Test
    void checkBdConnection() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        String db = "bd_for_check_connection";
        postgresPro.createDb(db, adminPassword);
        postgresPro.checkConnection(db, adminPassword);
        postgresPro.removeDb(db);
    }


    @Order(4)
    @DisplayName("Добавить пользователя PostgresPRO OLD")
    @Test
    void createDbmsUser() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdbforuser2", adminPassword);
        postgresPro.createDbmsUser("chelik1", "user", "createdbforuser2");

        postgresPro.removeDbmsUser("chelik1", "createdbforuser2");
        postgresPro.removeDb("createdbforuser2");
    }

    @Order(5)
    @DisplayName("Сбросить пароль PostgresPRO OLD")
    @Test
    void resetPassword() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdbforreset3", adminPassword);
        postgresPro.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgresPro.resetPassword("chelikforreset1");

        postgresPro.removeDbmsUser("chelikforreset1", "createdbforreset3");
        postgresPro.removeDb("createdbforreset3");
    }

    @Order(6)
    @DisplayName("Сбросить пароль владельца PostgresPRO OLD")
    @Test
    void resetDbOwnerPassword() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdbforreset8", adminPassword);
        postgresPro.resetDbOwnerPassword("createdbforreset8");

        postgresPro.removeDb("createdbforreset8");
    }

    @Order(7)
    @DisplayName("Удалить пользователя PostgresPRO OLD")
    @Test
    void removeDbmsUser() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdbforreset4", adminPassword);
        postgresPro.createDbmsUser("chelikforreset2", "user", "createdbforreset4");
        postgresPro.removeDbmsUser("chelikforreset2", "createdbforreset4");

        postgresPro.removeDb("createdbforreset4");
    }

    @Order(8)
    @DisplayName("Перезагрузить PostgresPRO OLD")
    @Test
    void restart() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.restart();
    }

    @Order(9)
    @DisplayName("Удалить БД PostgresPRO Old")
    @Test
    void removeDb() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.createDb("createdbforremove5", adminPassword);
        postgresPro.removeDb("createdbforremove5");
    }

    @Order(10)
    @DisplayName("Выключить PostgresPRO OLD")
    @Test
    void stopSoft() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.stopSoft();
    }

    @Tag("resize")
    @Order(11)
    @DisplayName("Изменить конфигурацию PostgresPRO Old")
    @Test
    void resize() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.resize(postgresPro.getMaxFlavor());
        postgresPro.resize(postgresPro.getMinFlavor());
    }

    @Order(12)
    @DisplayName("Включить PostgresPRO OLD")
    @Test
    void start() {
        if (postgresPro.productStatusIs(STARTED)) {
            postgresPro.stopHard();
        }
        postgresPro.start();
    }

    @Order(13)
    @DisplayName("Выключить принудительно PostgresPRO OLD")
    @Test
    void stopHard() {
        if (postgresPro.productStatusIs(STOPPED)) {
            postgresPro.start();
        }
        postgresPro.stopHard();
    }
}
