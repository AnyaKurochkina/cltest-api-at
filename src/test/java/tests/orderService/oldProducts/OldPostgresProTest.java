package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.PostgresPro;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("PostgresPRO OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_postgrespro"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldPostgresProTest extends Tests {

    PostgresPro postgresPro = PostgresPro.builder()
            .projectId("proj-67nljbzjtt")
            .productId("0da09981-c1ac-45b6-ba3b-7bfe52fd45bc")
            .orderId("fff78d59-f584-4fec-a93e-a258b4b68240")
            .productName("PostgresPro")
            .build();

    @Order(1)
    @DisplayName("Расширить PostgresPRO OLD")
    @Test
    void expandMountPoint() {
        try {
            postgresPro.start();
        } finally {
            postgresPro.expandMountPoint();
        }
    }

    @Order(2)
    @DisplayName("Добавить БД PostgresPRO OLD")
    @Test
    void createDb() {
        postgresPro.createDb("createdb1");

        postgresPro.removeDb("createdb1");
    }

    @Order(3)
    @DisplayName("Добавить пользователя PostgresPRO OLD")
    @Test
    void createDbmsUser() {
        postgresPro.createDb("createdbforuser2");
        postgresPro.createDbmsUser("chelik1", "user", "createdbforuser2");

        postgresPro.removeDbmsUser("chelik1", "createdbforuser2");
        postgresPro.removeDb("createdbforuser2");
    }

    @Order(4)
    @DisplayName("Сбросить пароль PostgresPRO OLD")
    @Test
    void resetPassword() {
        postgresPro.createDb("createdbforreset3");
        postgresPro.createDbmsUser("chelikforreset1", "user", "createdbforreset3");
        postgresPro.resetPassword("chelikforreset1");

        postgresPro.removeDbmsUser("chelikforreset1", "createdbforreset3");
        postgresPro.removeDb("createdbforreset3");
    }

    @Order(5)
    @DisplayName("Сбросить пароль владельца PostgresPRO OLD")
    @Test
    void resetDbOwnerPassword() {
        postgresPro.createDb("createdbforreset8");
        postgresPro.resetDbOwnerPassword("createdbforreset8");

        postgresPro.removeDb("createdbforreset8");
    }

    @Order(6)
    @DisplayName("Удалить пользователя PostgresPRO OLD")
    @Test
    void removeDbmsUser() {
        postgresPro.createDb("createdbforreset4");
        postgresPro.createDbmsUser("chelikforreset2", "user", "createdbforreset4");
        postgresPro.removeDbmsUser("chelikforreset2", "createdbforreset4");

        postgresPro.removeDb("createdbforreset4");
    }

    @Order(7)
    @DisplayName("Перезагрузить PostgresPRO OLD")
    @Test
    void restart() {
        postgresPro.restart();
    }

    @Order(8)
    @DisplayName("Удалить БД PostgresPRO Old")
    @Test
    void removeDb() {
        postgresPro.createDb("createdbforremove5");
        postgresPro.removeDb("createdbforremove5");
    }

    @Order(9)
    @DisplayName("Выключить PostgresPRO OLD")
    @Test
    void stopSoft() {
        postgresPro.stopSoft();
        postgresPro.start();
    }

    @Order(10)
    @DisplayName("Изменить конфигурацию PostgresPRO Old")
    @Test
    void resize() {
        postgresPro.resize();
    }

    @Order(11)
    @DisplayName("Включить PostgresPRO OLD")
    @Test
    void start() {
        postgresPro.stopHard();
        postgresPro.start();
    }

    @Order(12)
    @DisplayName("Выключить принудительно PostgresPRO OLD")
    @Test
    void stopHard() {
        postgresPro.stopHard();
    }
}
