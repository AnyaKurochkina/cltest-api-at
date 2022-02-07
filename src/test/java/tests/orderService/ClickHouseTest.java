package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ClickHouse;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("ClickHouse")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("clickhouse"), @Tag("prod")})
public class ClickHouseTest extends Tests {

    @TmsLink("377799")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(ClickHouse product) {
        //noinspection EmptyTryBlock
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("377793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
            clickHouse.expandMountPoint();
        }
    }

    //    @TmsLink("377800")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Добавить БД {0}")
//    void createDb(ClickHouse product) {
//        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
//            clickHouse.createDb("cached_bd");
//        }
//    }

    //    @TmsLink("377802")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Добавить пользователя {0}")
//    void createDbmsUser(ClickHouse product) {
//        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
//            clickHouse.createDb("cached_bd");
//            clickHouse.createDbmsUser("chelik1", "user", "cached_bd");
//        }
//    }

//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Сбросить пароль {0}")
//    void resetPassword(ClickHouse product) {
//        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
//            clickHouse.createDb("cached_bd");
//            clickHouse.createDbmsUser("chelikforreset1", "user", "cached_bd");
//            clickHouse.resetPassword("chelikforreset1");
//        }
//    }

    @TmsLink("377689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPasswordOwner(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPassword("username_created");
        }
    }

//    @TmsLink("")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Удалить пользователя {0}")
//    void removeDbmsUser(ClickHouse product) {
//        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
//            clickHouse.createDb("cached_bd");
//            clickHouse.createDbmsUser("chelikforreset2", "user", "cached_bd");
//            clickHouse.removeDbmsUser("chelikforreset2", "cached_bd");
//        }
//    }

    @TmsLink("377795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
            clickHouse.restart();
        }
    }

//    @TmsLink("377688")
//    @Tag("actions")
//    @Source(ProductArgumentsProvider.PRODUCTS)
//    @ParameterizedTest(name = "Удалить БД {0}")
//    void removeDb(ClickHouse product) {
//        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
//            clickHouse.createDb("cached_bd");
//            clickHouse.removeDb("cached_bd");
//        }
//    }

    @TmsLink("377798")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
            clickHouse.stopSoft();
            clickHouse.start();
        }
    }

    @TmsLink("377797")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @TmsLink("377796")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkPreconditionStatusProduct(ProductStatus.CREATED);
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @TmsLink("377794")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.deleteObject();
        }
    }
}
