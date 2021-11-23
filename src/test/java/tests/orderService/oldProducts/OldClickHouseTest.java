package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ClickHouse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("ClickHouse old")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_clickhouse"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldClickHouseTest extends Tests {

    ClickHouse clickHouse = ClickHouse.builder()
            .projectId("proj-67nljbzjtt")
            .productId("93fbf58f-bf5b-4f2b-a491-3c382c9837fd")
            .orderId("e75ff7b3-5bc9-4c61-a445-b4caba970606")
            .build();

    @Order(1)
    @DisplayName("Расширить clickHouse old")
    @Test
    void expandMountPoint() {
        clickHouse.start();
        clickHouse.expandMountPoint();
    }

    @Order(2)
    @Test
    @DisplayName("Добавить БД clickHouse old")
    void createDb() {
        clickHouse.createDb("db_1");

        clickHouse.removeDb("db_1");

    }

    @Order(3)
    @Test
    @DisplayName("Добавить пользователя clickHouse old")
    void createDbmsUser() {
        clickHouse.createDb("createdbforuser");
        clickHouse.createDbmsUser("chelik1", "user", "createdbforuser");

        clickHouse.removeDb("createdbforuser");
    }

    @Order(4)
    @Test
    @DisplayName("Сбросить пароль clickHouse old")
    void resetPassword() {
        clickHouse.createDb("createdbforreset1");
        clickHouse.createDbmsUser("chelikforreset1", "user", "createdbforreset1");
        clickHouse.resetPassword("chelikforreset1");

        clickHouse.removeDbmsUser("chelikforreset1", "createdbforreset1");
        clickHouse.removeDb("createdbforreset1");
    }

    @Order(5)
    @Test
    @DisplayName("Удалить пользователя clickHouse old")
    void removeDbmsUser() {
        clickHouse.createDb("createdbforreset2");
        clickHouse.createDbmsUser("chelikforreset2", "user", "createdbforreset2");
        clickHouse.removeDbmsUser("chelikforreset2", "createdbforreset2");

        clickHouse.removeDb("createdbforreset2");
    }

    @Order(6)
    @Test
    @DisplayName("Перезагрузить clickHouse old")
    void restart() {
        clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
        clickHouse.restart();
    }

    @Order(7)
    @Test
    @DisplayName("Удалить БД clickHouse old")
    void removeDb() {
        clickHouse.createDb("createdbforremove3");
        clickHouse.removeDb("createdbforremove3");
    }

    @Order(8)
    @Test
    @DisplayName("Выключить clickHouse old")
    void stopSoft() {
        clickHouse.stopSoft();
        clickHouse.start();
    }

    @Order(9)
    @Test
    @DisplayName("Включить clickHouse old")
    void start() {
        clickHouse.stopHard();
        clickHouse.start();
    }

    @Order(10)
    @Test
    @DisplayName("Выключить принудительно clickHouse old")
    void stopHard() {
        clickHouse.stopHard();
    }
}
