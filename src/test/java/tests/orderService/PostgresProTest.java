package tests.orderService;

import org.junit.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.PostgresPro;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;

@Epic("Продукты")
@Feature("PostgresPro")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("postgresPro"), @Tag("prod")})
public class PostgresProTest extends Tests {

    @TmsLink("392138")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Создать {0}")
    void create(PostgresPro product) {
        //noinspection EmptyTryBlock
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("392131")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Расширить {0}")
    void expandMountPoint(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.expandMountPoint();
        }
    }

    @TmsLink("392139")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить БД {0}")
    void createDb(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
        }
    }

    @TmsLink("392140")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Добавить пользователя {0}")
    void createDbmsUser(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
            postgresPro.createDbmsUser("chelik1", "user", "cached_bd");
        }
    }

    @TmsLink("392133")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль {0}")
    void resetPassword(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
            postgresPro.createDbmsUser("chelikforreset1", "user", "cached_bd");
            postgresPro.resetPassword("chelikforreset1");
        }
    }

    @TmsLink("461753")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetDbOwnerPassword(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
            postgresPro.resetDbOwnerPassword("cached_bd");
        }
    }

    @TmsLink("392141")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить пользователя {0}")
    void removeDbmsUser(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
            postgresPro.createDbmsUser("chelikforreset2", "user", "cached_bd");
            postgresPro.removeDbmsUser("chelikforreset2", "cached_bd");
        }
    }

    @TmsLink("392134")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.restart();
        }
    }

    @TmsLink("392142")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить БД {0}")
    void removeDb(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.createDb("cached_bd");
            postgresPro.removeDb("cached_bd");
        }
    }

    @TmsLink("392137")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.stopSoft();
            postgresPro.start();
        }
    }

    @TmsLink("653491")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Изменить конфигурацию {0}")
    void resize(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.resize(postgresPro.getMaxFlavor());
        }
    }

    @TmsLink("392136")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Включить {0}")
    void start(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.stopHard();
            postgresPro.start();
        }
    }

    @TmsLink("392135")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно {0}")
    void stopHard(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.checkPreconditionStatusProduct(ProductStatus.CREATED);
            postgresPro.stopHard();
            postgresPro.start();
        }
    }

    @TmsLink("392132")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Удалить {0}")
    @MarkDelete
    void delete(PostgresPro product) {
        try (PostgresPro postgresPro = product.createObjectExclusiveAccess()) {
            postgresPro.deleteObject();
        }
    }
}
