package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.ClickHouse;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("ClickHouse OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_clickhouse"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldClickHouseTest extends Tests {

    final ClickHouse clickHouse = ClickHouse.builder()
            .projectId("proj-67nljbzjtt")
            .productId("93fbf58f-bf5b-4f2b-a491-3c382c9837fd")
            .orderId("e75ff7b3-5bc9-4c61-a445-b4caba970606")
            .productName("ClickHouse")
            .build();

    @Order(1)
    @DisplayName("Расширить CLickHouse OLD")
    @Test
    void expandMountPoint() {
        if (clickHouse.productStatusIs(STOPPED)) {
            clickHouse.start();
        }
        clickHouse.expandMountPoint();
    }

//    @Order(2)
//    @Test
//    @DisplayName("Добавить БД CLickHouse OLD")
//    void createDb() {
//        if (clickHouse.productStatusIs(STOPPED)) {
//            clickHouse.start();
//        }
//        clickHouse.createDb("db_1");
//
//        clickHouse.removeDb("db_1");
//
//    }

//    @Order(3)
//    @Test
//    @DisplayName("Добавить пользователя CLickHouse OLD")
//    void createDbmsUser() {
//        if (clickHouse.productStatusIs(STOPPED)) {
//            clickHouse.start();
//        }
//        clickHouse.createDb("createdbforuser");
//        clickHouse.createDbmsUser("chelik1", "user", "createdbforuser");
//
//        clickHouse.removeDb("createdbforuser");
//    }

//    @Order(4)
//    @Test
//    @DisplayName("Сбросить пароль CLickHouse OLD")
//    void resetPassword() {
//        if (clickHouse.productStatusIs(STOPPED)) {
//            clickHouse.start();
//        }
//        clickHouse.createDb("createdbforreset1");
//        clickHouse.createDbmsUser("chelikforreset1", "user", "createdbforreset1");
//        clickHouse.resetPasswordOwner();
//
//        clickHouse.removeDbmsUser("chelikforreset1", "createdbforreset1");
//        clickHouse.removeDb("createdbforreset1");
//    }

//    @Order(5)
//    @Test
//    @DisplayName("Удалить пользователя CLickHouse OLD")
//    void removeDbmsUser() {
//        if (clickHouse.productStatusIs(STOPPED)) {
//            clickHouse.start();
//        }
//        clickHouse.createDb("createdbforreset2");
//        clickHouse.createDbmsUser("chelikforreset2", "user", "createdbforreset2");
//        clickHouse.removeDbmsUser("chelikforreset2", "createdbforreset2");
//
//        clickHouse.removeDb("createdbforreset2");
//    }

    @Order(6)
    @Test
    @DisplayName("Перезагрузить CLickHouse OLD")
    void restart() {
        if (clickHouse.productStatusIs(STOPPED)) {
            clickHouse.start();
        }
        clickHouse.restart();
    }

//    @Order(7)
//    @Test
//    @DisplayName("Удалить БД CLickHouse OLD")
//    void removeDb() {
//        if (clickHouse.productStatusIs(STOPPED)) {
//            clickHouse.start();
//        }
//        clickHouse.createDb("createdbforremove3");
//        clickHouse.removeDb("createdbforremove3");
//    }

    @Order(8)
    @Test
    @DisplayName("Выключить CLickHouse OLD")
    void stopSoft() {
        if (clickHouse.productStatusIs(STOPPED)) {
            clickHouse.start();
        }
        clickHouse.stopSoft();
    }

    @Order(9)
    @Test
    @DisplayName("Включить CLickHouse OLD")
    void start() {
        if (clickHouse.productStatusIs(STARTED)) {
            clickHouse.stopHard();
        }
        clickHouse.start();
    }

    @Order(10)
    @Test
    @DisplayName("Выключить принудительно CLickHouse OLD")
    void stopHard() {
        if (clickHouse.productStatusIs(STOPPED)) {
            clickHouse.start();
        }
        clickHouse.stopHard();
    }
}
