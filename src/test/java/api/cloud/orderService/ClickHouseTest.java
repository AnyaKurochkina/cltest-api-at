package api.cloud.orderService;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.orderService.products.ClickHouse;
import org.junit.MarkDelete;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import static core.utils.AssertUtils.assertContains;

@Epic("Продукты")
@Feature("ClickHouse")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("clickhouse"), @Tag("prod")})
public class ClickHouseTest extends Tests {

    @TmsLink("377799")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Создать {0}")
    void create(ClickHouse product, Integer num) {
        //noinspection EmptyTryBlock
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {}
    }

    @TmsLink("")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить ОС {0}")
    void checkActions(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            Assertions.assertTrue(clickHouse.isActionExist("update_os_standalone"));
        }
    }

    @TmsLink("377793")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Расширить {0}")
    void expandMountPoint(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.expandMountPoint();
        }
    }

    @TmsLink("1427551")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль владельца {0}")
    void resetPasswordOwner(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            Assumptions.assumeTrue("dev".equalsIgnoreCase(product.envType()), "Тест включен только для dev среды");
            clickHouse.resetPasswordOwner();
        }
    }

    @TmsLink("391689")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Сбросить пароль customer {0}")
    void resetPasswordCustomer(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.resetPasswordCustomer();
        }
    }

    @Disabled
    @TmsLink("377795")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Перезагрузить {0}")
    void restart(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.restart();
        }
    }

    @TmsLink("711827")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Проверка создания {0}")
    void checkConnectDb(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.checkConnectDb();
        }
    }

    @Disabled
    @TmsLink("377798")
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить {0}")
    void stopSoft(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopSoft();
            clickHouse.start();
        }
    }

    @Disabled
    @TmsLinks({@TmsLink("377796"),@TmsLink("377797")})
    @Tag("actions")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Выключить принудительно/Включить {0}")
    void stopHard(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.stopHard();
            clickHouse.start();
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("377800"),@TmsLink("1427552")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] ТУЗ локальные, создание/удаление локальной УЗ {0}")
    void createUserAccount(ClickHouse product, Integer num) {
        try (ClickHouse cluster = product.createObjectExclusiveAccess()) {
            cluster.createUserAccount("test", "helv1gONd1kINnQe7XsAzqiPPtyq50E0LAA2NX");
            cluster.deleteUserAccount("test");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("377802"),@TmsLink("1427553")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] ТУЗ AD, добавление/удаление {0}")
    void addUserAd(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.addUserAd("user1");
            clickHouse.deleteUserAd("user1");
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("391688"),@TmsLink("1427554")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Группы пользователей AD, добавление/удаление {0}")
    void addGroupAd(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.deleteGroupAd(clickHouse.accessGroup());
            clickHouse.addGroupAd(clickHouse.accessGroup());
        }
    }

    @Tag("actions")
    @TmsLinks({@TmsLink("1427555"),@TmsLink("1427556")})
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Группы прикладных администраторов AD, добавление/удаление {0}")
    void addGroupAdmin(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.deleteGroupAdmin(clickHouse.accessGroup());
            clickHouse.addGroupAdmin(clickHouse.accessGroup());
        }
    }

    @TmsLink("330446")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] AD Проверка создания ВМ {0}")
    void checkCreate(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            assertContains(clickHouse.executeSsh("sudo id"), "root");
        }
    }

    @TmsLink("1653898")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить информацию о сертификатах {0}")
    void updateCertsInfo(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.certsInfo();
        }
    }

    @Disabled
    @TmsLink("1653899")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Обновить сертификаты {0}")
    void updateCerts(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.certsInfo();
        }
    }

    @TmsLink("377794")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "[{1}] Удалить {0}")
    @MarkDelete
    void delete(ClickHouse product, Integer num) {
        try (ClickHouse clickHouse = product.createObjectExclusiveAccess()) {
            clickHouse.deleteObject();
        }
    }
}
