package api.cloud.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouse;
import models.cloud.orderService.products.ClickHouseCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import api.Tests;

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
            clickHouse.expandMountPoint();
        }
    }

    @TmsLink("1427551")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль владельца {0}")
    void resetPasswordOwner(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPasswordOwner();
        }
    }

    @TmsLink("391689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Сбросить пароль customer {0}")
    void resetPasswordCustomer(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPasswordCustomer();
        }
    }

    @Disabled
    @TmsLink("377795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Перезагрузить {0}")
    void restart(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.restart();
        }
    }

    @TmsLink("711827")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Проверка создания {0}")
    void checkConnectDb(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkConnectDb();
        }
    }

    @Disabled
    @TmsLink("377798")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить {0}")
    void stopSoft(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopSoft();
            clickHouse.start();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("377796"),@TmsLink("377797")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Выключить принудительно/Включить {0}")
    void stopHard(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("377800"),@TmsLink("1427552")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "ТУЗ локальные, создание/удаление локальной УЗ {0}")
    void createUserAccount(ClickHouse product) {
        try (ClickHouse cluster = product.createObjectExclusiveAccess()) {
            cluster.createUserAccount("test", "qBZ7hUOija_gSSyOEt7rA-.nk-x.R4UzdJvrv8y1JJk.lpp");
            cluster.deleteUserAccount("test");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("377802"),@TmsLink("1427553")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "ТУЗ AD, добавление/удаление {0}")
    void addUserAd(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.addUserAd("user1");
            clickHouse.deleteUserAd("user1");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("391688"),@TmsLink("1427554")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Группы пользователей AD, добавление/удаление {0}")
    void addGroupAd(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(clickHouse.getProjectId()).build().createObject();
            clickHouse.deleteGroupAd(accessGroup.getPrefixName());
            clickHouse.addGroupAd(accessGroup.getPrefixName());
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1427555"),@TmsLink("1427556")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "Группы прикладных администраторов AD, добавление/удаление {0}")
    void addGroupAdmin(ClickHouse product) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            AccessGroup accessGroup = AccessGroup.builder().projectName(clickHouse.getProjectId()).build().createObject();
            clickHouse.deleteGroupAdmin(accessGroup.getPrefixName());
            clickHouse.addGroupAdmin(accessGroup.getPrefixName());
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
